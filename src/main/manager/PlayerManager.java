package main.manager;

import environment.entity.Player;
import main.handler.KeyHandler;
import main.util.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class PlayerManager {

    GamePanel gp;
    KeyHandler keyH;

    // ArrayList Player (Public agar bisa diakses fitur Lempar di Player.java)
    public ArrayList<Player> players = new ArrayList<>();

    public int activePlayerIndex = 0;

    public PlayerManager(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setupPlayers();
    }

    public void setupPlayers() {
        players.clear();

        // [UBAH KOORDINAT DI SINI]
        // Ganti 10,10 menjadi posisi yang aman (misal 4,4 atau 5,5)
        // Chef 1 di (5, 5)
        players.add(new Player(gp, keyH, 5 * gp.tileSize, 5 * gp.tileSize));

        // Chef 2 di (6, 5) - Sebelahnya
        players.add(new Player(gp, keyH, 6 * gp.tileSize, 5 * gp.tileSize));

        activePlayerIndex = 0;
    }

    // [FIX 1] Method reset() dipanggil oleh GamePanel saat restart/ganti level
    public void reset() {
        setupPlayers();
    }

    // [FIX 2] Method getPlayers() dipanggil oleh CollisionChecker
    // Mengembalikan Array karena CollisionChecker mengharapkan Player[]
    public Player[] getPlayers() {
        return players.toArray(new Player[0]);
    }

    public void update() {
        if (getActivePlayer() != null) {
            getActivePlayer().update();
        }
    }

    public void draw(Graphics2D g2) {
        for (Player p : players) {
            p.draw(g2);
        }

        // Gambar panah di atas player aktif
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

    public void switchPlayer() {
        if (players.isEmpty()) return;

        activePlayerIndex++;
        if (activePlayerIndex >= players.size()) {
            activePlayerIndex = 0;
        }
        System.out.println("Switched to Chef " + (activePlayerIndex + 1));
    }

    public Player getActivePlayer() {
        if (players.isEmpty()) return null;
        return players.get(activePlayerIndex);
    }
}