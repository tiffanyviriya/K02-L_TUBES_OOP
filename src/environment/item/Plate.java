package environment.item;

import environment.food_related.Dish;
import environment.food_related.Ingredient;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Plate extends Item {
    public PlateState plateState;
    public Set<Preparable> itemOnPlate = new HashSet<>();

    public Dish completedDish = null;

    /* Konstruktor untuk inisialisasi piring dengan status bersih */
    public Plate(GamePanel gp) {
        super(gp);
        plateState = PlateState.CLEAN;
        solidArea = new Rectangle(0,0, 24,24);
        updateImage();
    }

    /* Memperbarui gambar piring berdasarkan status bersih atau kotor */
    public void updateImage() {
        try {
            if (plateState == PlateState.CLEAN) {
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_clean.png"));
            } else {
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_dirty.png"));
                completedDish = null;
                itemOnPlate.clear();
            }
        } catch (Exception e) {  }
    }

    /* Menambahkan item ke piring dan mengecek apakah membentuk resep */
    public void addItem(Preparable preparable) {
        itemOnPlate.add(preparable);
        checkRecipe();
    }

    /* Mengecek apakah kombinasi bahan di piring sesuai dengan resep yang ada */
    public void checkRecipe() {
        java.util.List<Preparable> ingredientsList = new java.util.ArrayList<>(itemOnPlate);

        Dish dish = new Dish.Builder(gp)
                .addIngredients(ingredientsList)
                .build();

        if (dish != null) {
            this.completedDish = dish;
        }
    }

    /* Menggambar piring beserta isinya atau hidangan yang sudah jadi */
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        super.draw(g2, x, y);

        if (plateState == PlateState.CLEAN) {
            if (completedDish != null && completedDish.image != null) {
                g2.drawImage(completedDish.image, x, y, gp.itemSize, gp.itemSize, null);
            }
            else {
                int count = 0;
                for (Preparable p : itemOnPlate) {
                    if (p instanceof Item) {
                        Item item = (Item) p;
                        int shift = count * 2;
                        g2.drawImage(item.image, x + shift, y - shift, gp.itemSize, gp.itemSize, null);
                        count++;
                    }
                }
            }
        }
    }
}