package tile;

import java.io.IOException;
import javax.imageio.ImageIO;

import environment.*;
import main.GamePanel;

public class IngredientStorage extends Tile {

    GamePanel gp;
    String ingredientName;

    public Item itemOnTop = null;

    public IngredientStorage(GamePanel gp, String ingredientName) {
        super(gp);
        this.gp = gp;
        this.ingredientName = ingredientName;

        this.collision = true;

        loadStorageImage();
    }

    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/storage-sementara.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {
        // KASUS 1: Ada item di atas Storage (Berperilaku seperti meja)
        if (itemOnTop != null) {
            // Jika tangan player kosong, AMBIL item dari atas storage
            if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari atas storage.");
            }
            // Jika player membawa piring dan di atas storage ada makanan (opsional logic)
            // ... (bisa ditambahkan nanti)
            else {
                System.out.println("Tangan penuh! Tidak bisa mengambil item.");
            }
        }
        // KASUS 2: Storage KOSONG (Berperilaku sebagai Spawner)
        else {
            // Jika tangan player kosong -> SPAWN Bahan Baru
            if (player.inventory == null) {
                player.inventory = new Ingredient(gp, ingredientName);
                System.out.println("Player mengambil " + ingredientName + " baru.");
            }
            // Jika tangan player ada item -> TARUH item tersebut di atas storage
            else {
                itemOnTop = player.inventory;
                player.inventory = null;
                System.out.println("Player menaruh " + itemOnTop.name + " di atas storage.");
            }
        }
    }
}