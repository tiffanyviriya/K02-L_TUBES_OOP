package main.scene;

import main.util.FontManager;
import main.util.GamePanel;
import main.handler.MainMenuSceneMouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MainMenuScene implements Scene {

    public GamePanel gp;
    public MainMenuSceneMouseHandler mouseHandler;

    // UI Elements
    private Rectangle playButton;
    private Rectangle exitButton;

    // State Visual
    private boolean playHover = false;
    private boolean exitHover = false;

    // Resources
    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;
    private Font pixelFont; // Masih disimpan di sini untuk referensi lokal scene ini

    public MainMenuScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new MainMenuSceneMouseHandler(this);

        // 1. Setup Posisi Tombol
        int buttonWidth = 200;
        int buttonHeight = 60;
        int spacing = 40;

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);
        int playButtonY = (gp.screenHeight / 2) - buttonHeight;
        int exitButtonY = playButtonY + buttonHeight + spacing;

        playButton = new Rectangle(buttonX, playButtonY, buttonWidth, buttonHeight);
        exitButton = new Rectangle(buttonX, exitButtonY, buttonWidth, buttonHeight);

        // 2. Load Resources
        loadResources();
    }

    private void loadResources() {
        try {
            // Load Gambar (Tetap di sini karena spesifik untuk scene ini)
            InputStream bgStream = getClass().getResourceAsStream("/ui/nimonscooked.png");
            InputStream btnStream = getClass().getResourceAsStream("/ui/buttonUI.png");

            if (bgStream != null) backgroundImage = ImageIO.read(bgStream);
            if (btnStream != null) buttonImage = ImageIO.read(btnStream);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading image resources.");
        }

        // --- LOAD FONT VIA FONT MANAGER ---
        // Kode menjadi jauh lebih bersih.
        // Anda bisa memanggil ini di scene lain dengan ukuran berbeda jika mau.
        pixelFont = FontManager.getPixelFont(36f);
    }

    @Override
    public void update() {
        // Logika animasi menu
    }

    @Override
    public void draw(Graphics2D g2) {
        // 1. Gambar Background
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

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        // Gambar Tombol
        if (buttonImage != null) {
            g2.drawImage(buttonImage, rect.x, rect.y, rect.width, rect.height, null);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // Overlay Gelap saat Hover
        if (hover) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // Styling Font
        g2.setFont(pixelFont); // Menggunakan font yang didapat dari FontManager

        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();

        // Shadow Effect
        g2.setColor(Color.BLACK);
        g2.drawString(text, textX + 3, textY + 3);

        // Main Text Color
        if (hover) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(text, textX, textY);
    }

    public Rectangle getPlayButton() { return playButton; }
    public Rectangle getExitButton() { return exitButton; }
    public void setPlayHover(boolean hover) { this.playHover = hover; }
    public void setExitHover(boolean hover) { this.exitHover = hover; }
}