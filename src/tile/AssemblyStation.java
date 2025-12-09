package tile;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

import environment.*;
import main.GamePanel;

public class AssemblyStation extends Tile {

    public Item itemOnTop = null;

    public AssemblyStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadStorageImage();
    }

    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/assembly-horizontal.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interact(Entity player) {
        // Logika Interaksi
        if (itemOnTop != null) {
            if (itemOnTop instanceof Plate && player.inventory != null && player.inventory instanceof Preparable) {
                ((Plate) itemOnTop).addItem((Preparable) player.inventory);
                player.inventory = null;
                System.out.println("Player menaruh bahan ke dalam piring di meja assembly.");
            }
            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari meja assembly.");
            }
            else {
                System.out.println("Tangan penuh! Tidak bisa menukar item saat ini.");
            }
        }
        else {
            if (player.inventory == null) {
                System.out.println("Meja kosong.");
            } else {
                itemOnTop = player.inventory;
                player.inventory = null;
                System.out.println("Player menaruh " + itemOnTop.name + " di meja assembly.");
            }
        }
    } // <--- PASTIKAN KURUNG KURAWAL INI ADA (Penutup method interact)

    // Method draw harus sejajar dengan interact (bukan di dalamnya)
    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Meja Assembly
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        // 2. Gambar Item di atas meja (Plate atau Ingredient)
        if (itemOnTop != null) {
            // Hitung posisi tengah agar rapi
            // (TileSize 48 - ItemSize 24) / 2 = 12 pixel offset
            int itemX = x + 12;
            int itemY = y + 12;

            // Panggil method draw milik Item (atau Plate)
            itemOnTop.draw(g2, itemX, itemY);
        }
    }

} // <--- Penutup Class AssemblyStation