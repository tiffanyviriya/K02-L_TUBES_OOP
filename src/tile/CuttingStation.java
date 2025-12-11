package tile;

import environment.*;
import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;

public class CookingStation extends Tile {

    public KitchenUtensil utensilOnStation = null;

    public CookingStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cooking_station.png"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Method interaksi Player dengan Kompor
    @Override
    public void interact(Entity player) {
        
        // KASUS A: Kompor KOSONG
        if (utensilOnStation == null) {
            // Jika Player bawa Panci -> Taruh Panci
            if (player.inventory instanceof KitchenUtensil) {
                utensilOnStation = (KitchenUtensil) player.inventory;
                player.inventory = null;
                System.out.println("Menaruh panci di kompor.");
            }
        }
        
        // KASUS B: Kompor ADA Panci
        else {
            // 1. Player bawa Bahan -> Masukkan ke Panci
            if (player.inventory instanceof Ingredient) {
                Ingredient in = (Ingredient) player.inventory;
                if (in.canBeCooked()) { // Cek apakah bahan mentah/potong
                    utensilOnStation.addIngredient(in);
                    player.inventory = null;
                }
            }
            
            // 2. Player bawa Piring -> Pindahkan Makanan Matang (Plating)
            else if (player.inventory instanceof Plate) {
                Plate plate = (Plate) player.inventory;
                
                // Coba tuang dari panci ke piring
                ArrayList<Ingredient> food = utensilOnStation.serveToPlate();
                
                if (food != null) {
                    // Masukkan semua isi panci ke piring
                    for(Ingredient i : food) {
                        plate.addItem(i);
                    }
                    System.out.println("Makanan dipindah ke piring!");
                } else {
                    System.out.println("Belum matang atau gosong!");
                }
            }
            
            // 3. Player Tangan Kosong -> Angkat Panci
            else if (player.inventory == null) {
                player.inventory = utensilOnStation;
                utensilOnStation = null;
                System.out.println("Mengangkat panci.");
            }
        }
    }

    // PENTING: Panggil method ini di GamePanel.update() atau TileManager.update()
    // Agar masakan bisa matang
    public void update() {
        if (utensilOnStation != null) {
            // Panci dipanaskan
            utensilOnStation.cook();
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Kompor
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);

        // 2. Gambar Panci (Jika ada)
        if (utensilOnStation != null) {
            // Panggil draw milik KitchenUtensil biar bar-nya muncul
            // Kita sesuaikan posisi sedikit biar pas di tengah kompor
            utensilOnStation.draw(g2, x, y); 
        }
    }
}
