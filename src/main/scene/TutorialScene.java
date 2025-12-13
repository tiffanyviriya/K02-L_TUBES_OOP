package main.scene;

import main.manager.FontManager;
import main.util.GamePanel;
import main.handler.TutorialSceneMouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/* Menangani tampilan petunjuk permainan dan interaksi pengguna di layar tutorial */
public class TutorialScene implements Scene {

    public GamePanel gp;
    public TutorialSceneMouseHandler mouseHandler;

    private final Rectangle backButton;

    public boolean backHover = false;

    private BufferedImage tutorialImage;
    private BufferedImage buttonImage;
    private BufferedImage instructionsImage;
    private Font pixelFont;

    private static final String TUTORIAL_IMAGE_PATH = "/ui/nimonscooked.png";
    private static final String BUTTON_IMAGE_PATH = "/ui/buttonUI.png";
    private static final String INSTRUCTIONS_IMAGE_PATH = "/ui/howtoplay.jpeg";

    /* Menginisialisasi scene, posisi tombol, dan memuat resource */
    public TutorialScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new TutorialSceneMouseHandler(this);

        int buttonWidth = 300;
        int buttonHeight = 60;

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);
        int backButtonY = gp.screenHeight - buttonHeight - 40;

        backButton = new Rectangle(buttonX, backButtonY, buttonWidth, buttonHeight);

        loadResources();
    }

    /* Memuat gambar tutorial, tombol, instruksi, dan font dari resource */
    private void loadResources() {
        try {
            InputStream tutorialStream = getClass().getResourceAsStream(TUTORIAL_IMAGE_PATH);
            if (tutorialStream != null) {
                tutorialImage = ImageIO.read(tutorialStream);
            }

            InputStream btnStream = getClass().getResourceAsStream(BUTTON_IMAGE_PATH);
            if (btnStream != null) {
                buttonImage = ImageIO.read(btnStream);
            }

            InputStream instStream = getClass().getResourceAsStream(INSTRUCTIONS_IMAGE_PATH);
            if (instStream != null) {
                instructionsImage = ImageIO.read(instStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        pixelFont = FontManager.getPixelFont(36f);
    }

    /* Memperbarui logika scene setiap frame */
    @Override
    public void update() {
    }

    /* Menggambar latar belakang, instruksi, dan tombol kembali ke layar */
    @Override
    public void draw(Graphics2D g2) {
        if (tutorialImage != null) {
            g2.drawImage(tutorialImage, 0, 0, gp.screenWidth, gp.screenHeight, null);

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(FontManager.getPixelFont(50f));
            g2.setColor(Color.RED);
            g2.drawString("TUTORIAL IMAGE MISSING", 50, gp.screenHeight / 2);
        }

        if (instructionsImage != null) {
            int imgWidth = (int) (gp.screenWidth * 0.85);
            int imgHeight = (int) (gp.screenHeight * 0.70);

            int imgX = (gp.screenWidth - imgWidth) / 2;
            int imgY = (gp.screenHeight - imgHeight) / 2;

            g2.drawImage(instructionsImage, imgX, imgY, imgWidth, imgHeight, null);
        }

        drawButton(g2, backButton, "KEMBALI KE MENU", backHover);
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

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
        }

        if (hover) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        g2.setFont(pixelFont);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int totalTextHeight = fm.getHeight();

        int tx = rect.x + (rect.width - textWidth) / 2;

        int ty = rect.y + (rect.height - totalTextHeight) / 2 + fm.getAscent();

        g2.setColor(Color.BLACK);
        g2.drawString(text, tx + 3, ty + 3);

        if (hover) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(text, tx, ty);
    }

    /* Mengambil objek rectangle tombol kembali */
    public Rectangle getBackButton() { return backButton; }

    /* Mengatur status hover untuk tombol kembali */
    public void setBackHover(boolean hover) { this.backHover = hover; }
}