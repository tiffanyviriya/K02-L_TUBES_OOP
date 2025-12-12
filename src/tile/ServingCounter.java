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

    // Inner class untuk melacak piring yang sedang "dimakan" pelanggan
    private class PendingPlate {
        Plate plate;
        int timer; // Timer dalam frame (60 FPS x 10 detik = 600)

        public PendingPlate(Plate plate, int timer) {
            this.plate = plate;
            this.timer = timer;
        }
    }

    private ArrayList<PendingPlate> pendingPlates = new ArrayList<>();
    private final int RETURN_DELAY = 600; // 10 Detik * 60 FPS

    public ServingCounter(GamePanel gp) {
        super(gp);
        this.collision = true; // Player tidak bisa menembus meja
        loadCounterImage();
    }

    private void loadCounterImage() {
        try {
            // Gunakan gambar serving counter atau wall sementara
            image = ImageIO.read(getClass().getResourceAsStream("/stations/servingcounter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- INTERAKSI PEMAIN ---
    @Override
    public void interact(Entity player) {
        // Syarat: Player harus memegang PIRING (Plate)
        if (player.inventory instanceof Plate) {
            Plate servedPlate = (Plate) player.inventory;

            // 1. Validasi kecocokan dish dengan OrderManager
            // checkServing akan mengembalikan true (Score) atau false (Penalty)
            gp.orderM.checkServing(servedPlate);

            // 2. Logika Pembersihan & Pengembalian
            // Baik pesanan benar atau salah, makanan hilang "dimakan"
            servedPlate.itemOnPlate.clear();

            // Player melepaskan piring dari tangan
            player.inventory = null;

            // 3. Masukkan ke antrean pengembalian (Timer 10 detik)
            pendingPlates.add(new PendingPlate(servedPlate, RETURN_DELAY));

            System.out.println("Dish disajikan. Piring akan kembali dalam 10 detik.");
        }
        else {
            System.out.println("Gagal: Anda harus menyajikan makanan di atas piring!");
        }
    }

    // --- UPDATE LOOP (WAJIB DIPANGGIL DI TILEMANAGER) ---
    public void update() {
        if (pendingPlates.isEmpty()) return;

        Iterator<PendingPlate> it = pendingPlates.iterator();
        while (it.hasNext()) {
            PendingPlate pp = it.next();
            pp.timer--;

            // Jika waktu habis (sudah 10 detik)
            if (pp.timer <= 0) {
                returnPlateToStorage(pp.plate);
                it.remove(); // Hapus dari daftar pending
            }
        }
    }

    // Helper untuk mengembalikan piring ke PlateStorage
    private void returnPlateToStorage(Plate plate) {
        // Cari Tile PlateStorage di map
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (gp.tileM.worldTiles[col][row] instanceof PlateStorage) {
                    PlateStorage ps = (PlateStorage) gp.tileM.worldTiles[col][row];

                    // 1. Set status piring jadi KOTOR
                    plate.plateState = PlateState.DIRTY;

                    // 2. [PENTING] Update gambar piring agar terlihat kotor
                    plate.updateImage();

                    // 3. Masukkan ke tumpukan paling atas
                    ps.storePlate(plate);

                    System.out.println("Piring kotor dikembalikan ke Storage.");
                    return;
                }
            }
        }
        System.out.println("Error: Tidak menemukan PlateStorage untuk mengembalikan piring!");
    }

    // [FIX]: Hapus @Override karena class Tile tidak punya method draw()
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}