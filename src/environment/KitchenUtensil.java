package environment;

import main.GamePanel;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;

public class KitchenUtensil extends Item {

    // Menyimpan bahan makanan di dalam alat masak
    public ArrayList<Ingredient> ingredients = new ArrayList<>();

    // Menyimpan progress memasak (agar jika diangkat, progress tidak hilang)
    public int cookingProgress = 0;

    public KitchenUtensil(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        loadUtensilImage();
    }

    private void loadUtensilImage() {
        try {
            // Ganti dengan nama file gambar panci/wajan Anda
            // Contoh: "pan.png" atau "pot.png"
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/OOPtile.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addIngredient(Ingredient in) {
        ingredients.add(in);
    }
}