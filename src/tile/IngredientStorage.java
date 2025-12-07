package tile;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import environment.Entity;
import environment.Ingredient;
import environment.Item;

public class IngredientStorage extends Tile {

    GamePanel gp;
    String ingredientName; // Contoh: "potato", "tomato", "lettuce"

    // Sesuai PDF: "Station ini juga dapat digunakan untuk menaruh bahan"
    // Jadi kita butuh variabel untuk menyimpan item yang ditaruh di atas crate
    public Item itemOnTop = null;

    public IngredientStorage(GamePanel gp, String ingredientName) {
        super(gp);
        this.gp = gp;
        this.ingredientName = ingredientName;

        // Storage bersifat solid (tidak bisa ditembus)
        this.collision = true;

        loadStorageImage();
    }

    private void loadStorageImage() {
        try {
            // Memuat gambar crate spesifik, misal: "crate_potato.png"
            // Pastikan Anda membuat gambar ini di folder /res/tiles/
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/OOPtile.png"));
        } catch (Exception e) {
            try {
                // Fallback ke gambar default jika gambar spesifik belum ada
                image = ImageIO.read(getClass().getResourceAsStream("/tiles/crate_default.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method logika interaksi utama.
     * Dipanggil oleh CollisionChecker saat pemain menekan tombol interaksi di depan storage ini.
     */
    public void interact(Entity player) {

        // KASUS 1: Ada item di atas crate (misal piring atau bahan lain yang ditaruh player sebelumnya)
        if (itemOnTop != null) {
            if (player.inventory == null) {
                // Player mengambil item yang ada di atas crate
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari atas storage.");
            } else {
                // Logika Plating (Advanced):
                // Jika player bawa piring bersih & di atas crate ada bahan matang -> Gabung ke piring (Plating)
                // Jika player bawa bahan & di atas crate ada piring -> Gabung ke piring
                System.out.println("Tangan penuh! Tidak bisa mengambil item.");
            }
        }

        // KASUS 2: Tidak ada item di atas crate (Crate murni sebagai spawner)
        else {
            if (player.inventory == null) {
                // SPESIFIKASI: Mengambil bahan mentah (RAW)
                // Spawn objek ingredient baru
                player.inventory = new Ingredient(gp, ingredientName);
                System.out.println("Player mengambil bahan baru: " + ingredientName);
            } else {
                // SPESIFIKASI: Menaruh item di atas station
                itemOnTop = player.inventory;
                player.inventory = null; // Kosongkan tangan player
                System.out.println("Player menaruh " + itemOnTop.name + " di atas storage " + ingredientName);
            }
        }
    }
}