package main;

import java.awt.*;

public class PlayScene {
    GamePanel gp;

    public PlayScene(GamePanel gp){
        this.gp = gp;
    }

    public void update(){
        gp.playerM.update();
    }

    public void draw(Graphics2D g2){
        gp.tileM.draw(g2);
        gp.itemM.draw(g2);
        gp.playerM.draw(g2);
    }
}
