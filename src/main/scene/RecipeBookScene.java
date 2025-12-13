package main.scene;

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

    private BufferedImage recipeImage;

    /* Konstruktor untuk inisialisasi scene buku resep, memuat resource, dan menyiapkan tombol tutup */
    public RecipeBookScene(GamePanel gp) {
        this.gp = gp;
        this.mouseHandler = new RecipeBookMouseHandler(this, gp);

        loadResources();

        int btnSize = 50;
        this.closeButton = new Rectangle(gp.screenWidth - btnSize - 20, 20, btnSize, btnSize);

        gp.addMouseListener(mouseHandler);
        gp.addMouseMotionListener(mouseHandler);
    }

    /* Memuat gambar visual untuk tampilan buku resep */
    private void loadResources() {
        try {
            InputStream is = getClass().getResourceAsStream("/ui/recipebook.png");
            if (is != null) {
                recipeImage = ImageIO.read(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Memperbarui logika scene setiap frame */
    @Override
    public void update() {
    }

    /* Menggambar latar belakang redup, gambar buku resep, dan tombol tutup */
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (recipeImage != null) {
            g2.drawImage(recipeImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.drawString("Image Not Found", 100, 100);
        }

        drawCloseButton(g2);
    }

    /* Menggambar tombol tutup dengan efek visual hover */
    private void drawCloseButton(Graphics2D g2) {
        g2.setColor(closeHover ? Color.RED : new Color(200, 50, 50));
        g2.fillRoundRect(closeButton.x, closeButton.y, closeButton.width, closeButton.height, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(closeButton.x, closeButton.y, closeButton.width, closeButton.height, 15, 15);

        g2.setFont(FontManager.getPixelFont(30f));

        FontMetrics fm = g2.getFontMetrics();
        int textX = closeButton.x + (closeButton.width - fm.stringWidth("X")) / 2;
        int textY = closeButton.y + (closeButton.height - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString("X", textX, textY - 3);
    }

    /* Menangani event penekanan tombol mouse */
    @Override public void mousePressed(java.awt.event.MouseEvent e) {
    }

    /* Menangani event pergerakan mouse */
    @Override public void mouseMoved(java.awt.event.MouseEvent e) {
    }

    /* Mendapatkan referensi area tombol tutup */
    public Rectangle getCloseButton() { return closeButton; }

    /* Mengatur status hover pada tombol tutup */
    public void setCloseHover(boolean h) { this.closeHover = h; }
}