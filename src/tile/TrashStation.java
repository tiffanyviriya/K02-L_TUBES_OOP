package tile;

import environment.entity.Entity;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class TrashStation extends Tile {

    /* Konstruktor untuk inisialisasi stasiun sampah dan memuat gambarnya */
    public TrashStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/trash_station.png"));
        } catch (IOException e) {  }
    }

    /* Menangani interaksi pemain saat membuang item, mengosongkan piring, atau membersihkan alat masak */
    @Override
    public void interact(Entity player) {
        if (player.inventory == null) return;

        if (player.inventory instanceof KitchenUtensil) {
            KitchenUtensil utensil = (KitchenUtensil) player.inventory;

            if (!utensil.ingredients.isEmpty()) {
                utensil.reset();
            }
        }
        else if (player.inventory instanceof Plate) {
            Plate plate = (Plate) player.inventory;
            if (!plate.itemOnPlate.isEmpty()) {
                plate.itemOnPlate.clear();
                plate.completedDish = null;
                plate.updateImage();
            }
        }
        else {
            player.inventory = null;
        }
    }
}