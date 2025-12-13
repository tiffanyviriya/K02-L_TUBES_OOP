package main.manager;

import environment.entity.Player;
import main.handler.KeyHandler;
import main.util.GamePanel;

import java.awt.*;
import java.util.ArrayList;

/* Manajer untuk mengontrol entitas pemain, pergantian karakter, dan logika pembaruan */
public class PlayerManager {

    GamePanel gp;
    KeyHandler keyH;

    public ArrayList<Player> players = new ArrayList<>();

    public int activePlayerIndex = 0;

    /* Menginisialisasi manajer dan mengatur pemain awal */
    public PlayerManager(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setupPlayers();
    }

    /* Mengatur ulang daftar pemain dan menempatkan mereka di posisi awal */
    public void setupPlayers() {
        players.clear();

        players.add(new Player(gp, keyH, 5 * gp.tileSize, 5 * gp.tileSize));

        players.add(new Player(gp, keyH, 6 * gp.tileSize, 5 * gp.tileSize));

        activePlayerIndex = 0;
    }

    /* Mengatur ulang status pemain saat level dimulai ulang */
    public void reset() {
        setupPlayers();
    }

    /* Mengambil daftar semua pemain dalam bentuk array */
    public Player[] getPlayers() {
        return players.toArray(new Player[0]);
    }

    /* Memperbarui logika untuk pemain yang sedang aktif */
    public void update() {
        if (getActivePlayer() != null) {
            getActivePlayer().update();
        }
    }

    /* Menggambar semua pemain dan indikator panah pada pemain aktif */
    public void draw(Graphics2D g2) {
        for (Player p : players) {
            p.draw(g2);
        }

        Player active = getActivePlayer();
        if (active != null) {
            int arrowX = active.pos.x + 12;
            int arrowY = active.pos.y - 10;

            g2.setColor(Color.RED);
            int[] xPoints = {arrowX, arrowX + 8, arrowX - 8};
            int[] yPoints = {arrowY + 8, arrowY, arrowY};
            g2.fillPolygon(xPoints, yPoints, 3);
        }
    }

    /* Mengganti kontrol ke pemain berikutnya dalam daftar */
    public void switchPlayer() {
        if (players.isEmpty()) return;

        activePlayerIndex++;
        if (activePlayerIndex >= players.size()) {
            activePlayerIndex = 0;
        }
    }

    /* Mengembalikan referensi ke pemain yang sedang dikendalikan */
    public Player getActivePlayer() {
        if (players.isEmpty()) return null;
        return players.get(activePlayerIndex);
    }
}