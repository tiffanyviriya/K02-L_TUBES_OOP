package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MainMenuScene implements Scene {

    GamePanel gp;
    public MainMenuMouseHandler mouseHandler; // Public agar bisa diakses jika perlu

    // UI Elements
    private Rectangle playButton;
    private Rectangle exitButton;

    // State Visual
    private boolean playHover = false;
    private boolean exitHover = false;

    // Resources
    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;
    private Font pixelFont;

    public MainMenuScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new MainMenuMouseHandler(this);

        // -----------------------------------------------------------
        // 1. Setup Posisi Tombol (Dinamis Tengah Layar)
        // -----------------------------------------------------------
        int buttonWidth = 200;
        int buttonHeight = 60;
        int spacing = 40;

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);
        int playButtonY = (gp.screenHeight / 2) - buttonHeight; // Sedikit ke atas
        int exitButtonY = playButtonY + buttonHeight + spacing;

        playButton = new Rectangle(buttonX, playButtonY, buttonWidth, buttonHeight);
        exitButton = new Rectangle(buttonX, exitButtonY, buttonWidth, buttonHeight);

        // -----------------------------------------------------------
        // 2. Load Resources (Safe Loading)
        // -----------------------------------------------------------
        loadResources();
    }

    private void loadResources() {
        try {
            // Load Gambar
            // Pastikan path: /res/ui/ atau /ui/ tergantung struktur folder src anda
            InputStream bgStream = getClass().getResourceAsStream("/ui/nimonscooked.png");
            InputStream btnStream = getClass().getResourceAsStream("/ui/buttonUI.png");

            if (bgStream != null) backgroundImage = ImageIO.read(bgStream);
            if (btnStream != null) buttonImage = ImageIO.read(btnStream);

            // Load Font
            InputStream fontStream = getClass().getResourceAsStream("/font/ByteBounce.ttf");
            if (fontStream != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(36f);
            } else {
                System.out.println("Warning: Custom font not found. Using default.");
                pixelFont = new Font("Monospaced", Font.BOLD, 32);
            }

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.out.println("Error loading resources. Using fallbacks.");
            pixelFont = new Font("Monospaced", Font.BOLD, 32);
        }
    }

    @Override
    public void update() {
        // Logika animasi menu bisa ditaruh di sini
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

    // --- Input Delegation (Penting!) ---
    // Scene menerima input dari GamePanel, lalu memberikannya ke Handler

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
        // Gambar Tombol (Image / Kotak Polos)
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
        g2.setFont(pixelFont);
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

    // --- Getters & Setters ---
    public Rectangle getPlayButton() { return playButton; }
    public Rectangle getExitButton() { return exitButton; }
    public void setPlayHover(boolean hover) { this.playHover = hover; }
    public void setExitHover(boolean hover) { this.exitHover = hover; }
}