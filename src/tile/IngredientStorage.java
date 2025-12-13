package tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;

import environment.entity.Entity;
import environment.food_related.Dish;
import environment.food_related.Ingredient;
import environment.item.Item;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

public class IngredientStorage extends Tile {

    GamePanel gp;
    String ingredientName;

    public Item itemOnTop = null;

    /* Konstruktor untuk inisialisasi penyimpanan bahan dengan nama bahan spesifik */
    public IngredientStorage(GamePanel gp, String ingredientName) {
        super(gp);
        this.gp = gp;
        this.ingredientName = ingredientName;

        this.collision = true;

        loadStorageImage();
    }

    /* Memuat gambar visual untuk penyimpanan bahan berdasarkan namanya */
    private void loadStorageImage() {
        String specificPath = "/stations/ingredient-storage-" + ingredientName + ".png";

        try {
            image = ImageIO.read(getClass().getResourceAsStream(specificPath));
        } catch (Exception e) {
            try {
                image = ImageIO.read(getClass().getResourceAsStream("/stations/storage-sementara.png"));
            } catch (Exception ex) {

            }
        }
    }

    /* Menangani interaksi pemain mengambil bahan, menaruh item, atau memproses item di atas meja */
    @Override
    public void interact(Entity player) {

        if (itemOnTop != null) {

            if (itemOnTop instanceof Plate && player.inventory instanceof KitchenUtensil) {
                Plate plate = (Plate) itemOnTop;
                KitchenUtensil utensil = (KitchenUtensil) player.inventory;

                ArrayList<Ingredient> food = utensil.serveToPlate();

                if (food != null) {
                    for (Ingredient i : food) {
                        plate.addItem(i);
                    }
                }
                return;
            }

            if (player.inventory instanceof KitchenUtensil) {
                KitchenUtensil utensil = (KitchenUtensil) player.inventory;

                if (itemOnTop instanceof Ingredient) {
                    Ingredient ingredient = (Ingredient) itemOnTop;
                    if (ingredient.canBeCooked() && !utensil.isCooked && !utensil.isBurned) {
                        utensil.addIngredient(ingredient);
                        itemOnTop = null;
                    }
                }
            }

            else if (player.inventory instanceof Plate) {
                Plate plate = (Plate) player.inventory;
                if (itemOnTop instanceof Ingredient) {
                    plate.addItem((Ingredient) itemOnTop);
                    itemOnTop = null;
                }
            }

            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
            }

            else if (player.inventory instanceof Ingredient && itemOnTop instanceof Plate) {
                ((Plate) itemOnTop).addItem((Ingredient) player.inventory);
                player.inventory = null;
            }
        }

        else {
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
            }

            else {
                player.inventory = new Ingredient(gp, ingredientName);
            }
        }
    }

    /* Menggambar penyimpanan bahan dan item yang ada di atasnya */
    public void draw(Graphics2D g2, int x, int y) {
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