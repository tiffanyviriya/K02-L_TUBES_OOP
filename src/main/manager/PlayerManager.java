package main.manager;

import environment.entity.Player;
import main.handler.KeyHandler;
import main.util.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class PlayerManager {

    GamePanel gp;
    KeyHandler keyH;

    public ArrayList<Player> players = new ArrayList<>();
    public int activePlayerIndex = 0;

    public PlayerManager(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setupPlayers();
    }

    public void setupPlayers() {
        players.clear();

        // Player 1 (Index 0)
        Player p1 = new Player(gp, keyH, 5 * gp.tileSize, 5 * gp.tileSize);
        p1.myIndex = 0; // Set index manual (harus tambah field di Player)
        players.add(p1);

        // Player 2 (Index 1)
        Player p2 = new Player(gp, keyH, 6 * gp.tileSize, 5 * gp.tileSize);
        p2.myIndex = 1;
        players.add(p2);

        activePlayerIndex = 0;
    }

    public void reset() {
        setupPlayers();
    }

    public Player[] getPlayers() {
        return players.toArray(new Player[0]);
    }

    public void update() {
        // Update SEMUA player, bukan cuma yang aktif.
        // Karena di multiplayer, kedua player bergerak bersamaan.
        for (Player p : players) {
            p.update();
        }
    }

    public void draw(Graphics2D g2) {
        for (Player p : players) {
            p.draw(g2);
        }

        // Draw arrow indicator di atas kepala player KITA
        // Logic: kalau multiplayer, tunjukkan di player kita saja
        // Kalau singleplayer, tunjukkan di activePlayer

        Player indicatorTarget = null;

        if (gp.netClient.isConnected && gp.netClient.myPlayerId != -1) {
            // Multiplayer: Tunjuk diri sendiri
            if (gp.netClient.myPlayerId < players.size()) {
                indicatorTarget = players.get(gp.netClient.myPlayerId);
            }
        } else {
            // Singleplayer fallback
            indicatorTarget = getActivePlayer();
        }

        if (indicatorTarget != null) {
            int arrowX = indicatorTarget.pos.x + 12;
            int arrowY = indicatorTarget.pos.y - 10;
            g2.setColor(Color.RED);
            int[] xPoints = {arrowX, arrowX + 8, arrowX - 8};
            int[] yPoints = {arrowY + 8, arrowY, arrowY};
            g2.fillPolygon(xPoints, yPoints, 3);

            // Tambahan teks "P1" atau "P2"
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString("YOU", arrowX - 10, arrowY - 2);
        }
    }

    public void switchPlayer() {
        if (gp.netClient.isConnected) return;

        if (players.isEmpty()) return;
        activePlayerIndex++;
        if (activePlayerIndex >= players.size()) {
            activePlayerIndex = 0;
        }
    }

    public Player getActivePlayer() {
        if (players.isEmpty()) return null;
        return players.get(activePlayerIndex);
    }
}