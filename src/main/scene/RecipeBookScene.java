package main.scene;

// [PERBAIKAN 1] Ubah import ke main.manager
import main.manager.FontManager;
import main.handler.RecipeBookMouseHandler;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class RecipeBookScene implements Scene {

    private GamePanel gp;
    private RecipeBookMouseHandler mouseHandler;

    private Rectangle closeButton;
    private boolean closeHover = false;

    // Gambar untuk Recipe Book / How To Play
    private BufferedImage recipeImage;

    public RecipeBookScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new RecipeBookMouseHandler(this, gp);

        loadResources();

        // Tombol Close di pojok kanan atas
        // Sesuaikan posisi X dan Y jika menutupi gambar penting
        int btnSize = 50;
        this.closeButton = new Rectangle(gp.screenWidth - btnSize - 20, 20, btnSize, btnSize);

        // Daftarkan listener
        gp.addMouseListener(mouseHandler);
        gp.addMouseMotionListener(mouseHandler);
    }

    private void loadResources() {
        try {
            // [PERBAIKAN 2] Load gambar howtoplay.jpeg
            // Pastikan file ada di folder res/ui/howtoplay.jpeg
            InputStream is = getClass().getResourceAsStream("/ui/recipebook.png");
            if (is != null) {
                recipeImage = ImageIO.read(is);
            } else {
                System.out.println("Gagal memuat /ui/recipebook.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        // Tidak ada logika update khusus
    }

    @Override
    public void draw(Graphics2D g2) {
        // 1. Background Gelap Transparan (Dim)
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Gambar How To Play / Recipe Book
        if (recipeImage != null) {
            // Gambar full screen atau sesuaikan ukuran
            // Opsi A: Full Screen Stretch
            g2.drawImage(recipeImage, 0, 0, gp.screenWidth, gp.screenHeight, null);

            // Opsi B: Centered (Jika gambar lebih kecil/aspek rasio beda)
            /*
            int imgW = 800; // Sesuaikan ukuran yang diinginkan
            int imgH = 600;
            int x = (gp.screenWidth - imgW) / 2;
            int y = (gp.screenHeight - imgH) / 2;
            g2.drawImage(recipeImage, x, y, imgW, imgH, null);
            */
        } else {
            // Fallback jika gambar gagal load
            g2.setColor(Color.WHITE);
            g2.drawString("Image Not Found: /ui/howtoplay.jpeg", 100, 100);
        }

        // 3. Tombol Close (X)
        drawCloseButton(g2);
    }

    private void drawCloseButton(Graphics2D g2) {
        g2.setColor(closeHover ? Color.RED : new Color(200, 50, 50));
        g2.fillRoundRect(closeButton.x, closeButton.y, closeButton.width, closeButton.height, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(closeButton.x, closeButton.y, closeButton.width, closeButton.height, 15, 15);

        g2.setFont(FontManager.getPixelFont(30f)); // Menggunakan FontManager yang benar

        FontMetrics fm = g2.getFontMetrics();
        int textX = closeButton.x + (closeButton.width - fm.stringWidth("X")) / 2;
        int textY = closeButton.y + (closeButton.height - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString("X", textX, textY - 3);
    }

    // Interface methods
    @Override public void mousePressed(java.awt.event.MouseEvent e) {
        // Delegasikan ke handler jika perlu, atau handler yang panggil ini
        // Karena di GamePanel kita panggil handler langsung, ini bisa kosong
        // atau pindahkan logika handler ke sini.
        // Untuk konsistensi dengan handler yang sudah dibuat:
        // mouseHandler.mousePressed(e);
    }

    @Override public void mouseMoved(java.awt.event.MouseEvent e) {
        // mouseHandler.mouseMoved(e);
    }

    public Rectangle getCloseButton() { return closeButton; }
    public void setCloseHover(boolean h) { this.closeHover = h; }
}