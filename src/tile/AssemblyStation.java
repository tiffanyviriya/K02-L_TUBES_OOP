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

    /* Konstruktor untuk inisialisasi meja perakitan berdasarkan tipe orientasi */
    public AssemblyStation(GamePanel gp, String type) {
        super(gp);
        this.collision = true;
        loadStationImage(type);
    }

    /* Memuat gambar visual meja perakitan sesuai tipe (vertikal/horizontal) */
    private void loadStationImage(String type) {
        String path = "";
        try {
            if (type.equals("vertical")) {
                path = "/stations/vertikal.png";
            } else {
                path = "/stations/horizontal.png";
            }
            image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {

        }
    }

    /* Menangani interaksi pemain: menaruh item, menggabungkan bahan, atau memindahkan makanan antar wadah */
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
        else {
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
            }
        }
    }

    /* Menggambar meja perakitan dan item yang ada di atasnya */
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