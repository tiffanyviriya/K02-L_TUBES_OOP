package main.manager;

import java.awt.Graphics2D;
import java.util.ArrayList;

import environment.entity.Entity;
import environment.item.Item;
import main.util.GamePanel;

/* Manajer untuk mengelola item yang diletakkan di lantai permainan */
public class ItemManager {
    protected GamePanel gp;
    public ArrayList<Item> itemsOnFloor = new ArrayList<>();

    /* Menginisialisasi manajer item */
    public ItemManager(GamePanel gp) {
        this.gp = gp;
    }

    /* Menghapus semua item dari lantai saat permainan direset */
    public void reset() {
        itemsOnFloor.clear();
    }

    /* Menambahkan item ke posisi tertentu di lantai */
    public void addItem(Item item, int x, int y) {
        item.worldX = x;
        item.worldY = y;
        itemsOnFloor.add(item);
    }

    /* Mengambil item dari lantai yang bersentuhan dengan pemain */
    public Item getItemOnPlayer(Entity player) {
        Item item = gp.cChecker.checkItem(player);

        if (item != null) {
            itemsOnFloor.remove(item);
        }

        return item;
    }

    /* Menggambar semua item yang ada di lantai ke layar */
    public void draw(Graphics2D g2) {
        for (Item item : itemsOnFloor) {
            if (item != null) {
                item.draw(g2, item.worldX, item.worldY);
            }
        }
    }
}