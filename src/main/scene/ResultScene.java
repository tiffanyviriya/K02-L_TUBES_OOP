package main.scene;

import main.util.GamePanel;
import main.handler.ResultSceneMouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ResultScene implements Scene{

    protected GamePanel gp;
    private ResultSceneMouseHandler mouseHandler;
    private BufferedImage backgroundImage;

    private final Rectangle menuButton;
    private boolean menuHover = false;
    private final int cardWidth;
    private final int cardHeight;

    /* Konstruktor untuk inisialisasi scene hasil, memuat gambar latar, dan menyiapkan handler input */
    public ResultScene(GamePanel gp) {
        this.gp = gp;
        this.cardWidth = gp.screenWidth;
        this.cardHeight = gp.screenHeight;

        this.menuButton = new Rectangle(cardWidth/2 - 100, cardHeight - 100, 200, 50);

        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/resultscreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mouseHandler = new ResultSceneMouseHandler(this, gp);
        gp.addMouseListener(mouseHandler);
        gp.addMouseMotionListener(mouseHandler);
    }

    /* Memproses hasil permainan, mengevaluasi skor terhadap target, dan menyimpan status kelulusan level */
    public void processResult() {
        String difficulty = gp.currentDifficulty;
        int score = gp.orderM.score;

        int target = gp.scoreM.getTargetScore(difficulty);

        boolean isPass = score >= target;

        if (isPass) {
            gp.scoreM.setLevelCleared(difficulty);
        }
    }

    /* Mendapatkan referensi area tombol menu utama */
    public Rectangle getMenuButton() { return menuButton; }

    /* Mengatur status hover pada tombol menu utama */
    public void setMenuHover(boolean hover) { this.menuHover = hover; }

    /* Memperbarui logika scene setiap frame (tidak digunakan di scene ini) */
    @Override
    public void update() {

    }

    /* Menggambar tampilan hasil akhir termasuk latar belakang, skor, status kelulusan, dan tombol menu */
    public void draw(Graphics2D g2) {
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, cardWidth, cardHeight, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, cardWidth, cardHeight);
        }

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int score = gp.orderM.score;
        String difficulty = (gp.currentDifficulty != null) ? gp.currentDifficulty : "EASY";

        int targetScore = gp.scoreM.getTargetScore(difficulty);
        boolean isPass = score >= targetScore;

        g2.setColor(Color.WHITE);
        int centerX = gp.screenWidth / 2;
        int startY = 150;

        g2.setFont(new Font("Arial", Font.BOLD, 60));
        String titleText = isPass ? "STAGE CLEARED!" : "STAGE FAILED";
        Color titleColor = isPass ? Color.GREEN : Color.RED;
        g2.setColor(titleColor);
        drawCenteredText(g2, titleText, startY, centerX);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));

        startY += 80;
        drawCenteredText(g2, "Level: " + difficulty, startY, centerX);

        startY += 50;
        drawCenteredText(g2, "Your Score: " + score, startY, centerX);

        startY += 50;
        g2.setColor(Color.YELLOW);
        drawCenteredText(g2, "Target to Pass: " + targetScore, startY, centerX);

        startY += 80;
        g2.setFont(new Font("Arial", Font.ITALIC, 25));
        g2.setColor(Color.LIGHT_GRAY);
        String msg = isPass ? "Kerja Bagus! Menu berikutnya menanti." : "Skor belum mencukupi target.";
        drawCenteredText(g2, msg, startY, centerX);

        drawButton(g2, menuButton, "MAIN MENU", menuHover);
    }

    /* Menangani event penekanan tombol mouse */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    /* Menangani event pergerakan mouse */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    /* Menggambar teks dengan posisi rata tengah secara horizontal */
    private void drawCenteredText(Graphics2D g2, String text, int y, int centerX) {
        FontMetrics fm = g2.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        g2.drawString(text, x, y);
    }

    /* Menggambar tombol dengan teks dan efek visual hover */
    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        g2.setColor(hover ? new Color(100, 255, 100) : new Color(200, 200, 200));
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        int x = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int y = rect.y + (rect.height + fm.getAscent()) / 2 - 5;
        g2.drawString(text, x, y);
    }
}