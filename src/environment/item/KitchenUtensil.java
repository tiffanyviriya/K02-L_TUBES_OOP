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
            imgPanEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan.png"));
            imgPanShrimp = ImageIO.read(getClass().getResourceAsStream("/utensils/Frying_Pan_With_Shrimp.png"));
            imgPotEmpty = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot.png"));
            imgPotRice = ImageIO.read(getClass().getResourceAsStream("/utensils/Boiling_Pot_With_Rice.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void updateLook() {
        if (name.equalsIgnoreCase("Pan")) {
            image = hasIngredient("shrimp") ? imgPanShrimp : imgPanEmpty;
        } else if (name.equalsIgnoreCase("Pot")) {
            image = hasIngredient("rice") ? imgPotRice : imgPotEmpty;
        }
    }

    private boolean hasIngredient(String ingredientName) {
        for (Ingredient i : ingredients) {
            if (i.name.equalsIgnoreCase(ingredientName)) return true;
        }
        return false;
    }

    // [UBAH] Return boolean: true jika sukses masuk, false jika gagal/error
    public boolean addIngredient(Ingredient in) {
        // Cek Penuh/Matang
        if (!ingredients.isEmpty() || isCooked || isBurned) {
            gp.soundM.playSE(6); // Error Sound
            return false;
        }

        boolean isCompatible = false;

        // 1. VALIDASI POT (Hanya Rice RAW)
        if (name.equalsIgnoreCase("Pot")) {
            if (in.name.toLowerCase().contains("rice") && in.state == environment.food_related.IngredientState.RAW) {
                isCompatible = true;
            }
        }
        // 2. VALIDASI PAN (Hanya Shrimp CHOPPED)
        else if (name.equalsIgnoreCase("Pan")) {
            if (in.name.equalsIgnoreCase("shrimp") && in.state == environment.food_related.IngredientState.CHOPPED) {
                isCompatible = true;
            }
        }

        if (isCompatible) {
            ingredients.add(in);
            System.out.println("Bahan " + in.name + " masuk ke " + name);
            updateLook();
            return true; // Sukses
        } else {
            System.out.println("Bahan tidak cocok!");
            gp.soundM.playSE(6); // Error Sound (Index 6)
            return false; // Gagal
        }
    }

    public void cook() {
        if (!ingredients.isEmpty()) {
            cookingProgress++;
            if (cookingProgress >= TIME_TO_BURN) {
                isBurned = true; isCooked = false;
                for(Ingredient i : ingredients) i.burn();
            } else if (cookingProgress >= TIME_TO_COOK) {
                isCooked = true;
                for(Ingredient i : ingredients) i.cook();
            }
        }
    }

    public ArrayList<Ingredient> serveToPlate() {
        if (isCooked && !isBurned) {
            ArrayList<Ingredient> servedFood = new ArrayList<>(ingredients);
            ingredients.clear();
            cookingProgress = 0;
            isCooked = false; isBurned = false;
            updateLook();
            return servedFood;
        }
        return null;
    }

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