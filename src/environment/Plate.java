package environment;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;

public class Plate extends Item {
    
    // Piring bisa menampung banyak item (Nasi + Ikan + Rumput Laut)
    public ArrayList<Ingredient> platedFood = new ArrayList<>();
    
    public Plate(GamePanel gp) {
        super(gp);
        name = "Plate";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/sprites/Utensils/clean-plate.png"));
        } catch (Exception e) {}
    }

    public void addItem(Ingredient food) {
        platedFood.add(food);
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        super.draw(g2, x, y); // Gambar Piring Kosong

        // Gambar Makanan di atas piring (ditumpuk)
        int offset = 0;
        for (Ingredient food : platedFood) {
            // Gambar visual makanan yang sudah matang/siap saji
            g2.drawImage(food.image, x + 10 + offset, y + 10, 20, 20, null);
            offset += 5; // Geser sedikit biar kelihatan tumpukannya
        }
    }
}
