package tile;

import environment.Entity;
// Import KitchenUtensil dihapus karena logika spesifiknya tidak lagi digunakan
import environment.Ingredient;
import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * Merepresentasikan stasiun tempat sampah (Trash Station).
 * Membuang item apapun yang dipegang pemain sepenuhnya.
 * [T]
 */
public class TrashStation extends Tile {


    public TrashStation(GamePanel gp) {
        super(gp);
        this.collision = true;

        try {
            // Placeholder: Ganti dengan gambar tempat sampah yang sesuai
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/stations/trash_station.png")));
        } catch (IOException e) {
            System.err.println("Gagal memuat gambar Trash Station. Menggunakan gambar default.");
        }
    }

    /**
     * Menangani logika interaksi untuk Trash Station.
     * Item yang dipegang pemain akan dibuang sepenuhnya.
     * @param player Entity (Pemain) yang berinteraksi dengan stasiun.
     */
    @Override
    public void interact(Entity player) {
        if (player.inventory == null) {
            System.out.println("Trash Station: Tangan kosong. Tidak ada yang dibuang.");
            return;
        } else {
            player.inventory = null;
        }

        // --- LOGIKA SEDERHANA: BUANG SEMUA ITEM ---

//        // PROSES: Item apapun yang dibawa pemain dibuang sepenuhnya.
//        String itemName = player.inventory.name;
//        player.inventory = null;
//        System.out.println("Trash Station: Item '" + itemName + "' sepenuhnya dibuang.");
    }
}