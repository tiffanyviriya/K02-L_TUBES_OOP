package tile;

import environment.entity.Entity;
import environment.food_related.Ingredient;
import environment.item.KitchenUtensil;
import environment.item.Plate;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class CookingStation extends Tile {

    GamePanel gp;
    public KitchenUtensil utensilOnStation = null;

    /* Konstruktor untuk inisialisasi stasiun memasak (kompor) */
    public CookingStation(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.collision = true;
        loadStationImage();
    }

    /* Memuat gambar visual untuk kompor */
    private void loadStationImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cooking_station.png"));
        } catch (IOException e) {

        }
    }

    /* Menangani interaksi pemain: menaruh panci, memasukkan bahan, menyajikan ke piring, atau mengangkat panci */
    @Override
    public void interact(Entity player) {

        if (utensilOnStation == null) {
            if (player.inventory instanceof KitchenUtensil) {
                utensilOnStation = (KitchenUtensil) player.inventory;
                player.inventory = null;
            }
        }

        else {
            if (player.inventory instanceof Ingredient) {
                Ingredient in = (Ingredient) player.inventory;

                boolean success = utensilOnStation.addIngredient(in);

                if (success) {
                    player.inventory = null;
                }
            }

            else if (player.inventory instanceof Plate) {
                Plate plate = (Plate) player.inventory;
                java.util.ArrayList<Ingredient> food = utensilOnStation.serveToPlate();

                if (food != null) {
                    for (Ingredient i : food) {
                        plate.addItem(i);
                    }
                }
            }

            else if (player.inventory == null) {

                if (utensilOnStation != null) {
                    utensilOnStation.stopCookingSound();
                }

                player.inventory = utensilOnStation;
                utensilOnStation = null;
            }
        }
    }

    /* Memperbarui proses memasak jika ada peralatan masak di atas kompor */
    public void update() {
        if (utensilOnStation != null) {
            utensilOnStation.cook();
        }
    }

    /* Menggambar kompor dan peralatan masak yang ada di atasnya */
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
        if (utensilOnStation != null) {
            utensilOnStation.draw(g2, x, y);
        }
    }
}