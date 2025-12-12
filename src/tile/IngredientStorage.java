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

    public IngredientStorage(GamePanel gp, String ingredientName) {
        super(gp);
        this.gp = gp;
        this.ingredientName = ingredientName;

        this.collision = true;

        loadStorageImage();
    }

    private void loadStorageImage() {
        String specificPath = "/stations/ingredient-storage-" + ingredientName + ".png";

        try {
            image = ImageIO.read(getClass().getResourceAsStream(specificPath));
        } catch (Exception e) {
            try {
                image = ImageIO.read(getClass().getResourceAsStream("/stations/storage-sementara.png"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void interact(Entity player) {

        // KASUS 1: Ada Item di atas Storage
        if (itemOnTop != null) {

            // --- LOGIKA BARU: Tuang Panci ke Piring di Meja ---
            if (itemOnTop instanceof Plate && player.inventory instanceof KitchenUtensil) {
                Plate plate = (Plate) itemOnTop;
                KitchenUtensil utensil = (KitchenUtensil) player.inventory;

                ArrayList<Ingredient> food = utensil.serveToPlate();

                if (food != null) {
                    for (Ingredient i : food) {
                        plate.addItem(i);
                    }
                    System.out.println("Plating: Makanan dari " + utensil.name + " dituang ke Piring di Storage.");
                } else {
                    System.out.println("Gagal: Makanan belum matang atau gosong.");
                }
                return;
            }
            // --------------------------------------------------

            // A. Player bawa KitchenUtensil (Panci/Wajan)
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

            // B. Player bawa Piring (Plate)
            else if (player.inventory instanceof Plate) {
                Plate plate = (Plate) player.inventory;
                if (itemOnTop instanceof Ingredient) {
                    plate.addItem((Ingredient) itemOnTop);
                    itemOnTop = null;
                }
            }

            // C. Player Tangan Kosong
            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari atas storage.");
            }

            // D. Player bawa Ingredient -> Gabung ke Piring di Meja
            else if (player.inventory instanceof Ingredient && itemOnTop instanceof Plate) {
                ((Plate) itemOnTop).addItem((Ingredient) player.inventory);
                player.inventory = null;
            }
        }

        // KASUS 2: Storage Kosong (sebagai tempat spawn)
        else {
            // A. Player bawa Item -> Taruh item di atas Storage
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
                System.out.println("Player menaruh " + itemOnTop.name + " di atas storage.");
            }

            // B. Player Tangan Kosong -> Ambil Bahan Baru
            else {
                player.inventory = new Ingredient(gp, ingredientName);
                System.out.println("Player mengambil " + ingredientName + " baru.");
            }
        }
    }

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