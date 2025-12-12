package tile;

import environment.entity.Entity;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class TrashStation extends Tile {

    public TrashStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/trash_station.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void interact(Entity player) {
        if (player.inventory == null) return;

        // KASUS 1: Membuang isi Panci/Wajan (Termasuk Gosong)
        if (player.inventory instanceof KitchenUtensil) {
            KitchenUtensil utensil = (KitchenUtensil) player.inventory;

            // Cek apakah ada isinya? (Baik matang, mentah, atau gosong)
            if (!utensil.ingredients.isEmpty()) {
                utensil.reset(); // Panggil method reset() yang sudah kita perbaiki
                System.out.println("Trash Station: Isi " + utensil.name + " dibuang/dibersihkan.");

                // Opsional: Mainkan suara buang sampah
                // gp.soundM.playSE(x);
            }
        }

        // KASUS 2: Membuang isi Piring
        else if (player.inventory instanceof Plate) {
            Plate plate = (Plate) player.inventory;
            if (!plate.itemOnPlate.isEmpty()) {
                plate.itemOnPlate.clear();
                plate.completedDish = null;
                plate.updateImage(); // Reset gambar piring jadi bersih/kotor
                System.out.println("Trash Station: Makanan di piring dibuang.");
            }
        }

        // KASUS 3: Membuang Bahan (Ingredient) langsung
        else {
            player.inventory = null;
            System.out.println("Item dibuang.");
        }
    }
}