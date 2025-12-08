package environment;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage; // Tambahkan import
import javax.imageio.ImageIO;      // Tambahkan import
import java.io.IOException;

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
            // Pastikan nama file di folder res/menu SAMA PERSIS dengan nama resep (Case Sensitive)
            // Contoh: Jika name = "Kappa Maki", file harus "Kappa Maki.png"
            image = ImageIO.read(getClass().getResourceAsStream("/menu/" + name + ".png"));
        } catch (Exception e) {
            // Jika gambar tidak ketemu, print error tapi jangan crash
            System.out.println("Gagal load gambar resep: " + name);
            e.printStackTrace();
        }
    }

    public void addIngredient(String ingredientName, IngredientState state) {
        // Kita simpan format "Nama_STATE" untuk validasi mudah
        // Contoh: "fish_CHOPPED"
        requiredIngredients.add(ingredientName + "_" + state.toString());
    }
}