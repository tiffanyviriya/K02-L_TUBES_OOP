package tile;

import environment.Entity;
import environment.Ingredient;
import environment.IngredientState;
import environment.Item;
import environment.KitchenUtensil;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class CookingStation extends Tile implements Runnable {

    public KitchenUtensil utensilOnStation = null;
    private Thread cookingThread;
    private boolean isCooking = false;

    // Konfigurasi Waktu (Dalam detik * FPS atau Milidetik)
    // Disini kita pakai sleep 1 detik di thread, jadi hitungannya detik murni
    private final int TIME_TO_COOK = 12;
    private final int TIME_TO_BURN = 24;

    public CookingStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadStationImage();
    }

    private void loadStationImage() {
        try {
            // Gunakan gambar kompor
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {

        // KASUS 1: Station Kosong, Player bawa Alat Masak (Pan/Pot)
        if (utensilOnStation == null) {
            if (player.inventory instanceof KitchenUtensil) {
                utensilOnStation = (KitchenUtensil) player.inventory;
                player.inventory = null;
                System.out.println("Menaruh " + utensilOnStation.name + " di kompor.");

                // Cek jika alat masak sudah ada isinya, langsung lanjutkan masak
                checkAndStartCooking();
            }
        }

        // KASUS 2: Station Ada Alat Masak
        else {
            // Sub-kasus A: Player bawa Ingredient (RAW/CHOPPED) -> Masukkan ke Panci
            if (player.inventory instanceof Ingredient) {
                Ingredient in = (Ingredient) player.inventory;

                // Validasi: Hanya terima RAW atau CHOPPED
                if (in.state == IngredientState.RAW || in.state == IngredientState.CHOPPED) {
                    utensilOnStation.addIngredient(in);
                    player.inventory = null;
                    System.out.println("Memasukkan " + in.name + " ke dalam " + utensilOnStation.name);

                    // TRIGGER: Otomatis mulai masak saat bahan masuk
                    checkAndStartCooking();
                }
            }

            // Sub-kasus B: Player tangan kosong -> Angkat Panci
            else if (player.inventory == null) {
                // Stop thread masak dulu sebelum diangkat
                stopCooking();

                player.inventory = utensilOnStation;
                utensilOnStation = null;
                System.out.println("Mengangkat alat masak.");
            }
        }
    }

    // --- LOGIKA CONCURRENCY (THREAD) ---

    public void checkAndStartCooking() {
        // Mulai thread hanya jika:
        // 1. Ada alat masak
        // 2. Ada bahan di dalamnya
        // 3. Thread belum jalan
        if (utensilOnStation != null && !utensilOnStation.ingredients.isEmpty() && !isCooking) {
            startCookingThread();
        }
    }

    public void startCookingThread() {
        isCooking = true;
        cookingThread = new Thread(this);
        cookingThread.start();
        System.out.println("Proses memasak dimulai (Thread Start)...");
    }

    public void stopCooking() {
        isCooking = false;
        // Thread akan berhenti otomatis karena loop while(isCooking) akan false
    }

    @Override
    public void run() {
        while (isCooking && utensilOnStation != null) {
            try {
                // Sleep 1 detik
                Thread.sleep(1000);

                // Cek ulang (takutnya panci diangkat saat sleep)
                if (utensilOnStation == null) break;

                // Update Progress di Utensil
                utensilOnStation.cookingProgress++;
                int time = utensilOnStation.cookingProgress;

                System.out.println("Cooking Timer: " + time + "s");

                // --- LOGIKA STATE ---
                // Loop semua bahan dalam panci
                for (Ingredient in : utensilOnStation.ingredients) {

                    // Detik ke-12: Matang
                    if (time == TIME_TO_COOK) {
                        in.cook(); // Ubah state jadi COOKED
                        System.out.println(in.name + " Matang!");
                    }

                    // Detik ke-24: Gosong
                    else if (time >= TIME_TO_BURN) {
                        in.burn(); // Ubah state jadi BURNED
                        System.out.println(in.name + " GOSONG!");
                        // Opsional: Stop cooking jika gosong
                        // stopCooking();
                    }
                }

                // Repaint UI (opsional, agar bar update realtime)
                // gp.repaint();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // --- VISUALISASI ---
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Kompor
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);

        // 2. Gambar Alat Masak & Isinya
        if (utensilOnStation != null) {
            // Gambar Panci
            g2.drawImage(utensilOnStation.image, x + 4, y + 4, 40, 40, null);

            // Gambar Bahan (ambil bahan pertama sebagai representasi visual)
            if (!utensilOnStation.ingredients.isEmpty()) {
                g2.drawImage(utensilOnStation.ingredients.get(0).image, x + 12, y + 12, 24, 24, null);
            }

            // 3. Gambar Timer Bar
            if (utensilOnStation.cookingProgress > 0) {
                int barWidth = 32;
                int screenX = x + 8;
                int screenY = y - 10;

                // Background Bar
                g2.setColor(Color.WHITE);
                g2.fillRect(screenX, screenY, barWidth, 6);

                // Logic Warna Bar
                if (utensilOnStation.cookingProgress < TIME_TO_COOK) {
                    g2.setColor(Color.ORANGE); // Sedang Masak
                } else if (utensilOnStation.cookingProgress < TIME_TO_BURN) {
                    g2.setColor(Color.GREEN); // Matang (Segera angkat!)
                } else {
                    g2.setColor(Color.BLACK); // Gosong
                }

                // Panjang Bar (Scaling max 24 detik)
                int fillWidth = (int) (((double)utensilOnStation.cookingProgress / TIME_TO_BURN) * barWidth);
                if (fillWidth > barWidth) fillWidth = barWidth;

                g2.fillRect(screenX, screenY, fillWidth, 6);
                g2.setColor(Color.BLACK);
                g2.drawRect(screenX, screenY, barWidth, 6);
            }
        }
    }
}