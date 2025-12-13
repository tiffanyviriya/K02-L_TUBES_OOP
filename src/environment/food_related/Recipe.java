package environment.food_related;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Recipe {
    public String name;
    public List<String> requiredIngredients;
    public int reward;
    public int timeLimit;

    public BufferedImage image;

    /* Konstruktor untuk inisialisasi resep dengan nama, nilai hadiah, dan batas waktu pengerjaan */
    public Recipe(String name, int reward, int timeLimit) {
        this.name = name;
        this.reward = reward;
        this.timeLimit = timeLimit;
        this.requiredIngredients = new ArrayList<>();

        loadRecipeImage();
    }

    /* Memuat gambar visualisasi resep dari direktori resources */
    private void loadRecipeImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/menu/" + name + ".png"));
        } catch (Exception e) {

        }
    }

    /* Menambahkan kriteria bahan dan status pengolahannya ke dalam daftar kebutuhan resep */
    public void addIngredient(String ingredientName, IngredientState state) {
        requiredIngredients.add(ingredientName + "_" + state.toString());
    }
}