package environment.food_related;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage; // Tambahkan import
import javax.imageio.ImageIO;      // Tambahkan import


public class Recipe {
    public String name;
    public List<String> requiredIngredients; // Nama bahan beserta statenya (misal: "Fish_CHOPPED")
    public int reward;
    public int timeLimit; // Dalam detik

    public BufferedImage image;

    public Recipe(String name, int reward, int timeLimit) {
        this.name = name;
        this.reward = reward;
        this.timeLimit = timeLimit;
        this.requiredIngredients = new ArrayList<>();

        loadRecipeImage();
    }

    private void loadRecipeImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/menu/" + name + ".png"));
        } catch (Exception e) {
            // Jika gambar tidak ketemu, print error tapi jangan crash
            System.out.println("Gagal load gambar resep: " + name);
            e.printStackTrace();
        }
    }

    public void addIngredient(String ingredientName, IngredientState state) {
        requiredIngredients.add(ingredientName + "_" + state.toString());
    }
}