package environment.item;

import environment.food_related.Ingredient;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class KitchenUtensil extends Item {

    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    public int cookingProgress = 0;
    public final int TIME_TO_COOK = 300;
    public final int TIME_TO_BURN = 600;

    public boolean isCooked = false;
    public boolean isBurned = false;

    private boolean isSoundPlaying = false;

    // Gambar-gambar status
    private BufferedImage imgPanEmpty, imgPanShrimp, imgPanCooked, imgPanBurned;
    private BufferedImage imgPotEmpty, imgPotRice, imgPotCooked, imgPotBurned;

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
            // --- LOAD GAMBAR PAN ---
            imgPanEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan.png"));
            imgPanShrimp = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp.png"));
            // [FIX] Load Cooked & Burned
            imgPanCooked = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp_Cooked.png"));
            imgPanBurned = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp_Burned.png"));

            // --- LOAD GAMBAR POT ---
            imgPotEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot.png"));
            imgPotRice = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot_With_Rice.png"));

            // Load gambar tambahan Pot jika ada (gunakan fallback jika tidak ada file spesifik)
            try {
                imgPotCooked = ImageIO.read(getClass().getResourceAsStream("/utensils/boiling_pot_boil.png"));
                imgPotBurned = ImageIO.read(getClass().getResourceAsStream("/utensils/boiling_pot_gosong.png"));
            } catch (Exception e) {
                // Fallback jika file tidak ditemukan
                imgPotCooked = imgPotRice;
                imgPotBurned = imgPotRice;
            }

        } catch (IOException e) { e.printStackTrace(); }
    }

    // [FIX] Logika Update Tampilan
    public void updateLook() {
        if (name.equalsIgnoreCase("Pan")) {
            if (isBurned) {
                image = imgPanBurned; // Gambar Gosong
            } else if (isCooked) {
                image = imgPanCooked; // Gambar Matang
            } else {
                // Gambar Mentah atau Kosong
                image = hasIngredient("shrimp") ? imgPanShrimp : imgPanEmpty;
            }
        }
        else if (name.equalsIgnoreCase("Pot")) {
            if (isBurned) {
                image = imgPotBurned;
            } else if (isCooked) {
                image = imgPotCooked;
            } else if (cookingProgress > 0 && !ingredients.isEmpty()) {
                image = imgPotCooked; // Efek mendidih (pake gambar boil)
            } else {
                image = hasIngredient("rice") ? imgPotRice : imgPotEmpty;
            }
        }
    }

    private boolean hasIngredient(String ingredientName) {
        for (Ingredient i : ingredients) {
            if (i.name.equalsIgnoreCase(ingredientName)) return true;
        }
        return false;
    }

    public boolean addIngredient(Ingredient in) {
        if (!ingredients.isEmpty() || isCooked || isBurned) {
            gp.soundM.playSE(6);
            return false;
        }

        boolean isCompatible = false;
        if (name.equalsIgnoreCase("Pot")) {
            if (in.name.toLowerCase().contains("rice") && in.state == environment.food_related.IngredientState.RAW) {
                isCompatible = true;
            }
        }
        else if (name.equalsIgnoreCase("Pan")) {
            // Terima Shrimp CHOPPED
            if (in.name.equalsIgnoreCase("shrimp") && in.state == environment.food_related.IngredientState.CHOPPED) {
                isCompatible = true;
            }
        }

        if (isCompatible) {
            ingredients.add(in);
            System.out.println("Bahan " + in.name + " masuk ke " + name);
            updateLook();
            return true;
        } else {
            gp.soundM.playSE(6);
            return false;
        }
    }

    public void cook() {
        if (!ingredients.isEmpty()) {

            // Start Sound
            if (!isSoundPlaying && !isBurned) {
                if (name.equalsIgnoreCase("Pot")) gp.soundM.playPotSound();
                else if (name.equalsIgnoreCase("Pan")) gp.soundM.playPanSound();
                isSoundPlaying = true;
            }

            cookingProgress++;
            // Update look sesekali atau saat status berubah penting
            if (cookingProgress % 60 == 0) updateLook();

            if (cookingProgress >= TIME_TO_BURN) {
                if (!isBurned) { // Cek agar tidak update terus menerus
                    isBurned = true; isCooked = false;
                    stopCookingSound();
                    for(Ingredient i : ingredients) i.burn();
                    updateLook(); // Ubah ke gambar gosong
                }
            } else if (cookingProgress >= TIME_TO_COOK) {
                if (!isCooked) {
                    isCooked = true;
                    // Note: Jangan stop suara dulu, biarkan sampai diangkat atau gosong
                    for(Ingredient i : ingredients) i.cook();
                    updateLook(); // Ubah ke gambar matang
                }
            }
        } else {
            stopCookingSound();
        }
    }

    public ArrayList<Ingredient> serveToPlate() {
        if (isCooked && !isBurned) {
            ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
            reset(); // Bersihkan panci setelah disajikan
            return servedFood;
        }
        return null;
    }

    public void stopCookingSound() {
        if (isSoundPlaying) {
            if (name.equalsIgnoreCase("Pot")) gp.soundM.stopPotSound();
            else if (name.equalsIgnoreCase("Pan")) gp.soundM.stopPanSound();
            isSoundPlaying = false;
        }
    }

    // [FIX] Method Reset untuk Trash Station
    public void reset() {
        ingredients.clear();
        cookingProgress = 0;
        isCooked = false;
        isBurned = false;
        stopCookingSound();
        updateLook(); // Kembali ke gambar kosong
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        int size = gp.tileSize - 16;
        int offset = (gp.tileSize - size) / 2;
        if (image != null) g2.drawImage(image, x + offset, y + offset, size, size, null);

        // Progress Bar (Hanya muncul jika ada isi)
        if (!ingredients.isEmpty()) {
            int barWidth = 32; int screenX = x + offset; int screenY = y - 8;
            g2.setColor(Color.WHITE); g2.fillRect(screenX, screenY, barWidth, 5);

            if (isBurned) g2.setColor(Color.BLACK);
            else if (isCooked) g2.setColor(Color.GREEN);
            else g2.setColor(Color.ORANGE);

            double ratio = (double) cookingProgress / TIME_TO_BURN;
            if (ratio > 1) ratio = 1;
            g2.fillRect(screenX, screenY, (int)(barWidth * ratio), 5);
            g2.setColor(Color.BLACK); g2.drawRect(screenX, screenY, barWidth, 5);
        }
    }
}