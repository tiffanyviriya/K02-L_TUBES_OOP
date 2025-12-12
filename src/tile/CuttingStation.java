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
    private Entity activePlayer = null;
    private int currentProgress = 0;
    private final int TIME_TO_CUT = 180; // 3 detik

    public CuttingStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadStationImage();
    }

    private void loadStationImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cutting_station.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void interact(Entity player) {
        // KASUS 1: Menaruh Item
        if (itemOnTop == null && player.inventory != null) {
            // Validasi bahan seperti sebelumnya...
            if (player.inventory instanceof Ingredient) {
                Ingredient ing = (Ingredient) player.inventory;
                boolean isCuttable = ing.name.equalsIgnoreCase("shrimp") ||
                        ing.name.equalsIgnoreCase("fish") ||
                        ing.name.equalsIgnoreCase("cucumber");
                if (!isCuttable) {
                    gp.soundM.playSE(6); // Error Sound
                    return;
                }
            } else {
                gp.soundM.playSE(6); // Error jika bukan ingredient
                return;
            }

            itemOnTop = player.inventory;
            player.inventory = null;
            return;
        }

        // KASUS 2: Interaksi (Start/Stop Cutting atau Ambil)
        if (itemOnTop != null) {
            boolean isRaw = false;
            if (itemOnTop instanceof Ingredient) {
                if (((Ingredient) itemOnTop).state == IngredientState.RAW) isRaw = true;
            }

            // A. START / STOP CUTTING
            if (isRaw && player.inventory == null) {
                // Cek apakah station sedang dipakai orang lain?
                if (activePlayer != null && activePlayer != player) {
                    // Jika dipakai orang lain, jangan ganggu
                    return;
                }

                // TOGGLE LOGIC
                if (activePlayer == null) {
                    startCutting(player); // START
                } else {
                    stopCutting(); // STOP
                }
            }

            // B. MENGAMBIL ITEM (Hanya jika progress berhenti/selesai)
            else if (player.inventory == null) {
                // Jangan ambil paksa jika teman sedang memotong
                if (activePlayer != null && activePlayer != player) return;

                stopCutting(); // Pastikan reset state
                player.inventory = itemOnTop;
                itemOnTop = null;
                currentProgress = 0;
            }
        }
    }

    public void update() {
        // Jika ada player yang aktif, jalankan progress
        if (activePlayer != null && itemOnTop != null) {
            // Kunci status player jadi BUSY terus menerus
            if (activePlayer instanceof Player) {
                ((Player) activePlayer).playerState = PlayerState.BUSY;
            }

            currentProgress++;

            if (currentProgress >= TIME_TO_CUT) {
                finishCutting();
            }
        }
        // Safety check: Jika barang diambil tiba-tiba
        else if (activePlayer != null && itemOnTop == null) {
            stopCutting();
        }
    }

    private void startCutting(Entity player) {
        activePlayer = player;
        if (player instanceof Player) ((Player) player).playerState = PlayerState.BUSY;

        gp.soundM.playSELoop(9); // Loop suara cutting
        System.out.println("Start Cutting...");
    }

    private void stopCutting() {
        // Kembalikan player ke IDLE
        if (activePlayer instanceof Player) {
            ((Player) activePlayer).playerState = PlayerState.IDLE;
        }
        activePlayer = null;

        gp.soundM.stopSELoop(); // Stop suara
        System.out.println("Stop Cutting.");
    }

    private void finishCutting() {
        if (itemOnTop instanceof Ingredient) {
            ((Ingredient) itemOnTop).chop();
        }
        currentProgress = 0;
        stopCutting(); // Ini akan stop suara dan bebaskan player
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        if (itemOnTop != null) {
            g2.drawImage(itemOnTop.image, x + 12, y + 12, gp.itemSize, gp.itemSize, null);
            if (currentProgress > 0) {
                int barWidth = 32;
                int screenX = x + 8;
                int screenY = y - 10;
                g2.setColor(Color.RED); g2.fillRect(screenX, screenY, barWidth, 6);
                int greenBar = (int) (((double)currentProgress / TIME_TO_CUT) * barWidth);
                g2.setColor(Color.GREEN); g2.fillRect(screenX, screenY, greenBar, 6);
                g2.setColor(Color.BLACK); g2.drawRect(screenX, screenY, barWidth, 6);
            }
        }
    }
}