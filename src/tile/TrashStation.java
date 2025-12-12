package tile;

import environment.entity.Entity;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Merepresentasikan stasiun tempat sampah (Trash Station).
 * - Jika membawa Bahan: Bahan hilang.
 * - Jika membawa Panci/Piring: Hanya isinya yang hilang (dibersihkan).
 */
public class TrashStation extends Tile {

    public TrashStation(GamePanel gp) {
        super(gp);
        this.collision = true;

        try {
            // Sesuaikan path ini dengan lokasi gambar Trash Station Anda
            // Berdasarkan file yang diupload: /Sprites_Overcooked/Sprites_Stations/Trash_Station.png
            // Atau jika Anda sudah memindahkannya ke folder /stations/:
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/stations/Trash_Station.png")));
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar Trash Station.");
        }
    }

    /**
     * Menangani logika interaksi untuk Trash Station.
     */
    @Override
    public void interact(Entity player) {
        // Cek apakah tangan pemain kosong
        if (player.inventory == null) {
            System.out.println("Trash Station: Tangan kosong. Tidak ada yang dibuang.");
            return;
        }

        // KASUS 1: Pemain membawa Panci/Wajan (KitchenUtensil)
        if (player.inventory instanceof KitchenUtensil) {
            KitchenUtensil utensil = (KitchenUtensil) player.inventory;

            // Cek apakah ada isinya
            if (!utensil.ingredients.isEmpty()) {
                // 1. Hapus semua bahan
                utensil.ingredients.clear();

                // 2. Reset status memasak
                utensil.isCooked = false;
                utensil.isBurned = false;
                utensil.cookingProgress = 0;

                // 3. Update gambar panci menjadi kosong
                utensil.updateLook();

                System.out.println("Trash Station: Isi " + utensil.name + " dibuang/dibersihkan.");
            } else {
                System.out.println("Trash Station: " + utensil.name + " sudah kosong.");
            }
        }

        // KASUS 2: Pemain membawa Piring (Plate)
        else if (player.inventory instanceof Plate) {
            Plate plate = (Plate) player.inventory;

            // Cek apakah ada makanan di piring
            if (!plate.itemOnPlate.isEmpty()) {
                // Hapus makanan dari piring
                plate.itemOnPlate.clear();
                System.out.println("Trash Station: Makanan di piring dibuang.");
            } else {
                System.out.println("Trash Station: Piring sudah bersih.");
            }
        }

        // KASUS 3: Pemain membawa Bahan Biasa (Ingredient) -> Buang Itemnya
        else {
            System.out.println("Trash Station: " + player.inventory.name + " dibuang.");
            player.inventory = null; // Item hilang dari tangan
        }
    }
}