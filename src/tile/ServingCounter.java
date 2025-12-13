package tile;

import environment.entity.Entity;
import environment.food_related.Ingredient;
import environment.item.Item;
import environment.item.Plate;
import environment.item.PlateState;
import environment.item.Preparable;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ServingCounter extends Tile {

    private class PendingPlate {
        Plate plate;
        int timer;

        public PendingPlate(Plate plate, int timer) {
            this.plate = plate;
            this.timer = timer;
        }
    }

    private ArrayList<PendingPlate> pendingPlates = new ArrayList<>();
    private final int RETURN_DELAY = 600;

    /* Konstruktor untuk inisialisasi counter penyajian dan memuat gambarnya */
    public ServingCounter(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadCounterImage();
    }

    /* Memuat gambar visual untuk counter penyajian */
    private void loadCounterImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/servingcounter.png"));
        } catch (IOException e) {

        }
    }

    /* Menangani interaksi pemain saat menyajikan makanan, memvalidasi resep, dan mengambil piring */
    @Override
    public void interact(Entity player) {
        if (player.inventory instanceof Plate) {
            Plate servedPlate = (Plate) player.inventory;

            if (!servedPlate.itemOnPlate.isEmpty()) {

                ArrayList<String> plateIngredients = new ArrayList<>();
                for (Preparable p : servedPlate.itemOnPlate) {
                    if (p instanceof Ingredient) {
                        Ingredient ing = (Ingredient) p;
                        plateIngredients.add(ing.name + "_" + ing.state);
                    }
                }

                gp.orderM.checkServing(plateIngredients);

                servedPlate.completedDish = null;
                servedPlate.itemOnPlate.clear();

                player.inventory = null;
                pendingPlates.add(new PendingPlate(servedPlate, RETURN_DELAY));
            }
        }
    }

    /* Memperbarui timer pengembalian piring dan memproses piring yang waktunya habis */
    public void update() {
        if (pendingPlates.isEmpty()) return;

        Iterator<PendingPlate> it = pendingPlates.iterator();
        while (it.hasNext()) {
            PendingPlate pp = it.next();
            pp.timer--;

            if (pp.timer <= 0) {
                returnPlateToStorage(pp.plate);
                it.remove();
            }
        }
    }

    /* Mengembalikan piring kotor ke tempat penyimpanan piring secara otomatis */
    private void returnPlateToStorage(Plate plate) {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (gp.tileM.worldTiles[col][row] instanceof PlateStorage) {
                    PlateStorage ps = (PlateStorage) gp.tileM.worldTiles[col][row];
                    plate.plateState = PlateState.DIRTY;
                    plate.updateImage();
                    ps.storePlate(plate);
                    return;
                }
            }
        }
    }

    /* Menggambar counter penyajian ke layar */
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}