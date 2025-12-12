package tile;

import environment.Entity;
import environment.Ingredient;
import environment.KitchenUtensil;
import environment.Plate;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class CookingStation extends Tile {

    GamePanel gp;
    // Variabel untuk menyimpan Panci/Wajan yang ada di atas kompor
    public KitchenUtensil utensilOnStation = null;

    public CookingStation(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.collision = true; // Supaya player tidak bisa menembus kompor

        loadStationImage();
    }

    private void loadStationImage() {
        try {
            // Pastikan path gambar station benar
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cooking_station.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- LOGIKA UTAMA INTERAKSI ---
    @Override
    public void interact(Entity player) {

        // KASUS 1: Kompor KOSONG (Tidak ada panci/wajan)
        if (utensilOnStation == null) {
            // Jika player memegang Panci/Wajan -> Taruh di kompor
            if (player.inventory instanceof KitchenUtensil) {
                utensilOnStation = (KitchenUtensil) player.inventory;
                player.inventory = null; // Tangan player jadi kosong
                System.out.println("Menaruh " + utensilOnStation.name + " di kompor.");
            }
        }

        // KASUS 2: Kompor ADA Panci/Wajan
        else {
            // A. Player membawa BAHAN (Ingredient) -> Masukkan ke Panci
            if (player.inventory instanceof Ingredient) {
                Ingredient in = (Ingredient) player.inventory;
                
                // Cek apakah bahan bisa dimasak (opsional, tergantung logic Ingredient)
                if (in.canBeCooked()) {
                    utensilOnStation.addIngredient(in);
                    player.inventory = null; // Bahan pindah ke panci
                }
            }

            // B. Player membawa PIRING (Plate) -> Angkat masakan matang (Plating)
            // --- TAMBAHKAN LOGIKA INI ---
        // Sub-kasus B: Player bawa PIRING -> Plating (Ambil masakan)
        else if (player.inventory instanceof Plate) {
            Plate plate = (Plate) player.inventory;

            // Panggil method serveToPlate dari utensil
            java.util.ArrayList<Ingredient> food = utensilOnStation.serveToPlate();

            if (food != null) {
                // Pindahkan semua bahan ke piring
                for (Ingredient i : food) {
                    plate.addItem(i);
                }
                System.out.println("Masakan berhasil dipindah ke piring!");
            } else {
                System.out.println("Belum matang atau gosong! Tidak bisa diambil.");
            }
        }
        

            // C. Player TANGAN KOSONG -> Angkat Panci/Wajan
            else if (player.inventory == null) {
                player.inventory = utensilOnStation;
                utensilOnStation = null; // Kompor jadi kosong
                System.out.println("Mengangkat panci/wajan.");
            }
        }
    }

    // --- UPDATE LOOP (Untuk Memasak) ---
    // Method ini WAJIB dipanggil di TileManager.update()
    public void update() {
        if (utensilOnStation != null) {
            // Panaskan panci setiap frame
            utensilOnStation.cook();
        }
    }

    // --- RENDER ---
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Kompor
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        // 2. Gambar Panci/Wajan di atasnya (Jika ada)
        if (utensilOnStation != null) {
            // Panggil draw milik KitchenUtensil (biar progress bar & perubahan gambar muncul)
            utensilOnStation.draw(g2, x, y);
        }
    }
}
