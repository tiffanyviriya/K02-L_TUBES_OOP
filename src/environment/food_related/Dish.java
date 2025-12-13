package environment.food_related;

import environment.item.Item;
import environment.item.Preparable;
import main.util.GamePanel;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

public class Dish extends Item {

    public List<Preparable> components;

    /* Konstruktor privat untuk membuat objek Dish dengan komponen spesifik */
    private Dish(GamePanel gp, String name, List<Preparable> ingredients) {
        super(gp);
        this.name = name;
        this.components = ingredients;
        this.collision = true;

        loadDishImage();
    }

    /* Memuat gambar hidangan berdasarkan nama dari sumber daya */
    private void loadDishImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/menu/" + name + ".png"));
        } catch (Exception e) {

        }
    }

    /* Menggambar hidangan pada posisi tertentu menggunakan konteks grafis */
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.itemSize, gp.itemSize, null);
        }
    }

    public static class Builder {
        private GamePanel gp;
        private List<Preparable> ingredients;

        /* Konstruktor Builder untuk inisialisasi daftar bahan */
        public Builder(GamePanel gp) {
            this.gp = gp;
            this.ingredients = new ArrayList<>();
        }

        /* Menambahkan satu bahan ke dalam daftar bahan pembentuk hidangan */
        public Builder addIngredient(Preparable p) {
            this.ingredients.add(p);
            return this;
        }

        /* Menambahkan daftar bahan sekaligus ke dalam pembentuk hidangan */
        public Builder addIngredients(List<Preparable> list) {
            this.ingredients.addAll(list);
            return this;
        }

        /* Membangun objek Dish jika kombinasi bahan cocok dengan resep yang ada */
        public Dish build() {
            ArrayList<String> ingredientNames = new ArrayList<>();
            for (Preparable p : ingredients) {
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    ingredientNames.add(ing.name + "_" + ing.state);
                }
            }

            for (Recipe recipe : gp.orderM.levelRecipes) {
                if (isRecipeMatch(recipe, ingredientNames)) {
                    return new Dish(gp, recipe.name, new ArrayList<>(ingredients));
                }
            }

            return null;
        }

        /* Memeriksa apakah bahan-bahan di piring cocok dengan resep tertentu */
        private boolean isRecipeMatch(Recipe recipe, ArrayList<String> plateContents) {
            if (recipe.requiredIngredients.size() != plateContents.size()) return false;

            ArrayList<String> tempPlate = new ArrayList<>(plateContents);

            for (String req : recipe.requiredIngredients) {
                if (tempPlate.contains(req)) {
                    tempPlate.remove(req);
                } else {
                    return false;
                }
            }
            return true;
        }
    }
}