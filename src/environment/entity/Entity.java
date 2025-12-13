package environment.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import environment.item.Item;
import main.util.GamePanel;

public class Entity {

    GamePanel gp;
    public Position pos = new Position();
    public int speed;

    public BufferedImage up0, up1, up2, down0, down1, down2, left0, left1, left2, right0, right1, right2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    public Item inventory = null;

    public Entity(GamePanel gp) {
        this.gp = gp;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public static class Position {
        public int x;
        public int y;
    }
}

