package tile;

import main.GamePanel;
import environment.Entity;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ServingCounter extends Tile {

    public ServingCounter(GamePanel gp) {
        super(gp);
        this.collision = true; // Player tidak bisa jalan tembus counter
        loadCounterImage();
    }

    private void loadCounterImage() {
        try {
            // Pastikan kamu punya gambar ini, atau ganti dengan "/tiles/wall1.png" sementara
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method interaksi yang dipanggil Player saat menekan 'C'
    public void interact(Entity player) {
        // Cek jika Player membawa sesuatu
        if (player.inventory != null) {
            // Panggil fungsi cek serving di OrderManager
            boolean success = gp.orderM.checkServing(player.inventory);

            if (success) {
                // Makanan diterima: Hapus item dari tangan player
                player.inventory = null;
                System.out.println("Serving Berhasil!");
                // (Nanti di sini bisa tambah logika spawn piring kotor)
            } else {
                // Makanan salah: Player tetap pegang
                System.out.println("Pesanan Salah! Coba cek resep lagi.");
            }
        }
    }
}