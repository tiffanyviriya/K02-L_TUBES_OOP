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
        // Coba load gambar spesifik, misal: "/statio
        String specificPath = "/stations/ingredient-storage-" + ingredientName + ".png";

        try {
            image = ImageIO.read(getClass().getResourceAsStream(specificPath));
        } catch (Exception e) {
            // Jika gambar spesifik tidak ada, gunakan default "storage-sementara.png"
            try {
                image = ImageIO.read(getClass().getResourceAsStream("/stations/storage-sementara.png"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void interact(Entity player) {
        // KASUS 1: Ada item di atas Storage
        if (itemOnTop != null) {
            if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari atas storage.");
            }
            else {
                System.out.println("Tangan penuh! Tidak bisa mengambil item.");
            }
        }
        // KASUS 2: Storage KOSONG -> Spawn Bahan Baru
        else {
            if (player.inventory == null) {
                // Spawn bahan sesuai nama ingredientName (misal: "rice", "fish")
                player.inventory = new Ingredient(gp, ingredientName);
                System.out.println("Player mengambil " + ingredientName + " baru.");
            }
            else {
                // Taruh item player ke atas storage
                itemOnTop = player.inventory;
                player.inventory = null;
                System.out.println("Player menaruh " + itemOnTop.name + " di atas storage.");
            }
        }
    }
}