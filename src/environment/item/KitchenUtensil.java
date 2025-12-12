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

    // Progress Memasak
    public int cookingProgress = 0;
    public final int TIME_TO_COOK = 300; // 5 Detik
    public final int TIME_TO_BURN = 600; // 10 Detik

    public boolean isCooked = false;
    public boolean isBurned = false;

    // Cache Gambar
    // Pan Images
    private BufferedImage imgPanEmpty, imgPanRaw, imgPanCooked, imgPanBurned;
    // Pot Images
    private BufferedImage imgPotEmpty, imgPotRaw, imgPotCooked, imgPotBurned;

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
            // --- Load Gambar Wajan (Pan) ---
            imgPanEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan.png"));
            // Pan with Shrimp (Raw)
            imgPanRaw = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp.png"));
            // Pan with Shrimp (Cooked)
            imgPanCooked = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp_Cooked.png"));
            // Pan Burned
            imgPanBurned = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp_Burned.png"));

            // --- Load Gambar Panci (Pot) ---
            imgPotEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot.png"));
            // Pot with Grain/Boiling (Raw State) - Menggunakan gambar 'boil' untuk proses memasak
            imgPotRaw = ImageIO.read(getClass().getResourceAsStream("/utensils/boiling_pot_boil.png"));
            // Pot Rice (Cooked State)
            imgPotCooked = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot_With_Rice.png"));
            // Pot Burned
            imgPotBurned = ImageIO.read(getClass().getResourceAsStream("/utensils/boiling_pot_gosong.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLook() {
        // --- LOGIKA TAMPILAN PAN ---
        if (name.equalsIgnoreCase("Pan")) {
            if (ingredients.isEmpty()) {
                image = imgPanEmpty;
            } else if (isBurned) {
                image = imgPanBurned;
            } else if (isCooked) {
                image = imgPanCooked; // Pan Shrimp Cooked
            } else {
                image = imgPanRaw; // Pan with Shrimp (Raw)
            }
        }
        // --- LOGIKA TAMPILAN POT ---
        else if (name.equalsIgnoreCase("Pot")) {
            if (ingredients.isEmpty()) {
                image = imgPotEmpty;
            } else if (isBurned) {
                image = imgPotBurned;
            } else if (isCooked) {
                image = imgPotCooked; // Pot Rice
            } else {
                image = imgPotRaw; // Boiling Pot with Grain
            }
        }
    }

    public void addIngredient(Ingredient in) {
        // Cek jika alat sudah terisi atau sudah matang/gosong, tidak bisa tambah lagi
        if (!ingredients.isEmpty()) {
            gp.soundM.playSE(6); // Error sound
            return;
        }

        // --- VALIDASI KHUSUS PAN ---
        if (name.equalsIgnoreCase("Pan")) {
            // Hanya terima Shrimp
            if (in.name.equalsIgnoreCase("shrimp")) {
                ingredients.add(in);
                System.out.println("Shrimp added to Pan");
                gp.soundM.playSE(2); // Play Sound: Cooking Pan
                updateLook();
            } else {
                // Bahan salah
                System.out.println("Pan hanya menerima Shrimp!");
                gp.soundM.playSE(6); // Play Sound: Error
            }
        }
        // --- VALIDASI KHUSUS POT ---
        else if (name.equalsIgnoreCase("Pot")) {
            // Hanya terima Rice (Grain)
            if (in.name.equalsIgnoreCase("rice")) {
                ingredients.add(in);
                System.out.println("Rice added to Pot");
                gp.soundM.playSE(1); // Play Sound: Boiling Pot
                updateLook();
            } else {
                // Bahan salah
                System.out.println("Pot hanya menerima Rice!");
                gp.soundM.playSE(6); // Play Sound: Error
            }
        }
    }

    public void cook() {
        if (!ingredients.isEmpty()) {
            cookingProgress++;

            // Cek Status Burned
            if (cookingProgress >= TIME_TO_BURN) {
                if (!isBurned) { // Hanya update jika status berubah
                    isBurned = true;
                    isCooked = false;
                    for(Ingredient i : ingredients) i.burn();
                    updateLook(); // Ganti gambar ke gosong
                    System.out.println(name + " Gosong!");
                }
            }
            // Cek Status Cooked
            else if (cookingProgress >= TIME_TO_COOK) {
                if (!isCooked && !isBurned) { // Hanya update jika status berubah
                    isCooked = true;
                    for(Ingredient i : ingredients) i.cook();
                    updateLook(); // Ganti gambar ke matang
                    System.out.println(name + " Matang!");
                }
            }
        }
    }

    public ArrayList<Ingredient> serveToPlate() {
        // Hanya bisa disajikan jika SUDAH MATANG dan TIDAK GOSONG
        if (isCooked && !isBurned) {
            ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
            ingredients.clear();
            cookingProgress = 0;
            isCooked = false;
            isBurned = false;
            updateLook(); // Reset ke gambar kosong
            return servedFood;
        }
        return null;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Utensil dengan ukuran TILESIZE (agar besar)
        if (image != null) {
            g2.drawImage(image, x + 8, y + 8, gp.tileSize-16, gp.tileSize-16, null);
        }

        // 2. Fallback Visual: (Opsional, karena kita sudah punya gambar full state)
        // Kode di bawah menggambar icon kecil bahan jika gambar utensil masih default/kosong
        // tapi ada isinya. Berguna jika gambar state belum ter-load sempurna.
        if (!ingredients.isEmpty()) {
            if (image == imgPanEmpty || image == imgPotEmpty) {
                Ingredient ig = ingredients.get(0);
                if (ig.image != null) {
                    g2.drawImage(ig.image, x + 8, y + 8, 24, 24, null);
                }
            }
        }

        // 3. Gambar Progress Bar
        if (!ingredients.isEmpty()) {
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
    }
}