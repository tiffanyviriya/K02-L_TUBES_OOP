package main.scene;

import main.manager.FontManager;
import main.util.GamePanel;
import main.handler.MainMenuSceneMouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/* Menangani tampilan, logika, dan interaksi pada menu utama permainan */
public class MainMenuScene implements Scene {

    public GamePanel gp;
    public MainMenuSceneMouseHandler mouseHandler;

    private Rectangle playButton;
    private Rectangle tutorialButton;
    private Rectangle exitButton;

    public boolean playHover = false;
    public boolean tutorialHover = false;
    public boolean exitHover = false;

    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;
    private Font pixelFont;

    /* Menginisialisasi scene, posisi tombol, dan memuat resource yang diperlukan */
    public MainMenuScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new MainMenuSceneMouseHandler(this);

        int buttonWidth = 200;
        int buttonHeight = 60;
        int spacing = 40;

        int totalHeight = (buttonHeight * 3) + (spacing * 2);

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);

        int startY = (gp.screenHeight / 2) - (totalHeight / 2);

        playButton = new Rectangle(buttonX, startY, buttonWidth, buttonHeight);
        tutorialButton = new Rectangle(buttonX, startY + buttonHeight + spacing, buttonWidth, buttonHeight);
        exitButton = new Rectangle(buttonX, startY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight);

        loadResources();
    }

    /* Memuat gambar background, tombol, dan font dari resource */
    private void loadResources() {
        try {
            InputStream bgStream = getClass().getResourceAsStream("/ui/nimonscooked.png");
            InputStream btnStream = getClass().getResourceAsStream("/ui/buttonUI.png");

            if (bgStream != null) backgroundImage = ImageIO.read(bgStream);
            if (btnStream != null) buttonImage = ImageIO.read(btnStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        pixelFont = FontManager.getPixelFont(36f);
    }

    /* Memperbarui logika scene setiap frame (saat ini tidak ada logika animasi khusus) */
    @Override
    public void update() {
    }

    /* Menggambar elemen visual seperti background dan tombol ke layar */
    @Override
    public void draw(Graphics2D g2) {
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        drawButton(g2, playButton, "PLAY", playHover);
        drawButton(g2, tutorialButton, "TUTORIAL", tutorialHover);
        drawButton(g2, exitButton, "EXIT", exitHover);
    }

    /* Meneruskan event klik mouse ke handler */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    /* Meneruskan event pergerakan mouse ke handler */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    /* Menggambar tombol dengan background, teks, dan efek hover */
    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        if (buttonImage != null) {
            g2.drawImage(buttonImage, rect.x, rect.y, rect.width, rect.height, null);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        if (hover) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        g2.setFont(pixelFont);

        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(Color.BLACK);
        g2.drawString(text, textX + 3, textY + 3);

        if (hover) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(text, textX, textY);
    }

    /* Mengambil objek rectangle tombol play */
    public Rectangle getPlayButton() { return playButton; }

    /* Mengambil objek rectangle tombol tutorial */
    public Rectangle getTutorialButton() { return tutorialButton; }

    /* Mengambil objek rectangle tombol exit */
    public Rectangle getExitButton() { return exitButton; }

    /* Mengatur status hover untuk tombol play */
    public void setPlayHover(boolean hover) { this.playHover = hover; }

    /* Mengatur status hover untuk tombol tutorial */
    public void setTutorialHover(boolean hover) { this.tutorialHover = hover; }

    /* Mengatur status hover untuk tombol exit */
    public void setExitHover(boolean hover) { this.exitHover = hover; }
}