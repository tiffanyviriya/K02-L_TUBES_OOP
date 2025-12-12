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

    // Dish yang sudah jadi (bukan cuma gambar, tapi objek Dish)
    public Dish completedDish = null;

    public Plate(GamePanel gp) {
        super(gp);
        plateState = PlateState.CLEAN;
        solidArea = new Rectangle(0,0, 24,24);
        updateImage();
    }

    public void updateImage() {
        try {
            if (plateState == PlateState.CLEAN) {
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_clean.png"));
            } else {
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_dirty.png"));
                completedDish = null;
                itemOnPlate.clear();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void addItem(Preparable preparable) {
        // Tambah bahan
        itemOnPlate.add(preparable);

        // [BARU] Langsung cek apakah jadi Resep?
        checkRecipe();
    }

    // Method untuk mengecek resep secara otomatis
    public void checkRecipe() {
        // Konversi Set ke List agar bisa dipakai oleh Dish.Builder
        java.util.List<Preparable> ingredientsList = new java.util.ArrayList<>(itemOnPlate);

        // Gunakan Dish.Builder untuk mengecek resep
        Dish dish = new Dish.Builder(gp)
                .addIngredients(ingredientsList)
                .build();

        if (dish != null) {
            this.completedDish = dish;
            System.out.println("Resep Terbentuk di Piring: " + dish.name);
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        super.draw(g2, x, y);

        if (plateState == PlateState.CLEAN) {
            // 1. Jika sudah jadi Dish -> Gambar Dish
            if (completedDish != null && completedDish.image != null) {
                // Gambar dish agak besar menutupi piring
                g2.drawImage(completedDish.image, x, y, gp.itemSize, gp.itemSize, null);
            }
            // 2. Jika belum jadi -> Gambar bahan-bahan numpuk
            else {
                int count = 0;
                for (Preparable p : itemOnPlate) {
                    if (p instanceof Item) {
                        Item item = (Item) p;
                        // Geser sedikit tiap bahan biar kelihatan numpuk
                        int shift = count * 2;
                        g2.drawImage(item.image, x + shift, y - shift, gp.itemSize, gp.itemSize, null);
                        count++;
                    }
                }
            }
        }
    }
}