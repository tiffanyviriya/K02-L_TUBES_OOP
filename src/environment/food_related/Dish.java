package environment.food_related;

import environment.item.Item;
import environment.item.Preparable;
import main.util.GamePanel;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

public class Dish extends Item {

    // Dish menyimpan daftar bahan pembentuknya
    public List<Preparable> components;

    // Constructor Private: Hanya bisa dipanggil oleh Builder
    private Dish(GamePanel gp, String name, List<Preparable> ingredients) {
        super(gp);
        this.name = name;
        this.components = ingredients;
        this.collision = true; // Dish bisa ditaruh di meja

        loadDishImage();
    }

    private void loadDishImage() {
        try {
            // Load gambar sesuai nama Dish (misal: "Kappa Maki.png")
            // Pastikan gambar ada di folder /res/menu/
            image = ImageIO.read(getClass().getResourceAsStream("/menu/" + name + ".png"));
        } catch (Exception e) {
            System.out.println("Gambar Dish tidak ditemukan: " + name);
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.itemSize, gp.itemSize, null);
        }
    }

    // ==========================================
    //       STATIC INNER CLASS: BUILDER
    // ==========================================
    public static class Builder {
        private GamePanel gp;
        private List<Preparable> ingredients;

        public Builder(GamePanel gp) {
            this.gp = gp;
            this.ingredients = new ArrayList<>();
        }

        // Method untuk menambah bahan satu per satu (Chaining)
        public Builder addIngredient(Preparable p) {
            this.ingredients.add(p);
            return this;
        }

        // Method untuk menambah banyak bahan sekaligus
        public Builder addIngredients(List<Preparable> list) {
            this.ingredients.addAll(list);
            return this;
        }

        // Method build() yang melakukan validasi resep secara otomatis
        public Dish build() {
            // 1. Siapkan list nama bahan string untuk pengecekan (misal: "fish_CHOPPED")
            ArrayList<String> ingredientNames = new ArrayList<>();
            for (Preparable p : ingredients) {
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    ingredientNames.add(ing.name + "_" + ing.state);
                }
            }

            // 2. Cek kecocokan dengan Resep yang ada di OrderManager
            // Kita mengakses daftar resep level ini lewat GamePanel -> OrderManager
            for (Recipe recipe : gp.orderM.levelRecipes) {
                if (isRecipeMatch(recipe, ingredientNames)) {
                    // JIKA COCOK: Return Dish baru
                    return new Dish(gp, recipe.name, new ArrayList<>(ingredients));
                }
            }

            // JIKA TIDAK ADA YANG COCOK: Return null (Belum jadi Dish valid)
            return null;
        }

        // Logika untuk memastikan bahan di piring SAMA PERSIS dengan resep
        private boolean isRecipeMatch(Recipe recipe, ArrayList<String> plateContents) {
            // Jumlah bahan harus sama persis (Exact Match)
            if (recipe.requiredIngredients.size() != plateContents.size()) return false;

            // Gunakan copy list agar aman saat remove
            ArrayList<String> tempPlate = new ArrayList<>(plateContents);

            for (String req : recipe.requiredIngredients) {
                if (tempPlate.contains(req)) {
                    tempPlate.remove(req);
                } else {
                    return false; // Bahan wajib tidak ditemukan
                }
            }
            return true; // Semua bahan cocok
        }
    }
}