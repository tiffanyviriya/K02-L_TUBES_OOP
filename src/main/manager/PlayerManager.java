package main.manager;

import environment.entity.Player;
import environment.entity.PlayerState; // Pastikan import ini ada
import main.util.GamePanel;
import main.handler.KeyHandler;

import java.awt.*;

public class PlayerManager {
    private GamePanel gp;
    private KeyHandler keyH;

    private Player[] players = new Player[2];
    private int activeIndex = 0;

    public PlayerManager(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        reset();
    }

    // Method untuk mengembalikan Player ke kondisi awal
    public void reset() {
        players[0] = new Player(gp, keyH, 288, 240);
        players[1] = new Player(gp, keyH, 576, 240);
        activeIndex = 0;
    }

    public void switchPlayer() {
        // [FIX] Reset status player yang lama ke IDLE sebelum pindah
        // Ini mencegah player lama "stuck" dalam kondisi BUSY (memasak/memotong)
        if (getActivePlayer() != null) {
            getActivePlayer().playerState = PlayerState.IDLE;
        }

        // Pindah index ke player berikutnya
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

        // Opsional: Gambar indikator (panah) di atas player yang aktif
        Player active = getActivePlayer();
        if (active != null) {
            g2.setColor(Color.WHITE);
            // Gambar segitiga kecil di atas kepala
            int [] xPoints = {active.pos.x + 16, active.pos.x + 8, active.pos.x + 24};
            int [] yPoints = {active.pos.y - 10, active.pos.y - 20, active.pos.y - 20};
            g2.fillPolygon(xPoints, yPoints, 3);
        }
    }
}