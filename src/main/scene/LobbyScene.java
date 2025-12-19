package main.scene;

import main.util.GamePanel;
import main.handler.LobbySceneMouseHandler; // Akan kita buat dummy-nya
import java.awt.*;
import java.awt.event.MouseEvent;

public class LobbyScene implements Scene {
    private GamePanel gp;
    private LobbySceneMouseHandler mouseHandler;

    // Font
    private Font bigFont = new Font("Arial", Font.BOLD, 40);
    private Font smallFont = new Font("Arial", Font.PLAIN, 20);

    public LobbyScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new LobbySceneMouseHandler(this);
    }

    @Override
    public void update() {
        // Khusus Player 1 (Host), cek tombol 'P' untuk Start Game
        if (gp.netClient.isConnected && gp.netClient.myPlayerId == 0) {
            if (gp.keyH.pPressed) {
                tryStartGame();
                // Delay kecil agar tidak spam
                try { Thread.sleep(300); } catch (Exception e){}
            }
        }
    }

    // Method dipanggil dari KeyHandler secara langsung jika ada input P
    public void tryStartGame() {
        if (gp.netClient.myPlayerId == 0) { // Hanya Host
            gp.netClient.sendCommand("START_GAME");
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // Background Hitam
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(bigFont);

        String title = "LOBBY MULTIPLAYER";
        int x = getXforCenteredText(g2, title);
        g2.drawString(title, x, 100);

        // Status
        g2.setFont(smallFont);
        String status = "Menunggu koneksi...";
        if (gp.netClient.isConnected) {
            status = "Terhubung! ID Kamu: Player " + (gp.netClient.myPlayerId + 1);
        } else {
            status = "Menghubungkan ke Server...";
        }

        x = getXforCenteredText(g2, status);
        g2.drawString(status, x, 200);

        // Instruksi
        if (gp.netClient.myPlayerId == 0) {
            g2.setColor(Color.YELLOW);
            String inst = "Kamu adalah HOST. Tekan 'P' untuk memulai game.";
            x = getXforCenteredText(g2, inst);
            g2.drawString(inst, x, 300);
        } else if (gp.netClient.myPlayerId == 1) {
            g2.setColor(Color.CYAN);
            String inst = "Kamu adalah PLAYER 2. Menunggu Host memulai...";
            x = getXforCenteredText(g2, inst);
            g2.drawString(inst, x, 300);
        }
    }

    private int getXforCenteredText(Graphics2D g2, String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}