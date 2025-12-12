package tile;

import environment.entity.Entity;
import environment.entity.Player;
import environment.entity.PlayerState;
import environment.food_related.Ingredient;
import environment.food_related.IngredientState;
import environment.item.Item;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class CuttingStation extends Tile {

    public Item itemOnTop = null;

    // Siapa yang sedang menggunakan station ini?
    private Entity activePlayer = null;

    private int currentProgress = 0;
    private final int TIME_TO_CUT = 180; // 3 detik x 60 FPS

    public CuttingStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadStationImage();
    }

    private void loadStationImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cutting_station.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {
        // KASUS 1: Menaruh Item (Syarat: Meja kosong, Tangan player isi)
        if (itemOnTop == null && player.inventory != null) {
            itemOnTop = player.inventory;
            player.inventory = null;
            System.out.println("Menaruh " + itemOnTop.name + " di Cutting Station");
            return;
        }

        // KASUS 2: Interaksi dengan Item di Meja
        if (itemOnTop != null) {

            boolean isRawIngredient = false;
            if (itemOnTop instanceof Ingredient) {
                if (((Ingredient) itemOnTop).state == IngredientState.RAW) {
                    isRawIngredient = true;
                }
            }

            // A. LOGIKA CUTTING (TOGGLE ON/OFF)
            if (isRawIngredient && player.inventory == null) {

                // Jika Station SEDANG DIPAKAI orang lain -> Abaikan
                if (activePlayer != null && activePlayer != player) {
                    System.out.println("Station sedang digunakan chef lain!");
                    return;
                }

                // TOGGLE LOGIC
                if (activePlayer == null) {
                    // --- MULAI MEMOTONG (START) ---
                    startCutting(player);
                } else {
                    // --- BERHENTI MEMOTONG (STOP) ---
                    stopCutting();
                }
            }

            // B. MENGAMBIL ITEM
            else if (player.inventory == null) {
                // Jangan ambil jika sedang dipotong orang lain
                if (activePlayer != null && activePlayer != player) return;

                // Stop dulu kalau sedang jalan
                stopCutting();

                player.inventory = itemOnTop;
                itemOnTop = null;
                currentProgress = 0; // Reset progress saat diambil
                System.out.println("Mengambil item.");
            }
        }
    }

    // --- LOGIKA UPDATE (Jalan Otomatis) ---
    public void update() {
        // Jika ada player yang aktif memotong, jalankan progress
        if (activePlayer != null && itemOnTop != null) {

            // Pastikan player terkunci (BUSY)
            if (activePlayer instanceof Player) {
                ((Player) activePlayer).playerState = PlayerState.BUSY;
            }

            currentProgress++;

            if (currentProgress >= TIME_TO_CUT) {
                finishCutting();
            }
        }
        // Safety: Jika item diambil paksa atau hilang
        else if (activePlayer != null && itemOnTop == null) {
            stopCutting();
        }
    }

    // --- HELPER METHODS ---
    private void startCutting(Entity player) {
        activePlayer = player;
        // currentProgress = 0; // Jangan reset jika ingin melanjutkan sisa potongan
        if (player instanceof Player) {
            ((Player) player).playerState = PlayerState.BUSY;
        }
        System.out.println("Mulai memotong...");
    }

    private void stopCutting() {
        if (activePlayer instanceof Player) {
            ((Player) activePlayer).playerState = PlayerState.IDLE;
        }
        activePlayer = null;
        System.out.println("Berhenti memotong/Pause.");
    }

    private void finishCutting() {
        if (itemOnTop instanceof Ingredient) {
            ((Ingredient) itemOnTop).chop();
        }
        currentProgress = 0;
        stopCutting();
        System.out.println("Selesai memotong!");
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);

        if (itemOnTop != null) {
            g2.drawImage(itemOnTop.image, x + 12, y + 12, gp.itemSize, gp.itemSize, null);

            if (currentProgress > 0) {
                int barWidth = 32;
                int barHeight = 6;
                int screenX = x + 8;
                int screenY = y - 10;

                g2.setColor(Color.RED);
                g2.fillRect(screenX, screenY, barWidth, barHeight);

                int greenBar = (int) (((double)currentProgress / TIME_TO_CUT) * barWidth);
                g2.setColor(Color.GREEN);
                g2.fillRect(screenX, screenY, greenBar, barHeight);

                g2.setColor(Color.BLACK);
                g2.drawRect(screenX, screenY, barWidth, barHeight);

                // Indikator visual jika sedang aktif
                if (activePlayer != null) {
                    g2.setColor(Color.YELLOW);
                    g2.drawRect(screenX - 2, screenY - 2, barWidth + 4, barHeight + 4);
                }
            }
        }
    }
}