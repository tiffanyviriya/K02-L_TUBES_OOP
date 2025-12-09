package tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import environment.Entity;
import main.GamePanel;

public class Tile {
    GamePanel gp;
    public BufferedImage image;
    public boolean collision = false;

    public Tile(GamePanel gp){
        this.gp = gp;
    }

    public void interact(Entity player) {
    }
}
