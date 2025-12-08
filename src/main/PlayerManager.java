package main;

import environment.Player;

import java.awt.*;

public class PlayerManager {
    private Player[] players = new Player[2];
    private int activeIndex = 0;

    public PlayerManager(GamePanel gp, KeyHandler keyH){
        players[0] = new Player(gp, keyH, 288, 240);
        players[1] = new Player(gp, keyH, 576, 240);
    }

    public void switchPlayer() {
        activeIndex = (activeIndex + 1) % players.length;
    }

    public Player getActivePlayer() {
        return players[activeIndex];
    }

    public void update() {
        getActivePlayer().update();
    }

    public void draw(Graphics2D g2) {
        for (Player p : players) {
            p.draw(g2);
        }
    }
}
