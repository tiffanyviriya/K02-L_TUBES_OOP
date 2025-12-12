package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Kelas yang menangani tampilan dan logika untuk pemilihan tingkat kesulitan.
 * Tampilan terdiri dari 3 tombol level (EASY, MEDIUM, HARD) dan 1 tampilan Title/Judul.
 */
public class DifficultyScene {
    GamePanel gp;

    // Gambar latar belakang
    private BufferedImage backgroundImage;

    // String untuk identifikasi level
    private static final String LEVEL_EASY = "EASY";
    private static final String LEVEL_MEDIUM = "MEDIUM";
    private static final String LEVEL_HARD = "HARD";

    // Posisi tombol
    private final Rectangle easyButton;
    private final Rectangle mediumButton;
    private final Rectangle hardButton;

    // Status hover
    private boolean easyHover = false;
    private boolean mediumHover = false;
    private boolean hardHover = false;

    // Teks Judul
    private final String titleText = "PILIH TINGKAT KESULITAN";
    private final int titleYPosition = 48; // Jarak 48 pixel dari border atas

    private final int buttonWidth = 200;
    private final int buttonHeight = 60;
    private final int buttonSpacing = 115;

    public DifficultyScene(GamePanel gp) {
        this.gp = gp;

        // Muat Gambar Latar Belakang (asumsi ada di /ui/nimonscooked.png)
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/difficultyscene.png"));
        } catch (IOException e) {
            // Jatuhkan ke konsol jika gagal memuat, tapi biarkan game berjalan dengan background hitam
            System.err.println("Gagal memuat gambar latar belakang untuk Difficulty Scene.");
            e.printStackTrace();
        }

        // Menghitung posisi tombol agar terpusat di tengah layar
        int totalHeight = (buttonHeight * 3) + (buttonSpacing * 2);
        // Pusatkan vertikal di bagian bawah judul
        int startY = gp.screenHeight / 2 - totalHeight / 2 + (titleYPosition / 2);
        int centerX = gp.screenWidth / 2 - buttonWidth / 2;

        this.easyButton = new Rectangle(centerX, startY, buttonWidth, buttonHeight);
        this.mediumButton = new Rectangle(centerX, startY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
        this.hardButton = new Rectangle(centerX, startY + (buttonHeight + buttonSpacing) * 2, buttonWidth, buttonHeight);

        // --- Listener untuk DifficultyScene ---
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gp.gameState == GameState.DIFFICULTY_SELECT) {
                    Point p = e.getPoint();
                    String selectedDifficulty = null;

                    if (easyButton.contains(p)) {
                        selectedDifficulty = LEVEL_EASY;
                    } else if (mediumButton.contains(p)) {
                        selectedDifficulty = LEVEL_MEDIUM;
                    } else if (hardButton.contains(p)) {
                        selectedDifficulty = LEVEL_HARD;
                    }

                    if (selectedDifficulty != null) {
                        startGame(selectedDifficulty);
                    }
                }
            }
        });

        gp.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gp.gameState == GameState.DIFFICULTY_SELECT) {
                    Point p = e.getPoint();
                    boolean updated = false;

                    if (easyHover != easyButton.contains(p)) { easyHover = !easyHover; updated = true; }
                    if (mediumHover != mediumButton.contains(p)) { mediumHover = !mediumHover; updated = true; }
                    if (hardHover != hardButton.contains(p)) { hardHover = !hardHover; updated = true; }

                    if (updated) {
                        gp.repaint(); // Panggil repaint agar efek hover terlihat
                    }
                }
            }
        });
    }

    private void startGame(String difficulty) {
        // Logika untuk menentukan waktu berdasarkan tingkat kesulitan
        int timeLimitSeconds = 0;
        switch (difficulty) {
            case LEVEL_EASY:
                timeLimitSeconds = 120; // 2 Menit
                break;
            case LEVEL_MEDIUM:
                timeLimitSeconds = 90;  // 1.5 Menit
                break;
            case LEVEL_HARD:
                timeLimitSeconds = 60;  // 1 Menit
                break;
        }

        // Output pesan di konsol
        System.out.println("Memulai Game Level: " + difficulty + " dengan Waktu: " + timeLimitSeconds + " detik.");

        // Atur ulang (reset) timer dengan waktu yang baru
        gp.uiTimer.resetTime(timeLimitSeconds);
        // TODO: Tambahkan logika reset game lainnya (misalnya skor, player position)

        // Ganti state ke bermain
        gp.gameState = GameState.PLAYING;
        gp.repaint(); // Repaint untuk langsung menampilkan PlayScene
    }

    public void draw(Graphics2D g2) {

        // 1. Gambar Background (Gambar penuh atau hitam jika gambar gagal dimuat)
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        // 2. Gambar Teks Judul
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.setColor(Color.WHITE);

        // Menggunakan shadow untuk kontras yang lebih baik terhadap gambar latar
        g2.setColor(Color.BLACK);
        int shadowX = getXforCenteredTextInRect(g2, titleText, gp.screenWidth) + 2;
        int shadowY = titleYPosition + 35 + 2;
        g2.drawString(titleText, shadowX, shadowY);

        // Teks utama
        g2.setColor(Color.YELLOW); // Warna cerah agar menonjol
        int textX = getXforCenteredTextInRect(g2, titleText, gp.screenWidth);
        int textY = titleYPosition + 35; // 48px dari atas + tinggi font (sekitar 35)
        g2.drawString(titleText, textX, textY);

        // 3. Gambar Tombol Pilihan Kesulitan
        drawButton(g2, easyButton, LEVEL_EASY, easyHover);
        drawButton(g2, mediumButton, LEVEL_MEDIUM, mediumHover);
        drawButton(g2, hardButton, LEVEL_HARD, hardHover);
    }

    /**
     * Helper untuk mendapatkan koordinat X agar teks berada di tengah area Rectangle.
     */
    public int getXforCenteredTextInRect(Graphics2D g2, String text, int rectWidth) {
        FontMetrics fm = g2.getFontMetrics();
        int length = fm.stringWidth(text);
        int xOffset = (rectWidth - length) / 2;
        return xOffset;
    }

    /**
     * Menggambar tombol dengan efek hover.
     */
    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        // Warna tombol
        g2.setColor(hover ? new Color(255, 180, 50, 200) : new Color(255, 210, 100)); // Emas/Oranye
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        // Border tombol
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setFont(new Font("Poppins", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int tx = rect.x + (rect.width - textWidth) / 2;
        int ty = rect.y + (rect.height + textHeight) / 2 - 6;

        g2.setColor(Color.BLACK);
        g2.drawString(text, tx, ty);
    }
}