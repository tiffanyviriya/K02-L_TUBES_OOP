package tile;

import environment.entity.Entity;
import environment.food_related.Ingredient;
import environment.item.Item;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;

public class AssemblyStation extends Tile {

    public Item itemOnTop = null;

    // [UBAH]: Tambahkan parameter 'type' di constructor
    public AssemblyStation(GamePanel gp, String type) {
        super(gp);
        this.collision = true;
        loadStationImage(type); // Panggil fungsi load dengan tipe
    }

    // [UBAH]: Terima parameter type untuk menentukan gambar
    private void loadStationImage(String type) {
        String path = "";

        try {
            if (type.equals("vertical")) {
                // Pastikan nama file sesuai dengan yang ada di folder res Anda
                path = "/stations/vertikal.png";
            } else {
                // Default ke horizontal
                path = "/stations/horizontal.png";
            }

            image = ImageIO.read(getClass().getResourceAsStream(path));

        } catch (Exception e) {
            System.out.println("Gagal load gambar: " + path);
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {
        // ... (Isi logika interact TETAP SAMA, tidak perlu diubah) ...
        // Copy paste logika interact yang sudah ada sebelumnya

        // KASUS 1: Ada Item di atas Meja
        if (itemOnTop != null) {
            if (itemOnTop instanceof KitchenUtensil) {
                KitchenUtensil utensil = (KitchenUtensil) itemOnTop;
                if (player.inventory instanceof Ingredient) {
                    Ingredient ingredient = (Ingredient) player.inventory;
                    if (ingredient.canBeCooked() && !utensil.isCooked && !utensil.isBurned) {
                        utensil.addIngredient(ingredient);
                        player.inventory = null;
                    }
                }
                else if (player.inventory instanceof Plate) {
                    Plate plate = (Plate) player.inventory;
                    ArrayList<Ingredient> food = utensil.serveToPlate();
                    if (food != null) {
                        for (Ingredient i : food) plate.addItem(i);
                    }
                }
                else if (player.inventory == null) {
                    player.inventory = itemOnTop;
                    itemOnTop = null;
                }
            }
            else if (itemOnTop instanceof Plate && player.inventory instanceof Ingredient) {
                ((Plate) itemOnTop).addItem((Ingredient) player.inventory);
                player.inventory = null;
            }
            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
            }
        }
        // KASUS 2: Meja Kosong
        else {
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
            }
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        // ... (Isi draw TETAP SAMA, tidak perlu diubah) ...
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
        if (itemOnTop != null) {
            if (itemOnTop instanceof KitchenUtensil) {
                itemOnTop.draw(g2, x, y);
            } else {
                int centerOffset = (gp.tileSize - gp.itemSize) / 2;
                itemOnTop.draw(g2, x + centerOffset, y + centerOffset);
            }
        }
    }
}