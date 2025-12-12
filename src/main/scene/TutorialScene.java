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

/**
 * Scene untuk menampilkan petunjuk permainan.
 */
public class TutorialScene implements Scene {

    public GamePanel gp;
    public TutorialSceneMouseHandler mouseHandler;

    // UI Elements
    private final Rectangle backButton;

    // State Visual
    public boolean backHover = false;

    // Resources
    private BufferedImage tutorialImage;
    private BufferedImage buttonImage;
    private BufferedImage instructionsImage; // Deklarasi untuk gambar instruksi
    private Font pixelFont;

    // Placeholder untuk gambar Tutorial. Asumsi menggunakan gambar yang sama sebagai placeholder.
    private static final String TUTORIAL_IMAGE_PATH = "/ui/nimonscooked.png";
    private static final String BUTTON_IMAGE_PATH = "/ui/buttonUI.png";
    private static final String INSTRUCTIONS_IMAGE_PATH = "/ui/howtoplay.jpeg"; // Path untuk gambar instruksi

    public TutorialScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new TutorialSceneMouseHandler(this);

        // 1. Setup Posisi Tombol
        int buttonWidth = 300;
        int buttonHeight = 60;

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);
        // Letakkan tombol di dekat bawah layar
        int backButtonY = gp.screenHeight - buttonHeight - 40;

        backButton = new Rectangle(buttonX, backButtonY, buttonWidth, buttonHeight);

        // 2. Load Resources
        loadResources();
    }

    private void loadResources() {
        try {
            // Load Gambar Tutorial (Placeholder)
            InputStream tutorialStream = getClass().getResourceAsStream(TUTORIAL_IMAGE_PATH);
            if (tutorialStream != null) {
                tutorialImage = ImageIO.read(tutorialStream);
            } else {
                System.out.println("Tutorial image not found at: " + TUTORIAL_IMAGE_PATH);
            }

            // Load Gambar Tombol
            InputStream btnStream = getClass().getResourceAsStream(BUTTON_IMAGE_PATH);
            if (btnStream != null) {
                buttonImage = ImageIO.read(btnStream);
            } else {
                System.out.println("Button image not found at: " + BUTTON_IMAGE_PATH);
            }

            // Muat Gambar Instruksi
            InputStream instStream = getClass().getResourceAsStream(INSTRUCTIONS_IMAGE_PATH);
            if (instStream != null) {
                instructionsImage = ImageIO.read(instStream);
            } else {
                System.out.println("Instructions image not found at: " + INSTRUCTIONS_IMAGE_PATH);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading tutorial or button image.");
        }

        // Ambil Font dari FontManager
        // SINKRONISASI FONT: Menggunakan ukuran 36f agar sama dengan MainMenuScene
        pixelFont = FontManager.getPixelFont(36f);
    }

    @Override
    public void update() {
        // Tidak ada logika update yang kompleks di scene statis ini
    }

    @Override
    public void draw(Graphics2D g2) {
        // 1. Gambar Gambar Tutorial (Penuh Layar)
        if (tutorialImage != null) {
            // Gambar semi-transparan (untuk latar belakang, jika gambar tidak penuh)
            g2.drawImage(tutorialImage, 0, 0, gp.screenWidth, gp.screenHeight, null);

            // Tambahkan overlay semi-transparan hitam agar teks lebih jelas
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // Teks Header/Instruksi Dihapus

        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(FontManager.getPixelFont(50f));
            g2.setColor(Color.RED);
            g2.drawString("TUTORIAL IMAGE MISSING", 50, gp.screenHeight / 2);
        }

        // 2. Gambar Gambar Instruksi di Bagian Kiri
        if (instructionsImage != null) {
            // Tentukan posisi dan ukuran gambar instruksi (contoh: 70% tinggi layar, diletakkan 40px dari kiri)
            int imgWidth = (int) (gp.screenWidth * 0.85); // Lebar 45% layar
            int imgHeight = (int) (gp.screenHeight * 0.70); // Tinggi 70% layar

            int imgX = (gp.screenWidth - imgWidth) / 2;
            // Posisikan secara vertikal di tengah
            int imgY = (gp.screenHeight - imgHeight) / 2;

            g2.drawImage(instructionsImage, imgX, imgY, imgWidth, imgHeight, null);
        }


        // 3. Gambar Tombol Kembali
        drawButton(g2, backButton, "KEMBALI KE MENU", backHover);
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
        // LOGIKA TOMBOL SAMA DENGAN MAINMENUSCENE

        // Gambar Tombol (Menggunakan buttonImage)
        if (buttonImage != null) {
            g2.drawImage(buttonImage, rect.x, rect.y, rect.width, rect.height, null);
        } else {
            // Fallback jika gambar tidak ada (menggunakan warna solid)
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);

            // Tambahkan Border (untuk Fallback)
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
        }

        // Overlay Gelap saat Hover (Sama seperti MainMenuScene jika menggunakan gambar)
        if (hover) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // Teks
        g2.setFont(pixelFont);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int totalTextHeight = fm.getHeight();

        // Posisi X: Di tengah tombol
        int tx = rect.x + (rect.width - textWidth) / 2;

        // Posisi Y (Baseline): Formula Centering Vertikal Standar
        int ty = rect.y + (rect.height - totalTextHeight) / 2 + fm.getAscent();

        // Shadow Effect
        g2.setColor(Color.BLACK);
        g2.drawString(text, tx + 3, ty + 3);

        // Main Text Color (Sama seperti MainMenuScene)
        if (hover) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(text, tx, ty);
    }

    public Rectangle getBackButton() { return backButton; }
    public void setBackHover(boolean hover) { this.backHover = hover; }
}