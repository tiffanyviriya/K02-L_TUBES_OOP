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

    public CookingStation(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.collision = true;
        loadStationImage();
    }

    private void loadStationImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cooking_station.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {

        // KASUS 1: Kompor KOSONG
        if (utensilOnStation == null) {
            if (player.inventory instanceof KitchenUtensil) {
                utensilOnStation = (KitchenUtensil) player.inventory;
                player.inventory = null;
                System.out.println("Menaruh " + utensilOnStation.name + " di kompor.");
            }
        }

        // KASUS 2: Kompor ADA Panci/Wajan
        else {
            // A. Masukkan BAHAN
            if (player.inventory instanceof Ingredient) {
                Ingredient in = (Ingredient) player.inventory;

                // [FIX]: Langsung panggil addIngredient.
                // Biarkan Utensil yang validasi dan mainkan suara Error jika salah.
                boolean success = utensilOnStation.addIngredient(in);

                // Hapus dari inventory HANYA jika sukses masuk
                if (success) {
                    player.inventory = null;
                }
            }

            // B. Ambil Masakan dengan PLATE
            else if (player.inventory instanceof Plate) {
                Plate plate = (Plate) player.inventory;
                java.util.ArrayList<Ingredient> food = utensilOnStation.serveToPlate();

                if (food != null) {
                    for (Ingredient i : food) {
                        plate.addItem(i);
                    }
                    System.out.println("Masakan berhasil dipindah ke piring!");
                } else {
                    System.out.println("Belum matang atau gosong! Tidak bisa diambil.");
                }
            }

            // C. Angkat Panci (Tangan Kosong)
            else if (player.inventory == null) {
                player.inventory = utensilOnStation;
                utensilOnStation = null;
                System.out.println("Mengangkat panci/wajan.");
            }
        }
    }

    public void update() {
        if (utensilOnStation != null) {
            utensilOnStation.cook();
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
        if (utensilOnStation != null) {
            utensilOnStation.draw(g2, x, y);
        }
    }
}