package environment;

import main.GamePanel;
import java.awt.*;
import java.util.ArrayList;

public abstract class KitchenUtensil extends Item implements CookingDevice {

    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    public int cookingTime = 0;
    public boolean isCooking = false;
    
    // Konfigurasi Waktu (60 FPS)
    // 12 detik matang, 24 detik gosong (total dari awal)
    protected final int TIME_TO_COOK = 12 * 60; 
    protected final int TIME_TO_BURN = 24 * 60; 

    public KitchenUtensil(GamePanel gp) {
        super(gp);

        solidArea = new Rectangle(0, 0, 16, 16);
        
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public boolean isPortable() {
        return true;
    }

    @Override
    public int capacity() {
        return 1; // Default 1 bahan, bisa diubah jika resep butuh lebih
    }

    @Override
    public void addIngredient(Ingredient ingredient) {
        if (ingredients.size() < capacity() && canAccept(ingredient)) {
            ingredients.add(ingredient);
            System.out.println("Bahan " + ingredient.name + " dimasukkan ke alat masak.");
            startCooking();
        }
    }

    @Override
    public void startCooking() {
        if (!ingredients.isEmpty()) {
            isCooking = true;
        }
    }

    public void update() {
        if (isCooking) {
            cookingTime++;

            // Cek Matang (12 Detik)
            if (cookingTime == TIME_TO_COOK) {
                for (Ingredient i : ingredients) {
                    i.cook(); // Ubah state bahan jadi COOKED
                }
                System.out.println("Makanan Matang!");
            }
            // Cek Gosong (24 Detik)
            else if (cookingTime >= TIME_TO_BURN) {
                for (Ingredient i : ingredients) {
                    i.burn(); // Ubah state bahan jadi BURNED
                }
                isCooking = false; // Stop timer visual, tapi status tetap gosong
                System.out.println("Makanan Gosong!");
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); // Gambar Panci/Wajan

        // Gambar Progress Bar jika sedang memasak
        if (!ingredients.isEmpty()) {
            int barWidth = 24;
            int barHeight = 4;
            int screenX = worldX; // Karena Item pakai world coordinate
            int screenY = worldY - 10;

            // Background Bar (Hitam)
            g2.setColor(Color.BLACK);
            g2.fillRect(screenX, screenY, barWidth, barHeight);

            // Progress Bar Logic
            g2.setColor(Color.GREEN);
            double progress = (double) cookingTime / TIME_TO_COOK;
            
            if (cookingTime >= TIME_TO_COOK) {
                g2.setColor(Color.RED); // Warning mau gosong
                // Progress menuju gosong (dari detik 12 ke 24)
                progress = (double) (cookingTime - TIME_TO_COOK) / (TIME_TO_BURN - TIME_TO_COOK);
            }
            
            if (cookingTime >= TIME_TO_BURN) {
                 g2.setColor(Color.DARK_GRAY); // Sudah gosong
                 progress = 1.0;
            }

            int currentWidth = (int) (barWidth * Math.min(progress, 1.0));
            g2.fillRect(screenX, screenY, currentWidth, barHeight);
        }
        
        // Gambar bahan di atas panci (opsional visual)
        if (!ingredients.isEmpty()) {
            g2.drawImage(ingredients.get(0).image, worldX + 4, worldY - 4, 16, 16, null);
        }
    }
}
