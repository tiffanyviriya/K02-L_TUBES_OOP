package environment.item;

import java.awt.*;
import java.awt.image.BufferedImage;

import main.util.GamePanel;

public class Item {
    public Rectangle solidArea;
    protected GamePanel gp;
    public String name;
    public BufferedImage image;
    public boolean collision = false;
    public boolean isHold = false;

    public int worldX, worldY;

    public int solidAreaDefaultX, solidAreaDefaultY;

    public Item(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2, int x, int y) {
        worldX = x;
        worldY = y;

        if (image != null) {
            g2.drawImage(image, worldX, worldY, gp.itemSize , gp.itemSize , null);
        }
    }

}
