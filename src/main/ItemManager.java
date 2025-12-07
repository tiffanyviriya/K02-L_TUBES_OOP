package main;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import environment.Item;

public class ItemManager {
    GamePanel gp;
    // Menggunakan ArrayList agar dinamis (bisa menampung banyak item tanpa batas array fix)
    public ArrayList<Item> itemsOnFloor = new ArrayList<>();

    public ItemManager(GamePanel gp) {
        this.gp = gp;
    }

    public void addItem(Item item, int col, int row) {
        // Set posisi item sesuai grid
        item.worldX = col * gp.tileSize;
        item.worldY = row * gp.tileSize;

        // Tambahkan ke list untuk dirender dan dicek interaksinya
        itemsOnFloor.add(item);
    }

    /**
     * Mengecek apakah ada item di koordinat (col, row) tertentu.
     * Jika ada, item diambil (dihapus dari lantai) dan dikembalikan ke Player.
     */
    public Item pickUpItem(int col, int row) {

        Iterator<Item> iterator = itemsOnFloor.iterator();

        while (iterator.hasNext()) {
            Item item = iterator.next();

            int itemCol = item.worldX / gp.tileSize;
            int itemRow = item.worldY / gp.tileSize;

            if (itemCol == col && itemRow == row) {
                iterator.remove(); // Hapus dari lantai
                return item; // Kembalikan item ke player
            }
        }
        return null; // Tidak ada item di lokasi tersebut
    }

    public void draw(Graphics2D g2) {
        for (Item item : itemsOnFloor) {
            if (item != null) {
                item.draw(g2);
            }
        }
    }
}
