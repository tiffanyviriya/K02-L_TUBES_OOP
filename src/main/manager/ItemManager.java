package main.manager;

import java.awt.Graphics2D;
import java.util.ArrayList;

import environment.entity.Entity;
import environment.item.Item;
import main.util.GamePanel;

public class ItemManager {
    protected GamePanel gp;
    public ArrayList<Item> itemsOnFloor = new ArrayList<>();

    public ItemManager(GamePanel gp) {
        this.gp = gp;
    }

    public void addItem(Item item, int x, int y) {
        item.worldX = x;
        item.worldY = y;
        itemsOnFloor.add(item);
    }

    public Item getItemOnPlayer(Entity player) {
        Item item = gp.cChecker.checkItem(player);

        if (item != null) {
            itemsOnFloor.remove(item);
        }

        return item;
    }

    public void draw(Graphics2D g2) {
        for (Item item : itemsOnFloor) {
            if (item != null) {
                item.draw(g2, item.worldX, item.worldY);
            }
        }
    }
}
