package environment.item;

import main.util.GamePanel;

import environment.food_related.Dish;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Plate extends Item {
    public PlateState plateState;

    // Menyimpan bahan-bahan mentah/setengah jadi
    public Set<Preparable> itemOnPlate = new HashSet<>();

    // Menyimpan hasil Dish jika kombinasi bahan valid (bisa null)
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method untuk menambah bahan ke piring
    public void addItem(Preparable preparable) {
        // 1. Tambahkan bahan ke set
        itemOnPlate.add(preparable);

        // 2. Cek apakah kombinasi bahan saat ini membentuk Dish valid?
        checkDishCompletion();
    }

    // Menggunakan BUILDER PATTERN untuk mengecek status Dish
    private void checkDishCompletion() {
        // A. Instansiasi Builder
        Dish.Builder builder = new Dish.Builder(gp);

        // B. Masukkan semua bahan yang ada di piring
        builder.addIngredients(new ArrayList<>(itemOnPlate));

        // C. Build (akan return null jika tidak valid, atau Object Dish jika valid)
        this.completedDish = builder.build();

        if (this.completedDish != null) {
            System.out.println("Plate Update: Menjadi hidangan " + completedDish.name);
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        super.draw(g2, x, y);

        // Hanya gambar makanan jika piring BERSIH
        if (plateState == PlateState.CLEAN) {

            // KONDISI 1: Sudah jadi Dish valid -> Gambar Dish utuh
            if (completedDish != null) {
                // Gambar agak di tengah (offset 4px)
                completedDish.draw(g2, x + 4, y + 4);
            }
            // KONDISI 2: Belum jadi Dish (atau kombinasi salah) -> Gambar bahan satu per satu
            else {
                for (Preparable p : itemOnPlate) {
                    Item item = (Item) p;
                    // Gambar bahan kecil-kecil
                    g2.drawImage(item.image, x + 6, y + 6, gp.itemSize - 8, gp.itemSize - 8, null);
                }
            }
        }
    }
}