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

    private BufferedImage imgPanEmpty, imgPanShrimp, imgPanCooked, imgPanBurned;
    private BufferedImage imgPotEmpty, imgPotRice, imgPotCooked, imgPotBurned;

    /* Konstruktor untuk inisialisasi peralatan dapur seperti panci atau wajan */
    public KitchenUtensil(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.collision = true;
        solidArea = new Rectangle(0,0, 24,24);
        loadAllImages();
        updateLook();
    }

    /* Memuat semua aset gambar untuk berbagai kondisi peralatan masak */
    private void loadAllImages() {
        try {
            imgPanEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan.png"));
            imgPanShrimp = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp.png"));
            imgPanCooked = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp_Cooked.png"));
            imgPanBurned = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp_Burned.png"));

            imgPotEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot.png"));
            imgPotRice = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot_With_Rice.png"));

            try {
                imgPotCooked = ImageIO.read(getClass().getResourceAsStream("/utensils/boiling_pot_boil.png"));
                imgPotBurned = ImageIO.read(getClass().getResourceAsStream("/utensils/boiling_pot_gosong.png"));
            } catch (Exception e) {
                imgPotCooked = imgPotRice;
                imgPotBurned = imgPotRice;
            }

        } catch (IOException e) { }
    }

    /* Memperbarui tampilan visual peralatan masak berdasarkan isinya dan status memasak */
    public void updateLook() {
        if (name.equalsIgnoreCase("Pan")) {
            if (isBurned) {
                image = imgPanBurned;
            } else if (isCooked) {
                image = imgPanCooked;
            } else {
                image = hasIngredient("shrimp") ? imgPanShrimp : imgPanEmpty;
            }
        }
        else if (name.equalsIgnoreCase("Pot")) {
            if (isBurned) {
                image = imgPotBurned;
            } else if (isCooked) {
                image = imgPotCooked;
            } else if (cookingProgress > 0 && !ingredients.isEmpty()) {
                image = imgPotCooked;
            } else {
                image = hasIngredient("rice") ? imgPotRice : imgPotEmpty;
            }
        }
    }

    /* Mengecek apakah bahan tertentu sudah ada di dalam peralatan masak */
    private boolean hasIngredient(String ingredientName) {
        for (Ingredient i : ingredients) {
            if (i.name.equalsIgnoreCase(ingredientName)) return true;
        }
        return false;
    }

    /* Menambahkan bahan ke dalam peralatan masak jika kompatibel */
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
            if (in.name.equalsIgnoreCase("shrimp") && in.state == environment.food_related.IngredientState.CHOPPED) {
                isCompatible = true;
            }
        }

        if (isCompatible) {
            ingredients.add(in);
            updateLook();
            return true;
        } else {
            gp.soundM.playSE(6);
            return false;
        }
    }

    /* Menjalankan proses memasak, memperbarui progress, dan menangani status matang atau gosong */
    public void cook() {
        if (!ingredients.isEmpty()) {

            if (!isSoundPlaying && !isBurned) {
                if (name.equalsIgnoreCase("Pot")) gp.soundM.playPotSound();
                else if (name.equalsIgnoreCase("Pan")) gp.soundM.playPanSound();
                isSoundPlaying = true;
            }

            cookingProgress++;
            if (cookingProgress % 60 == 0) updateLook();

            if (cookingProgress >= TIME_TO_BURN) {
                if (!isBurned) {
                    isBurned = true; isCooked = false;
                    stopCookingSound();
                    for(Ingredient i : ingredients) i.burn();
                    updateLook();
                }
            } else if (cookingProgress >= TIME_TO_COOK) {
                if (!isCooked) {
                    isCooked = true;
                    for(Ingredient i : ingredients) i.cook();
                    updateLook();
                }
            }
        } else {
            stopCookingSound();
        }
    }

    /* Memindahkan makanan yang sudah matang dari peralatan masak ke piring */
    public ArrayList<Ingredient> serveToPlate() {
        if (isCooked && !isBurned) {
            ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
            reset();
            return servedFood;
        }
        return null;
    }

    /* Menghentikan efek suara memasak */
    public void stopCookingSound() {
        if (isSoundPlaying) {
            if (name.equalsIgnoreCase("Pot")) gp.soundM.stopPotSound();
            else if (name.equalsIgnoreCase("Pan")) gp.soundM.stopPanSound();
            isSoundPlaying = false;
        }
    }

    /* Mengosongkan peralatan masak dan mengembalikan status ke awal */
    public void reset() {
        ingredients.clear();
        cookingProgress = 0;
        isCooked = false;
        isBurned = false;
        stopCookingSound();
        updateLook();
    }

    /* Menggambar peralatan masak dan bar progress memasak ke layar */
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        int size = gp.tileSize - 16;
        int offset = (gp.tileSize - size) / 2;
        if (image != null) g2.drawImage(image, x + offset, y + offset, size, size, null);

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