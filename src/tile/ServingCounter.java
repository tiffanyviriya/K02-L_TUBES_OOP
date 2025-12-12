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
        int timer; // Timer pengembalian piring

        public PendingPlate(Plate plate, int timer) {
            this.plate = plate;
            this.timer = timer;
        }
    }

    private ArrayList<PendingPlate> pendingPlates = new ArrayList<>();
    private final int RETURN_DELAY = 600; // 10 Detik * 60 FPS

    public ServingCounter(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadCounterImage();
    }

    private void loadCounterImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {
        // Syarat: Player harus memegang PIRING (Plate)
        if (player.inventory instanceof Plate) {
            Plate servedPlate = (Plate) player.inventory;

            // Pastikan piring tidak kosong
            if (!servedPlate.itemOnPlate.isEmpty()) {

                // 1. Ekstrak daftar bahan (String) dari piring untuk validasi
                ArrayList<String> plateIngredients = new ArrayList<>();
                for (Preparable p : servedPlate.itemOnPlate) {
                    if (p instanceof Ingredient) {
                        Ingredient ing = (Ingredient) p;
                        // Format string harus sama dengan yang ada di Recipe (misal: "fish_CHOPPED")
                        plateIngredients.add(ing.name + "_" + ing.state);
                    }
                }

                // 2. Kirim daftar bahan ke OrderManager untuk dicek (Benar/Salah)
                gp.orderM.checkServing(plateIngredients);

                // 3. Bersihkan Piring
                servedPlate.completedDish = null; // Hapus Dish visual
                servedPlate.itemOnPlate.clear();  // Hapus bahan-bahan

                // 4. Ambil piring dari tangan player & mulai timer pengembalian
                player.inventory = null;
                pendingPlates.add(new PendingPlate(servedPlate, RETURN_DELAY));

                System.out.println("Dish disajikan. Piring kembali dlm 10 detik.");
            }
            else {
                System.out.println("Gagal: Piring kosong!");
            }
        }
        else {
            System.out.println("Gagal: Anda harus menyajikan makanan di atas piring!");
        }
    }

    // Logika pengembalian piring ke storage (tidak berubah)
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

    private void returnPlateToStorage(Plate plate) {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (gp.tileM.worldTiles[col][row] instanceof PlateStorage) {
                    PlateStorage ps = (PlateStorage) gp.tileM.worldTiles[col][row];
                    plate.plateState = PlateState.DIRTY;
                    plate.updateImage();
                    ps.storePlate(plate);
                    System.out.println("Piring kotor dikembalikan ke Storage.");
                    return;
                }
            }
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}