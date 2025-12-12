package main.manager;

import environment.entity.Player;
import main.util.GamePanel;
import main.handler.KeyHandler;

import java.awt.*;

public class PlayerManager {
    private GamePanel gp;       // Tambahkan field
    private KeyHandler keyH;    // Tambahkan field

    private Player[] players = new Player[2];
    private int activeIndex = 0;

    public PlayerManager(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        reset(); // Panggil reset di awal untuk inisialisasi
    }

    // Method untuk mengembalikan Player ke kondisi awal
    public void reset() {
        players[0] = new Player(gp, keyH, 288, 240);
        players[1] = new Player(gp, keyH, 576, 240);
        activeIndex = 0;
    }

    public void switchPlayer() {
        activeIndex = (activeIndex + 1) % players.length;
    }

    public Player getActivePlayer() {
        return players[activeIndex];
    }

    public Player[] getPlayers(){
        return players;
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