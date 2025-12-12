package tile;

import environment.entity.Entity;
import environment.entity.Player;
import environment.entity.PlayerState;
import environment.food_related.Ingredient;
import environment.food_related.IngredientState;
import environment.item.Item;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

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
        // KASUS 1: Meja Ada Item
        if (itemOnTop != null) {

            // Cek apakah item sedang dipotong?
            if (activePlayer != null && activePlayer != player) {
                System.out.println("Station sedang digunakan chef lain!");
                return;
            }

            // --- LOGIKA BARU: Tuang Panci ke Piring di Meja ---
            if (itemOnTop instanceof Plate && player.inventory instanceof KitchenUtensil) {
                // Stop cutting jika ada progress gantung (safety)
                if (activePlayer == player) stopCutting();

                Plate plate = (Plate) itemOnTop;
                KitchenUtensil utensil = (KitchenUtensil) player.inventory;

                ArrayList<Ingredient> food = utensil.serveToPlate();

                if (food != null) {
                    for (Ingredient i : food) {
                        plate.addItem(i);
                    }
                    System.out.println("Plating: Makanan dari " + utensil.name + " dituang ke Piring di Cutting Station.");
                } else {
                    System.out.println("Gagal: Makanan belum matang atau gosong.");
                }
                return;
            }
            // --------------------------------------------------

            boolean isRawIngredient = false;
            if (itemOnTop instanceof Ingredient) {
                if (((Ingredient) itemOnTop).state == IngredientState.RAW) {
                    isRawIngredient = true;
                }
            }

            // A. LOGIKA CUTTING
            if (isRawIngredient && player.inventory == null) {
                if (activePlayer == null) {
                    startCutting(player);
                } else {
                    stopCutting();
                }
                return;
            }

            // B. LOGIKA ASSEMBLY LAINNYA

            // 1. Player bawa Piring -> Plating (Ambil bahan dari meja)
            if (player.inventory instanceof Plate) {
                if (activePlayer == player) stopCutting();

                Plate plate = (Plate) player.inventory;
                if (itemOnTop instanceof Ingredient) {
                    plate.addItem((Ingredient) itemOnTop);
                    itemOnTop = null;
                }
            }

            // 2. Player bawa Kitchen Utensil -> Masukkan bahan dari meja ke panci
            else if (player.inventory instanceof KitchenUtensil) {
                if (activePlayer == player) stopCutting();

                KitchenUtensil utensil = (KitchenUtensil) player.inventory;
                if (itemOnTop instanceof Ingredient) {
                    Ingredient ing = (Ingredient) itemOnTop;
                    if (ing.canBeCooked() && !utensil.isCooked && !utensil.isBurned) {
                        utensil.addIngredient(ing);
                        itemOnTop = null;
                    }
                }
            }

            // 3. Player bawa Ingredient -> Gabung ke Piring di Meja
            else if (player.inventory instanceof Ingredient && itemOnTop instanceof Plate) {
                ((Plate) itemOnTop).addItem((Ingredient) player.inventory);
                player.inventory = null;
            }

            // 4. Player Tangan Kosong -> Ambil Item
            else if (player.inventory == null) {
                stopCutting();
                player.inventory = itemOnTop;
                itemOnTop = null;
                currentProgress = 0;
            }
        }

        // KASUS 2: Meja Kosong -> Taruh Item
        else {
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
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
            // [PERBAIKAN] Logika Centering Dinamis

            if (itemOnTop instanceof KitchenUtensil) {
                // Utensil digambar dengan ukuran (tileSize - 16) di kelasnya
                // Maka offset agar ke tengah = 8
                int offset = 8;
                itemOnTop.draw(g2, x + offset, y + offset);
            } else {
                // Item biasa digambar dengan itemSize (24)
                // Offset = (LebarTile - LebarItem) / 2
                int centerOffset = (gp.tileSize - gp.itemSize) / 2;

                // Gunakan itemOnTop.draw() agar properti worldX/Y di item ikut terupdate
                itemOnTop.draw(g2, x + centerOffset, y + centerOffset);
            }

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

                if (activePlayer != null) {
                    g2.setColor(Color.YELLOW);
                    g2.drawRect(screenX - 2, screenY - 2, barWidth + 4, barHeight + 4);
                }
            }
        }
    }
}