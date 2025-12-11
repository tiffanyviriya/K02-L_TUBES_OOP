package environment;

import main.GamePanel;
import java.awt.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;

public class KitchenUtensil extends Item {

    // Bahan-bahan di dalam panci
    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    
    // Status Memasak
    public int cookingProgress = 0;
    public final int TIME_TO_COOK = 300; // 5 Detik (60 FPS * 5)
    public final int TIME_TO_BURN = 600; // 10 Detik gosong
    
    public boolean isCooked = false;
    public boolean isBurned = false;

    public KitchenUtensil(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.collision = true; // Bisa ditabrak/diambil
        loadUtensilImage();
    }

    private void loadUtensilImage() {
        try {
            // Default image (Panci Kosong)
            image = ImageIO.read(getClass().getResourceAsStream("/sprites/Utensils/Boiling_Pot.png")); 
            // Pastikan path gambarnya sesuai folder kamu
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addIngredient(Ingredient in) {
        // Hanya bisa tambah bahan jika belum matang/gosong
        if (!isCooked && !isBurned) {
            ingredients.add(in);
            System.out.println("Bahan " + in.name + " masuk ke " + name);
        }
    }

    // Method ini dipanggil oleh CookingStation setiap frame
    public void cook() {
        // Hanya masak jika ada bahan
        if (!ingredients.isEmpty()) {
            cookingProgress++;

            // Update State Bahan
            if (cookingProgress >= TIME_TO_BURN) {
                isBurned = true;
                isCooked = false;
                for(Ingredient i : ingredients) i.burn(); // Ubah visual bahan jadi gosong
            } 
            else if (cookingProgress >= TIME_TO_COOK) {
                isCooked = true;
                for(Ingredient i : ingredients) i.cook(); // Ubah visual bahan jadi matang
            }
        }
    }
    
    // Method untuk menuang isi panci ke piring
    public ArrayList<Ingredient> serveToPlate() {
        if (isCooked && !isBurned) {
            ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
            
            // Reset Panci setelah dituang
            ingredients.clear();
            cookingProgress = 0;
            isCooked = false;
            
            return servedFood;
        }
        return null; // Tidak bisa dituang jika belum matang atau gosong
    }

    // Visualisasi Panci + Bar Progress + Bahan di dalamnya
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Panci
        super.draw(g2, x, y);

        // 2. Gambar Bahan di dalam Panci (Kecil di tengah)
        if (!ingredients.isEmpty()) {
            // Ambil bahan pertama sebagai representasi
            g2.drawImage(ingredients.get(0).image, x + 10, y + 10, 20, 20, null);
        }

        // 3. Gambar Progress Bar (Hanya jika sedang ada isinya)
        if (!ingredients.isEmpty()) {
            int barWidth = 32;
            int barHeight = 5;
            int screenX = x + 8;
            int screenY = y - 10;

            // Background Putih
            g2.setColor(Color.WHITE);
            g2.fillRect(screenX, screenY, barWidth, barHeight);

            // Logic Warna Bar
            if (isBurned) g2.setColor(Color.BLACK);
            else if (isCooked) g2.setColor(Color.GREEN); // Matang!
            else g2.setColor(Color.ORANGE); // Sedang masak

            // Hitung panjang bar berdasarkan progress (Max sampai gosong)
            double ratio = (double) cookingProgress / TIME_TO_BURN;
            if (ratio > 1) ratio = 1;
            
            g2.fillRect(screenX, screenY, (int)(barWidth * ratio), barHeight);
            
            // Border
            g2.setColor(Color.BLACK);
            g2.drawRect(screenX, screenY, barWidth, barHeight);
        }
    }
}
