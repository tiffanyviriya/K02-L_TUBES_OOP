package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainMenuScene implements Scene {

    GamePanel gp;

    // Tombol-tombol menu
    private Rectangle playButton;
    private Rectangle exitButton;

    // Status hover
    private boolean playHover = false;
    private boolean exitHover = false;

    // Gambar-gambar UI
    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;

    private MainMenuMouseHandler mouseHandler;

    public MainMenuScene(GamePanel gp) {
        this.gp = gp;

        // -----------------------------------------------------------
        // Posisi tombol Dinamis
        // -----------------------------------------------------------
        int buttonWidth = 165;
        int buttonHeight = 64;

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);
        int playButtonY = (gp.screenHeight / 2) - buttonHeight;
        int exitButtonY = playButtonY + buttonHeight + 40;

        playButton = new Rectangle(buttonX, playButtonY, buttonWidth, buttonHeight);
        exitButton = new Rectangle(buttonX, exitButtonY, buttonWidth, buttonHeight);

        try {
            // Memuat gambar background dan tombol
            // Pastikan file "nimonscooked.png" dan "buttonUI.png" ada di folder /res/ui/
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/nimonscooked.png"));
            buttonImage = ImageIO.read(getClass().getResourceAsStream("/ui/buttonUI.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Warning: UI images failed to load.");
        }

        mouseHandler = new MainMenuMouseHandler(this);
    }

    @Override
    public void update() {
        // Logika update animasi menu
    }

    @Override
    public void draw(Graphics2D g2) {
        // 1. Gambar Background Utama
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        // 2. Gambar Tombol
        drawButton(g2, playButton, "PLAY", playHover);
        drawButton(g2, exitButton, "EXIT", exitHover);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    // --- Helper Methods ---

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {

        // A. Gambar Background Tombol
        if (buttonImage != null) {
            g2.drawImage(buttonImage, rect.x, rect.y, rect.width, rect.height, null);
        } else {
            g2.setColor(Color.GRAY);
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // B. EFEK GELAP SAAT HOVER (Overlay)
        if (hover) {
            // Membuat warna hitam dengan transparansi (Alpha)
            // Format: Red, Green, Blue, Alpha (0-255).
            // 0 = transparan penuh, 255 = solid.
            // 80 memberikan efek gelap sekitar 30%.
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // C. Setup Font Pixel Style
        g2.setFont(new Font("Monospaced", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        // Hitung posisi tengah
        int tx = rect.x + (rect.width - textWidth) / 2 - 4;
        int ty = rect.y + (rect.height + textHeight) / 2 -8;

        // D. Gambar Teks
        if (hover) {
            // Saat hover: Teks menjadi kuning cerah, shadow tetap hitam
            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2); // Shadow
            g2.setColor(Color.YELLOW);
            g2.drawString(text, tx, ty); // Teks Utama
        } else {
            // Saat normal: Teks putih, shadow hitam
            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2); // Shadow
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty); // Teks Utama
        }
    }

    public Rectangle getPlayButton() { return playButton; }
    public Rectangle getExitButton() { return exitButton; }
    public void setPlayHover(boolean playHover) { this.playHover = playHover; }
    public void setExitHover(boolean exitHover) { this.exitHover = exitHover; }
}