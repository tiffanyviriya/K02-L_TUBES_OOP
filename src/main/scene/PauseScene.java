package main.scene;

import main.util.GamePanel;
import main.handler.PauseSceneMouseHandler;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PauseScene implements Scene {

    protected GamePanel gp;
    private PauseSceneMouseHandler mouseHandler;

    private Rectangle resumeButton;
    private Rectangle menuButton;

    private boolean resumeHover = false;
    private boolean menuHover = false;

    public PauseScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new PauseSceneMouseHandler(this);

        int btnWidth = 200;
        int btnHeight = 50;
        int btnX = (gp.screenWidth / 2) - (btnWidth / 2);
        int startY = (gp.screenHeight / 2) - 50;

        resumeButton = new Rectangle(btnX, startY, btnWidth, btnHeight);
        menuButton = new Rectangle(btnX, startY + 70, btnWidth, btnHeight);
    }

    @Override
    public void update() {
        // Logika pause (biasanya kosong atau animasi tombol sederhana)
    }

    @Override
    public void draw(Graphics2D g2) {
        // --- OVERLAY TRANSPARAN ---
        // Warna Hitam (0,0,0) dengan Alpha 150 (0-255).
        // Semakin tinggi Alpha, semakin gelap.
        g2.setColor(new Color(0, 0, 0, 150));

        // Menggambar kotak memenuhi satu layar penuh
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // --- GAMBAR JUDUL & TOMBOL ---
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "PAUSED";
        int textWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - textWidth) / 2, resumeButton.y - 60);

        drawButton(g2, resumeButton, "RESUME", resumeHover);
        drawButton(g2, menuButton, "MAIN MENU", menuHover);
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        g2.setColor(hover ? new Color(100, 255, 100) : Color.WHITE);
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int ty = rect.y + (rect.height + fm.getAscent()) / 2 - 5;

        g2.drawString(text, tx, ty);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    public Rectangle getResumeButton() { return resumeButton; }
    public Rectangle getMenuButton() { return menuButton; }
    public void setResumeHover(boolean h) { this.resumeHover = h; }
    public void setMenuHover(boolean h) { this.menuHover = h; }
}