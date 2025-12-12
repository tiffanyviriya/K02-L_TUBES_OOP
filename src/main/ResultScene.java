
package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Kelas yang menangani tampilan skor akhir dan statistik saat GameState.RESULT.
 * Kini menggunakan latar belakang gambar sebesar window penuh dan tombol.
 */
public class ResultScene {

    GamePanel gp;
    private BufferedImage backgroundImage;

    // Dimensi Card Hasil diubah menjadi ukuran penuh layar
    private final int cardWidth;
    private final int cardHeight;
    private final int cardX = 0; // Mulai dari 0
    private final int cardY = 0; // Mulai dari 0

    // Tombol untuk kembali ke Menu
    private final Rectangle menuButton;
    private boolean menuHover = false;

    public ResultScene(GamePanel gp) {
        this.gp = gp;

        // Ukuran Card disetel sama dengan ukuran layar
        this.cardWidth = gp.screenWidth;
        this.cardHeight = gp.screenHeight;

        // Posisi Tombol: Diletakkan di tengah bawah layar
        // Posisi X: Tengah layar - setengah lebar tombol
        // Posisi Y: Tinggi layar - 80 pixel dari bawah
        this.menuButton = new Rectangle(cardWidth/2 - 100, cardHeight - 80, 200, 40);

        // Muat Gambar Latar Belakang
        try {
            // Menggunakan gambar yang sama dengan MainMenuScene
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/resultscreen.png"));
        } catch (IOException e) {
            System.err.println("Gagal memuat gambar latar belakang ResultScene.");
            e.printStackTrace();
        }

        // --- Listener untuk ResultScene ---
        // Karena listener ini hanya aktif di ResultScene, kita harus memastikan
        // ia hanya mengubah state jika GameState saat ini adalah RESULT.
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gp.gameState == GameState.RESULT) {
                    Point p = e.getPoint();
                    if (menuButton.contains(p)) {
                        // Kembali ke Menu Utama
                        gp.gameState = GameState.MAINMENU;
                    }
                }
            }
        });

        gp.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gp.gameState == GameState.RESULT) {
                    Point p = e.getPoint();
                    menuHover = menuButton.contains(p);
                }
            }
        });
    }

    public void draw(Graphics2D g2) {

        // 1. Gambar Background Penuh (menggantikan overlay hitam)
        // Gambar latar belakang dengan ukuran penuh layar
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, cardX, cardY, cardWidth, cardHeight, null);
        } else {
            // Fallback jika gambar gagal dimuat
            g2.setColor(Color.BLACK);
            g2.fillRect(cardX, cardY, cardWidth, cardHeight);
        }

        // 2. Tambahkan lapisan semi-transparan (overlay) di atas gambar latar belakang
        // Ini membantu teks statistik lebih mudah dibaca
        g2.setColor(new Color(0, 0, 0, 100)); // Hitam semi-transparan (opacity 100 dari 255)
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);


        // 3. Teks & Statistik

        // Menggunakan font tunggal (seperti evaluation)
        g2.setFont(new Font("Arial", Font.ITALIC, 35)); // Font untuk semua statistik
        g2.setColor(Color.WHITE); // Warna teks

        // Posisi Y awal baru: 40 pixel dari atas layar
        int initialY = cardY + 200;

        int score = gp.orderM.score;
        int failedOrders = gp.orderM.failedOrders;

        // Baris 1: Skor Total (Diletakkan di tengah)
        String scoreText = "SKOR AKHIR: " + score;
        int statX = getXforCenteredTextInRect(g2, scoreText, cardWidth);
        int currentY = initialY;
        g2.drawString(scoreText, statX, currentY);

        // Baris 2: Order Gagal (Diletakkan di tengah)
        String failedText = "Order Gagal: " + failedOrders;
        statX = getXforCenteredTextInRect(g2, failedText, cardWidth);
        currentY += 40; // Jarak antar baris
        g2.drawString(failedText, statX, currentY);

        // Baris 3: Pesan Evaluasi (Diletakkan di tengah)
        String evaluation = (score > 100) ? "Kerja bagus, Chef Nimon!" : "Latihan lagi ya, Chef!";
        statX = getXforCenteredTextInRect(g2, evaluation, cardWidth);
        currentY += 80; // Jarak sedikit lebih besar untuk pesan evaluasi
        g2.drawString(evaluation, statX, currentY);

        // 4. Gambar Tombol Menu
        drawButton(g2, menuButton, "MENU UTAMA", menuHover);
    }

    /**
     * Helper untuk mendapatkan koordinat X agar teks berada di tengah area Rectangle.
     * Catatan: Karena cardWidth = screenWidth, ini akan menengahkan di layar.
     */
    public int getXforCenteredTextInRect(Graphics2D g2, String text, int rectWidth) {
        FontMetrics fm = g2.getFontMetrics();
        int length = fm.stringWidth(text);
        int xOffset = (rectWidth - length) / 2;
        return xOffset;
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        // Efek Tombol sama seperti MainMenuScene
        g2.setColor(hover ? new Color(180, 180, 255) : new Color(140, 140, 200));
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int tx = rect.x + (rect.width - textWidth) / 2;
        int ty = rect.y + (rect.height + textHeight) / 2 - 4;

        g2.setColor(Color.BLACK);
        g2.drawString(text, tx, ty);
    }
}