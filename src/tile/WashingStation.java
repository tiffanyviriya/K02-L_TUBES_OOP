package tile;

import environment.entity.Entity;
import environment.item.Item;
import environment.item.Plate;
import environment.item.PlateState;
import main.util.GamePanel;
import main.util.ItemContainer;
import environment.entity.Player;
import environment.entity.PlayerState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WashingStation extends Tile implements Runnable {

    private int myCol, myRow;

    private ItemContainer<Item> dirtyStack = new ItemContainer<>();

    private ScheduledFuture<?> washingTask;
    private Entity activePlayer = null;
    private volatile int currentProgress = 0;
    private final int MAX_PROGRESS = 100;

    /* Konstruktor untuk inisialisasi stasiun pencucian pada posisi tertentu */
    public WashingStation(GamePanel gp, int col, int row) {
        super(gp);
        this.myCol = col;
        this.myRow = row;
        this.collision = true;
        loadImage();
        startWashingTask();
    }

    /* Memuat gambar visual untuk stasiun pencucian */
    private void loadImage() {
        try {
            var is = getClass().getResourceAsStream("/stations/washingstations.png");
            if (is == null) is = getClass().getResourceAsStream("/tiles/floor_tile.png");
            if (is != null) image = ImageIO.read(is);
        } catch (IOException e) {  }
    }

    /* Memulai thread terjadwal untuk menangani logika pencucian secara periodik */
    private void startWashingTask() {
        if (gp.globalExecutor != null && !gp.globalExecutor.isShutdown()) {
            washingTask = gp.globalExecutor.scheduleAtFixedRate(this, 0, 20, TimeUnit.MILLISECONDS);
        }
    }

    /* Logika loop utama proses pencucian yang dijalankan oleh thread */
    @Override
    public void run() {
        try {
            if (activePlayer != null && !dirtyStack.isEmpty()) {
                if (!isPlayerClose(activePlayer)) {
                    stopWashing();
                    return;
                }

                currentProgress++;

                if (activePlayer instanceof Player) {
                    ((Player) activePlayer).playerState = PlayerState.BUSY;
                }

                if (currentProgress >= MAX_PROGRESS) {
                    attemptTransferPlate();
                }

            } else if (activePlayer != null && dirtyStack.isEmpty()) {
                stopWashing();
            }
        } catch (Exception e) {

        }
    }

    /* Memulai interaksi pencucian oleh pemain */
    private void startWashing(Entity player) {
        this.activePlayer = player;
        if (player instanceof Player) {
            ((Player) player).playerState = PlayerState.BUSY;
        }
    }

    /* Menghentikan proses pencucian dan mengembalikan status pemain menjadi idle */
    private void stopWashing() {
        if (activePlayer != null) {
            if (activePlayer instanceof Player) {
                ((Player) activePlayer).playerState = PlayerState.IDLE;
            }
            this.activePlayer = null;
        }
    }

    /* Memeriksa apakah pemain berada dalam jarak interaksi yang valid */
    private boolean isPlayerClose(Entity p) {
        int tileX = myCol * gp.tileSize;
        int tileY = myRow * gp.tileSize;
        double dx = (tileX + gp.tileSize/2.0) - (p.pos.x + gp.tileSize/2.0);
        double dy = (tileY + gp.tileSize/2.0) - (p.pos.y + gp.tileSize/2.0);
        double distance = Math.sqrt(dx*dx + dy*dy);
        return distance < (gp.tileSize * 1.5);
    }

    /* Membersihkan task thread saat objek stasiun dihapus */
    public void dispose() {
        if (washingTask != null) {
            washingTask.cancel(true);
        }
    }

    /* Mencoba memindahkan piring yang sudah bersih ke counter sebelah */
    private synchronized void attemptTransferPlate() {
        WashingCounter targetCounter = findNeighborCounter();

        if (targetCounter != null) {
            Plate plate = (Plate) dirtyStack.takeItem();

            if (plate != null) {
                plate.plateState = PlateState.CLEAN;
                plate.updateImage();
                targetCounter.addCleanPlate(plate);
                currentProgress = 0;
            }
        } else {
            currentProgress = MAX_PROGRESS;
        }
    }

    /* Mencari objek WashingCounter yang bersebelahan dengan stasiun ini */
    private WashingCounter findNeighborCounter() {
        int targetCol = myCol - 1;
        int targetRow = myRow;

        if (targetCol >= 0 && targetCol < gp.maxScreenCol &&
                targetRow >= 0 && targetRow < gp.maxScreenRow) {

            Tile neighbor = gp.tileM.worldTiles[targetCol][targetRow];
            if (neighbor instanceof WashingCounter) {
                return (WashingCounter) neighbor;
            }
        }
        return null;
    }

    /* Menangani interaksi pemain: menaruh piring kotor atau memulai/menghentikan pencucian */
    @Override
    public void interact(Entity player) {
        if (player.inventory != null) {
            dirtyStack.addItem(player.inventory);
            player.inventory = null;
            return;
        }

        if (!dirtyStack.isEmpty()) {
            if (activePlayer == null) {
                startWashing(player);
            }
            else if (activePlayer == player) {
                stopWashing();
            }
        }
    }

    /* Menggambar stasiun, tumpukan item, dan progress bar pencucian */
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        else {
            g2.setColor(Color.CYAN);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }

        if (!dirtyStack.isEmpty()) {
            Item topItem = dirtyStack.peekItem();
            if (topItem != null && topItem.image != null) {
                g2.drawImage(topItem.image, x + 12, y + 10, gp.itemSize, gp.itemSize, null);
            }

            if (dirtyStack.size() > 1) {
                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
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

            if (activePlayer != null) {
                g2.setColor(Color.YELLOW);
                g2.drawRect(screenX - 1, screenY - 1, barWidth + 2, 7);
            }
        }
    }
}