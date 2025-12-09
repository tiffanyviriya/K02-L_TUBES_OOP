package tile;

import java.awt.*;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class Tile {
    GamePanel gp;
    public BufferedImage image;
    public boolean collision = false;

    public Tile(GamePanel gp){
        this.gp = gp;
    }

    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(this.image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
