package main.manager;

import main.util.GamePanel;
import main.util.GameState;
import main.handler.DifficultySceneMouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DifficultyScene {
    protected GamePanel gp;
    private DifficultySceneMouseHandler mouseHandler;

    private BufferedImage backgroundImage;
    private BufferedImage mapPreviewImage; // Gambar tambahan

    public static final String LEVEL_EASY = "EASY";
    public static final String LEVEL_MEDIUM = "MEDIUM";
    public static final String LEVEL_HARD = "HARD";

    // Posisi tombol
    private final Rectangle easyButton;
    private final Rectangle mediumButton;
    private final Rectangle hardButton;

    // Status hover
    private boolean easyHover = false;
    private boolean mediumHover = false;
    private boolean hardHover = false;

    private final String titleText = "PILIH TINGKAT KESULITAN";
    private final int titleYPosition = 48;

    private final int buttonWidth = 200;
    private final int buttonHeight = 60;
    private final int buttonSpacing = 115;

    // Posisi X untuk elemen UI (Tombol) agar rata kiri
    private final int uiLeftAlignX = 120;

    public DifficultyScene(GamePanel gp) {
        this.gp = gp;

        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/difficultyscene.png"));
            mapPreviewImage = ImageIO.read(getClass().getResourceAsStream("/ui/mappreviewplaceholder.png"));
        } catch (IOException e) {
            System.err.println("Gagal memuat gambar untuk Difficulty Scene.");
        } catch (IllegalArgumentException e) {
            System.err.println("Gambar tidak ditemukan (cek path folder /ui/).");
        }

        // Menghitung posisi Y agar tombol terpusat secara vertikal
        int totalHeight = (buttonHeight * 3) + (buttonSpacing * 2);
        int startY = gp.screenHeight / 2 - totalHeight / 2 + (titleYPosition / 2);

        // Posisi X tombol digeser ke kiri (uiLeftAlignX)
        this.easyButton = new Rectangle(uiLeftAlignX, startY, buttonWidth, buttonHeight);
        this.mediumButton = new Rectangle(uiLeftAlignX, startY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
        this.hardButton = new Rectangle(uiLeftAlignX, startY + (buttonHeight + buttonSpacing) * 2, buttonWidth, buttonHeight);

        mouseHandler = new DifficultySceneMouseHandler(this, gp);
        gp.addMouseListener(mouseHandler);
        gp.addMouseMotionListener(mouseHandler);
    }

    public void startGame(String difficulty) {
        gp.currentDifficulty = difficulty;

        int timeLimitSeconds = 0;
        switch (difficulty) {
            case LEVEL_EASY:
                timeLimitSeconds = 120;
                break;
            case LEVEL_MEDIUM:
                timeLimitSeconds = 90;
                break;
            case LEVEL_HARD:
                timeLimitSeconds = 60;
                break;
        }

        System.out.println("Start Level: " + difficulty + " | Target: " + gp.scoreM.getTargetScore(difficulty));

        gp.uiTimer.resetTime(timeLimitSeconds);
        gp.changeGameState(GameState.PLAYING);
        gp.repaint();
    }

    // Getters Setters
    public Rectangle getEasyButton() { return easyButton; }
    public Rectangle getMediumButton() { return mediumButton; }
    public Rectangle getHardButton() { return hardButton; }
    public boolean isEasyHover() { return easyHover; }
    public boolean isMediumHover() { return mediumHover; }
    public boolean isHardHover() { return hardHover; }
    public void setEasyHover(boolean h) { this.easyHover = h; }
    public void setMediumHover(boolean h) { this.mediumHover = h; }
    public void setHardHover(boolean h) { this.hardHover = h; }

    public void draw(Graphics2D g2) {
        // 1. Gambar Background Utama
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        // 2. Gambar Dekorasi Tambahan (422x302) di sebelah kanan
        if (mapPreviewImage != null) {
            int imgW = 422;
            int imgH = 302;
            // Posisi X di sebelah kanan layar (margin dari kanan sekitar 100px agar seimbang dengan tombol kiri)
            int imgX = gp.screenWidth - imgW - 50;
            int imgY = (gp.screenHeight - imgH) / 2 + 20; // Sedikit offset Y agar visual seimbang
            g2.drawImage(mapPreviewImage, imgX, imgY, imgW, imgH, null);
        }

        // 3. Judul (Kembali Ditengah Layar)
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));

        // Menggunakan tengah layar (gp.screenWidth / 2) sebagai titik tengah
        int screenCenterX = gp.screenWidth / 2;

        // Shadow Judul
        g2.setColor(Color.BLACK);
        int shadowX = getXforCenteredText(g2, titleText, screenCenterX) + 2;
        int shadowY = titleYPosition + 35 + 2;
        g2.drawString(titleText, shadowX, shadowY);

        // Teks Judul Utama
        g2.setColor(Color.YELLOW);
        int textX = getXforCenteredText(g2, titleText, screenCenterX);
        int textY = titleYPosition + 35;
        g2.drawString(titleText, textX, textY);

        // 4. Tombol Difficulty
        drawLevelButton(g2, easyButton, LEVEL_EASY, easyHover);
        drawLevelButton(g2, mediumButton, LEVEL_MEDIUM, mediumHover);
        drawLevelButton(g2, hardButton, LEVEL_HARD, hardHover);
    }

    private void drawLevelButton(Graphics2D g2, Rectangle rect, String levelName, boolean hover) {
        boolean isCleared = gp.scoreM.isLevelCleared(levelName);
        int targetScore = gp.scoreM.getTargetScore(levelName);

        if (isCleared) {
            g2.setColor(hover ? new Color(100, 255, 100) : new Color(50, 200, 50));
        } else {
            g2.setColor(hover ? new Color(255, 180, 50, 200) : new Color(255, 210, 100));
        }

        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setFont(new Font("Poppins", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(levelName)) / 2;
        int ty = rect.y + (rect.height + fm.getAscent()) / 2 - 10;

        g2.setColor(Color.BLACK);
        g2.drawString(levelName, tx, ty);

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String subText = isCleared ? "CLEARED (Target: " + targetScore + ")" : "Target Score: " + targetScore;

        int subTx = rect.x + (rect.width - g2.getFontMetrics().stringWidth(subText)) / 2;
        int subTy = ty + 20;
        g2.drawString(subText, subTx, subTy);
    }

    /**
     * Helper untuk mendapatkan X agar teks berada di tengah relatif terhadap titik tertentu.
     */
    public int getXforCenteredText(Graphics2D g2, String text, int centerXPoint) {
        FontMetrics fm = g2.getFontMetrics();
        int length = fm.stringWidth(text);
        return centerXPoint - (length / 2);
    }
}