package tile;

import environment.entity.Entity;
import environment.item.Plate;
import environment.item.PlateState;
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

            // 1. Cek apakah piring berisi Dish yang SUDAH JADI?
            // (Dish ini dibuat otomatis oleh Plate via Builder saat assembly)
            if (servedPlate.completedDish != null) {

                // 2. Kirim Dish ke OrderManager untuk dinilai
                gp.orderM.checkServing(servedPlate.completedDish);

                // 3. Bersihkan Piring
                servedPlate.completedDish = null; // Hapus Dish
                servedPlate.itemOnPlate.clear();  // Hapus bahan-bahan

                // 4. Ambil piring dari tangan player & mulai timer pengembalian
                player.inventory = null;
                pendingPlates.add(new PendingPlate(servedPlate, RETURN_DELAY));

                System.out.println("Dish disajikan. Piring kembali dlm 10 detik.");
            }
            else {
                // Jika piring kosong atau cuma berisi bahan acak yang bukan resep
                System.out.println("Gagal: Piring belum berisi hidangan yang valid!");
            }
        }
        else {
            System.out.println("Gagal: Anda harus menyajikan makanan di atas piring!");
        }
    }

    // Logika pengembalian piring ke storage (tidak berubah drastis)
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