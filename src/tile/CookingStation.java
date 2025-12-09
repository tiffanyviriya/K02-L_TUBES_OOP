package tile;

import main.GamePanel;
import environment.Entity;
import environment.KitchenUtensil;
import environment.Ingredient;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class CookingStation extends Tile {

    GamePanel gp;
    public KitchenUtensil utensil; // Alat masak yang sedang ditaruh di kompor

    public CookingStation(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.collision = true; // Tidak bisa ditembus player
        
        try {
            // Gambar kompor
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/stove.png")); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dipanggil setiap frame oleh GamePanel (harus ditambahkan di update loop)
    public void update() {
        if (utensil != null) {
            utensil.update(); // Jalankan proses memasak
        }
    }

    // Logika Interaksi Player dengan Kompor
    public void interact(Entity player) {
        
        // KASUS 1: Kompor Kosong
        if (utensil == null) {
            // Jika player membawa Alat Masak (Pot/Pan), taruh di kompor
            if (player.inventory instanceof KitchenUtensil) {
                utensil = (KitchenUtensil) player.inventory;
                player.inventory = null;
                
                // Set posisi visual utensil ke kompor
                // Kita perlu cara akses posisi tile ini. 
                // Biasanya interact dipanggil dengan mengetahui target tile.
                // Disini kita set manual saat render nanti, atau update worldX/Y utensil
            }
        }
        
        // KASUS 2: Ada Alat Masak di Kompor
        else {
            // A. Player Tangan Kosong -> Ambil Alat Masak
            if (player.inventory == null) {
                player.inventory = utensil;
                utensil = null;
                System.out.println("Player mengangkat alat masak.");
            }
            
            // B. Player Bawa Bahan -> Masukkan ke Alat Masak
            else if (player.inventory instanceof Ingredient) {
                Ingredient ing = (Ingredient) player.inventory;
                if (utensil.canAccept(ing)) {
                    utensil.addIngredient(ing);
                    player.inventory = null; // Bahan masuk ke panci
                } else {
                    System.out.println("Bahan tidak cocok untuk alat ini!");
                }
            }
        }
    }
    
    // Custom draw untuk menggambar utensil di atas kompor
    public void draw(Graphics2D g2, int x, int y) {
        // Gambar Kompor
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        
        // Gambar Utensil di atasnya (jika ada)
        if (utensil != null) {
            utensil.worldX = x + 4; // Sedikit offset biar pas di tengah
            utensil.worldY = y - 8; // Sedikit ke atas (perspektif)
            utensil.draw(g2);
        }
    }
}
