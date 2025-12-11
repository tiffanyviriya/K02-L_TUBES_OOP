package tile;

import environment.Entity;
import environment.Item;
import environment.Plate;
import main.GamePanel;
import environment.Player;
import main.PlayerState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Stack;
// Import Executor
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WashingStation extends Tile implements Runnable {

    private int myCol, myRow;
    private Stack<Item> dirtyStack = new Stack<>();

    // --- CONCURRENCY via EXECUTOR ---
    private ScheduledFuture<?> washingTask;

    // --- LOGIC VARIABLES ---
    // GANTI: Tidak pakai lastInteractionTime, tapi simpan referensi player aktif
    private Entity activePlayer = null;

    private volatile int currentProgress = 0;
    private final int MAX_PROGRESS = 100;

    public WashingStation(GamePanel gp, int col, int row) {
        super(gp);
        this.myCol = col;
        this.myRow = row;
        this.collision = true;
        tryingOut(); // Mempertahankan method debug Anda
        loadImage();

        // PENTING: Mendaftar ke Executor Global
        startWashingTask();
    }

    private void tryingOut(){
        // Menambahkan piring dummy untuk test
        dirtyStack.add(new Plate(gp));
    }

    private void loadImage() {
        try {
            // Menggunakan path sesuai kode Anda
            var is = getClass().getResourceAsStream("/tiles/OOPTile.png");
            if (is != null) image = ImageIO.read(is);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void startWashingTask() {
        if (gp.globalExecutor != null && !gp.globalExecutor.isShutdown()) {
            washingTask = gp.globalExecutor.scheduleAtFixedRate(this, 0, 20, TimeUnit.MILLISECONDS);
        }
    }

    // --- LOGIKA UTAMA (Berjalan di Background Thread) ---
    @Override
    public void run() {
        try {
            // Cek apakah ada player yang sedang aktif mencuci
            if (activePlayer != null && !dirtyStack.isEmpty()) {

                // SAFETY CHECK: Cek jarak player
                // Jika player berjalan menjauh, otomatis berhenti mencuci
                if (!isPlayerClose(activePlayer)) {
                    stopWashing();
                    return;
                }

                // Tambah Progress
                currentProgress++;

                // Pastikan state player tetap BUSY selama mencuci agar animasi mainkan
                if (activePlayer instanceof Player) {
                    ((Player) activePlayer).playerState = PlayerState.BUSY;
                }

                // Jika selesai 1 item
                if (currentProgress >= MAX_PROGRESS) {
                    attemptTransferPlate();
                }

            } else if (activePlayer != null && dirtyStack.isEmpty()) {
                // Jika stack habis saat sedang mencuci, otomatis berhenti
                stopWashing();
            }

            // Jika activePlayer == null, thread ini idle (pausing)

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- HELPER METHODS ---

    private void startWashing(Entity player) {
        this.activePlayer = player;
        if (player instanceof Player) {
            ((Player) player).playerState = PlayerState.BUSY;
        }
        System.out.println("Mulai mencuci otomatis...");
    }

    private void stopWashing() {
        if (activePlayer != null) {
            if (activePlayer instanceof Player) {
                ((Player) activePlayer).playerState = PlayerState.IDLE;
            }
            System.out.println("Berhenti mencuci.");
            this.activePlayer = null;
        }
    }

    private boolean isPlayerClose(Entity p) {
        int tileX = myCol * gp.tileSize;
        int tileY = myRow * gp.tileSize;

        // Cek jarak Euclidean sederhana
        double dx = (tileX + gp.tileSize/2.0) - (p.pos.x + gp.tileSize/2.0);
        double dy = (tileY + gp.tileSize/2.0) - (p.pos.y + gp.tileSize/2.0);
        double distance = Math.sqrt(dx*dx + dy*dy);

        // Toleransi jarak (1.5 kotak tile)
        return distance < (gp.tileSize * 1.5);
    }

    public void dispose() {
        if (washingTask != null) {
            washingTask.cancel(true);
        }
    }

    private synchronized void attemptTransferPlate() {
        WashingCounter targetCounter = findNeighborCounter();

        if (targetCounter != null) {
            Item plate = dirtyStack.pop();
            targetCounter.addCleanPlate(plate);
            currentProgress = 0; // Reset progress untuk piring selanjutnya
            System.out.println("Piring bersih dipindahkan ke Station sebelah!");
        } else {
            // Jika penuh atau tidak ada counter, progress mentok
            currentProgress = MAX_PROGRESS;
        }
    }

    private WashingCounter findNeighborCounter() {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        for (int[] dir : directions) {
            int targetCol = myCol + dir[0];
            int targetRow = myRow + dir[1];

            if (targetCol >= 0 && targetCol < gp.maxScreenCol &&
                    targetRow >= 0 && targetRow < gp.maxScreenRow) {

                Tile neighbor = gp.tileM.worldTiles[targetCol][targetRow];
                if (neighbor instanceof WashingCounter) {
                    return (WashingCounter) neighbor;
                }
            }
        }
        return null;
    }

    // --- INTERAKSI PEMAIN (Main Thread) ---
    @Override
    public void interact(Entity player) {
        // KASUS 1: Menaruh Piring Kotor (Selalu bisa dilakukan)
        if (player.inventory != null) {
            dirtyStack.push(player.inventory);
            player.inventory = null;
            System.out.println("Piring kotor ditaruh.");
            return;
        }

        // KASUS 2: Logika Cuci (Toggle Switch)
        if (!dirtyStack.isEmpty()) {
            // Jika belum ada yang mencuci -> MULAI
            if (activePlayer == null) {
                startWashing(player);
            }
            // Jika pemain ini sedang mencuci -> BERHENTI (Cancel manual)
            else if (activePlayer == player) {
                stopWashing();
            }
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        else {
            g2.setColor(Color.CYAN);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }

        if (!dirtyStack.isEmpty()) {
            g2.drawImage(dirtyStack.peek().image, x + 12, y + 10, gp.itemSize, gp.itemSize, null);
            if (dirtyStack.size() > 1) {
                g2.setColor(Color.RED);
                g2.drawString(String.valueOf(dirtyStack.size()), x + 34, y + 42);
            }
        }

        if (currentProgress > 0) {
            int barWidth = (int) (gp.tileSize * 0.8);
            int screenX = x + (gp.tileSize - barWidth) / 2;
            int screenY = y - 10;

            g2.setColor(Color.GRAY);
            g2.fillRect(screenX, screenY, barWidth, 6);

            int fill = (int) (((double) currentProgress / MAX_PROGRESS) * barWidth);
            g2.setColor(Color.BLUE);
            g2.fillRect(screenX, screenY, fill, 6);

            // Indikator visual bahwa sedang aktif (Bingkai Kuning)
            if (activePlayer != null) {
                g2.setColor(Color.YELLOW);
                g2.drawRect(screenX - 1, screenY - 1, barWidth + 2, 7);
            }
        }
    }
}