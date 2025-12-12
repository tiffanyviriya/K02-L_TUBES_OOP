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

    public AssemblyStation(GamePanel gp, String type) {
        super(gp);
        this.collision = true;
        loadStationImage(type);
    }

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
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {
        // KASUS 1: Ada Item di atas Meja
        if (itemOnTop != null) {

            // --- LOGIKA BARU: Tuang Panci ke Piring di Meja ---
            if (itemOnTop instanceof Plate && player.inventory instanceof KitchenUtensil) {
                Plate plate = (Plate) itemOnTop;
                KitchenUtensil utensil = (KitchenUtensil) player.inventory;

                // Coba sajikan isi panci ke piring
                ArrayList<Ingredient> food = utensil.serveToPlate();

                if (food != null) {
                    for (Ingredient i : food) {
                        plate.addItem(i);
                    }
                    System.out.println("Plating: Makanan dari " + utensil.name + " dituang ke Piring di meja.");
                } else {
                    System.out.println("Gagal: Makanan belum matang atau gosong.");
                }
                return; // Selesai interaksi
            }
            // --------------------------------------------------

            // Interaksi Panci di Meja (Masukkan bahan)
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
            // Interaksi Piring di Meja (Tambahkan bahan dari tangan)
            else if (itemOnTop instanceof Plate && player.inventory instanceof Ingredient) {
                ((Plate) itemOnTop).addItem((Ingredient) player.inventory);
                player.inventory = null;
            }
            // Ambil Item (Jika tangan kosong)
            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
            }
        }
        // KASUS 2: Meja Kosong -> Taruh Item
        else {
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
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