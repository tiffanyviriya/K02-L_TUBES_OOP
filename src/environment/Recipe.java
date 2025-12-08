package environment;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    public String name;
    public List<String> requiredIngredients; // Nama bahan beserta statenya (misal: "Fish_CHOPPED")
    public int reward;
    public int timeLimit; // Dalam detik

    public Recipe(String name, int reward, int timeLimit) {
        this.name = name;
        this.reward = reward;
        this.timeLimit = timeLimit;
        this.requiredIngredients = new ArrayList<>();
    }

    public void addIngredient(String ingredientName, IngredientState state) {
        // Kita simpan format "Nama_STATE" untuk validasi mudah
        // Contoh: "fish_CHOPPED"
        requiredIngredients.add(ingredientName + "_" + state.toString());
    }
}