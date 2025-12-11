package tile;

import environment.*;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;

public class AssemblyStation extends Tile {

    // Menyimpan item apa pun yang ada di atas meja (Piring, Panci, atau Bahan)
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
        
        // KASUS 1: Ada Item di atas Meja
        if (itemOnTop != null) {

            // A. Interaksi UTENSIL / PANCI (Fitur Baru: Masukkan bahan ke panci di meja)
            if (itemOnTop instanceof KitchenUtensil) {
                KitchenUtensil utensil = (KitchenUtensil) itemOnTop;

                // 1. Player bawa Ingredient -> Masukkan ke Panci
                if (player.inventory instanceof Ingredient) {
                    Ingredient ingredient = (Ingredient) player.inventory;

                    // Cek apakah bahan valid & panci belum matang
                    if (ingredient.canBeCooked() && !utensil.isCooked && !utensil.isBurned) {
                        utensil.addIngredient(ingredient);
                        player.inventory = null; // Bahan pindah ke panci
                        System.out.println("Player memasukkan " + ingredient.name + " ke panci di meja.");
                    } else {
                        System.out.println("Tidak bisa dimasukkan (Panci penuh/matang atau bahan salah).");
                    }
                }
                // 2. Player bawa Piring -> Plating (Ambil masakan matang dari panci)
                else if (player.inventory instanceof Plate) {
                    Plate plate = (Plate) player.inventory;
                    ArrayList<Ingredient> food = utensil.serveToPlate();
                    
                    if (food != null) {
                        for (Ingredient i : food) plate.addItem(i);
                        System.out.println("Plating makanan dari panci di meja.");
                    }
                }
                // 3. Player Tangan Kosong -> Ambil Panci
                else if (player.inventory == null) {
                    player.inventory = itemOnTop;
                    itemOnTop = null;
                    System.out.println("Player mengambil panci.");
                }
            }

            // B. Interaksi PLATE (Player bawa Bahan -> Taruh di Piring)
            else if (itemOnTop instanceof Plate && player.inventory instanceof Ingredient) {
                ((Plate) itemOnTop).addItem((Ingredient) player.inventory);
                player.inventory = null;
                System.out.println("Player menaruh bahan ke piring.");
            }
            
            // C. Interaksi UMUM (Player Tangan Kosong -> Ambil Item apa pun)
            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name);
            }
            
            else {
                System.out.println("Interaksi tidak valid / Tangan penuh.");
            }
        }
        
        // KASUS 2: Meja Kosong
        else {
            // Player menaruh item apa saja ke meja (Piring, Panci, Bahan)
            if (player.inventory != null) {
                itemOnTop = player.inventory;
                player.inventory = null;
                System.out.println("Player menaruh " + itemOnTop.name + " di meja.");
            } else {
                System.out.println("Meja kosong.");
            }
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        // 1. Gambar Meja
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        // 2. Gambar Item di atasnya
        if (itemOnTop != null) {
            // Logika Visual: Agar item terlihat rapi di tengah meja
            
            if (itemOnTop instanceof KitchenUtensil) {
                // Jika Panci (Besar), gambar pas di koordinat tile (48x48)
                itemOnTop.draw(g2, x, y);
            } else {
                // Jika Piring/Bahan (Kecil), gambar di tengah (Offset +12)
                int centerOffset = (gp.tileSize - gp.itemSize) / 2; 
                itemOnTop.draw(g2, x + centerOffset, y + centerOffset);
            }
        }
    }
}