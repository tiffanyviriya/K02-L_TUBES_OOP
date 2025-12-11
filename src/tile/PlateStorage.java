package tile;

import environment.Entity;
import environment.Plate;
import environment.PlateState;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Stack;

public class PlateStorage extends Tile {
    // Ubah visibility jadi public agar bisa diakses debugging jika perlu
    public Stack<Plate> plateOnStorage = new Stack<>();
    private final int STARTING_PLATES = 3; // Jumlah piring awal

    public PlateStorage(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadStorageImage();
        loadPlates();
    }

    private void loadStorageImage() {
        try {
            // Pastikan path ini benar
            image = ImageIO.read(getClass().getResourceAsStream("/stations/plate-storage.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlates() {
        // Isi stack dengan piring bersih di awal game
        for (int i = 0; i < STARTING_PLATES; i++) {
            plateOnStorage.push(new Plate(gp));
        }
    }

    // Method untuk mengembalikan piring (dari ServingCounter / WashingStation)
    public void storePlate(Plate plate) {
        plateOnStorage.push(plate);
        System.out.println("PlateStorage: Piring diterima. Total sekarang: " + plateOnStorage.size());
    }

    // Method interaksi pemain mengambil piring
    @Override
    public void interact(Entity player) {
        if (player.inventory == null) {
            if (!plateOnStorage.isEmpty()) {
                // Cek piring paling atas
                Plate topPlate = plateOnStorage.peek();

                // Aturan: Hanya bisa ambil jika piring bersih (kecuali mau cuci piring kotor)
                // Tapi biasanya dari storage diambil yang bersih.
                // Jika spek membolehkan ambil piring kotor untuk dicuci, hapus pengecekan ini.

                // Ambil piring dari stack
                player.inventory = plateOnStorage.pop();
                System.out.println("Mengambil piring. Sisa: " + plateOnStorage.size());
            } else {
                System.out.println("Storage kosong! Tunggu piring kembali.");
            }
        } else {
            System.out.println("Tangan penuh!");
        }
    }

    // [PENTING] Method Draw untuk Visualisasi Tumpukan
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Meja Storage (Base)
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        // 2. Gambar Tumpukan Piring
        if (!plateOnStorage.isEmpty()) {
            // Kita gambar piring paling atas saja sebagai representasi
            Plate topPlate = plateOnStorage.peek();

            // Gambar agak di tengah meja
            int plateX = x + 12; // Offset biar di tengah (48 - 24) / 2
            int plateY = y + 12;

            // Pastikan method draw milik Plate dipanggil
            // Atau gambar manual imagenya
            if (topPlate.image != null) {
                g2.drawImage(topPlate.image, plateX, plateY, gp.itemSize, gp.itemSize, null);
            }

            // 3. Visualisasi Jumlah (Opsional: Angka kecil di pojok)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(String.valueOf(plateOnStorage.size()), x + 35, y + 45);
        }
    }
}