package environment;

import main.GamePanel;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class KitchenUtensil extends Item {

    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    
    // Progress Memasak
    public int cookingProgress = 0;
    public final int TIME_TO_COOK = 300; 
    public final int TIME_TO_BURN = 600; 
    
    public boolean isCooked = false;
    public boolean isBurned = false;

    // Cache Gambar agar tidak load berulang kali
    private BufferedImage imgPanEmpty, imgPanShrimp;
    private BufferedImage imgPotEmpty, imgPotRice;

    public KitchenUtensil(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.collision = true;
        
        loadAllImages(); // Load semua kemungkinan gambar di awal
        updateLook();    // Set gambar awal
    }

    private void loadAllImages() {
        try {
            // Load Gambar Wajan
            imgPanEmpty = ImageIO.read(getClass().getResourceAsStream("/Sprites_Overcooked/Sprites_Utensils/Frying_Pan.png"));
            imgPanShrimp = ImageIO.read(getClass().getResourceAsStream("/Sprites_Overcooked/Sprites_Utensils/Frying_Pan_With_Shrimp.png"));

            // Load Gambar Panci
            imgPotEmpty = ImageIO.read(getClass().getResourceAsStream("/Sprites_Overcooked/Sprites_Utensils/Boiling_Pot.png"));
            imgPotRice = ImageIO.read(getClass().getResourceAsStream("/Sprites_Overcooked/Sprites_Utensils/Boiling_Pot_With_Rice.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method logic untuk menentukan gambar mana yang dipakai
    public void updateLook() {
        if (name.equalsIgnoreCase("Pan")) {
            if (hasIngredient("shrimp")) {
                image = imgPanShrimp; // Ganti ke Pan with Shrimp
            } else {
                image = imgPanEmpty;  // Pan Kosong
            }
        } 
        else if (name.equalsIgnoreCase("Pot")) {
            if (hasIngredient("rice")) {
                image = imgPotRice;   // Ganti ke Pot with Rice
            } else {
                image = imgPotEmpty;  // Pot Kosong
            }
        }
    }

    // Helper untuk cek apakah ada bahan tertentu
    private boolean hasIngredient(String ingredientName) {
        for (Ingredient i : ingredients) {
            if (i.name.equalsIgnoreCase(ingredientName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addIngredient(Ingredient in) {
        if (!isCooked && !isBurned) {
            ingredients.add(in);
            System.out.println("Bahan " + in.name + " masuk ke " + name);
            
            // PENTING: Update tampilan setelah bahan masuk
            updateLook();
        }
    }

    public ArrayList<Ingredient> serveToPlate() {
        if (isCooked && !isBurned) {
            ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
            ingredients.clear();
            
            // Reset status
            cookingProgress = 0;
            isCooked = false;
            
            // PENTING: Kembalikan tampilan ke kosong setelah disajikan
            updateLook();
            
            return servedFood;
        }
        return null; 
    }

    public void cook() {
        if (!ingredients.isEmpty()) {
            cookingProgress++;
            
            // Logic State (Gosong/Matang)
            if (cookingProgress >= TIME_TO_BURN) {
                isBurned = true;
                isCooked = false;
                for(Ingredient i : ingredients) i.burn(); 
            } 
            else if (cookingProgress >= TIME_TO_COOK) {
                isCooked = true;
                for(Ingredient i : ingredients) i.cook();
            }
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Utensil (Sudah otomatis berubah berkat updateLook)
        super.draw(g2, x, y);

        // 2. Gambar Progress Bar (Hanya jika ada isinya)
        if (!ingredients.isEmpty()) {
            // (Kode Progress Bar sama seperti sebelumnya)
            int barWidth = 32;
            int barHeight = 5;
            int screenX = x + 8;
            int screenY = y - 10;

            g2.setColor(Color.WHITE);
            g2.fillRect(screenX, screenY, barWidth, barHeight);

            if (isBurned) g2.setColor(Color.BLACK);
            else if (isCooked) g2.setColor(Color.GREEN);
            else g2.setColor(Color.ORANGE);

            double ratio = (double) cookingProgress / TIME_TO_BURN;
            if (ratio > 1) ratio = 1;
            
            g2.fillRect(screenX, screenY, (int)(barWidth * ratio), barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(screenX, screenY, barWidth, barHeight);
        }
        
        // Catatan: Kita tidak perlu lagi menggambar `ingredients.get(0).image` secara manual 
        // karena gambar panci/wajan itu sendiri sudah berubah visualnya (ada udangnya/nasinya).
        // Kecuali kamu punya bahan lain yang tidak ada aset khususnya.
    }
}
