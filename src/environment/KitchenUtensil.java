package environment;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class KitchenUtensil extends Item {

    public ArrayList<Ingredient> ingredients = new ArrayList<>();

    // Progress Memasak
    public int cookingProgress = 0;
    public final int TIME_TO_COOK = 300; // 5 Detik
    public final int TIME_TO_BURN = 600; // 10 Detik

    public boolean isCooked = false;
    public boolean isBurned = false;

    // Cache Gambar
    private BufferedImage imgPanEmpty, imgPanShrimp;
    private BufferedImage imgPotEmpty, imgPotRice;

    public KitchenUtensil(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.collision = true;
        solidArea = new Rectangle(0,0, 24,24);

        loadAllImages();
        updateLook();
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

    public void updateLook() {
        if (name.equalsIgnoreCase("Pan")) {
            if (hasIngredient("shrimp")) {
                image = imgPanShrimp;
            } else {
                image = imgPanEmpty;
            }
        }
        else if (name.equalsIgnoreCase("Pot")) {
            if (hasIngredient("cucumber")) {
                image = imgPotRice;
            } else {
                image = imgPotEmpty;
            }
        }
    }

    private boolean hasIngredient(String ingredientName) {
        for (Ingredient i : ingredients) {
            if (i.name.equalsIgnoreCase(ingredientName)) {
                return true;
            }
        }
        return false;
    }

    public void addIngredient(Ingredient in) {
        // HAPUS @Override KARENA Item.java TIDAK PUNYA METHOD INI
        if (!isCooked && !isBurned) {
            ingredients.add(in);
            System.out.println("Bahan " + in.name + " masuk ke " + name);
            updateLook();
        }
    }


    public void cook() {
        if (!ingredients.isEmpty()) {
            cookingProgress++;
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

    // Di dalam class KitchenUtensil

public ArrayList<Ingredient> serveToPlate() {
    // Hanya bisa disajikan jika SUDAH MATANG dan TIDAK GOSONG
    if (isCooked && !isBurned) {
        // Salin isi panci ke variabel sementara
        ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
        
        // Bersihkan panci
        ingredients.clear();
        cookingProgress = 0;
        isCooked = false;
        
        // Update visual panci jadi kosong
        updateLook();
        
        return servedFood; // Kembalikan bahan makanannya
    }
    return null; // Gagal (belum matang/gosong)
}

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // --- PERUBAHAN DI SINI ---
        // 1. Gambar Utensil dengan ukuran TILESIZE (48x48) agar besar
        // Kita tidak pakai super.draw() karena itu pakai itemSize (kecil)
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize-16, gp.tileSize-16, null);
        }

        // 2. Fallback Visual: Gambar bahan di tengah jika ada isinya
        // (Jika gambar utensil masih kosong/default, tapi ada isinya)
        if (!ingredients.isEmpty()) {
            if (image == imgPanEmpty || image == imgPotEmpty) {
                Ingredient ig = ingredients.get(0);
                if (ig.image != null) {
                    // Gambar kecil di tengah panci
                    // Koordinat +12 biar di tengah (48 - 24) / 2
                    g2.drawImage(ig.image, x + 12, y + 10, 24, 24, null);
                }
            }
        }

        // 3. Gambar Progress Bar
        if (!ingredients.isEmpty()) {
            int barWidth = 32;
            int barHeight = 5;
            // Posisi bar di atas panci
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
    }
}