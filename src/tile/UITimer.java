package tile;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import main.GamePanel;
import main.GameState;

/**
 * Kelas yang menangani logika hitungan mundur (timer) dan menggambar hasilnya
 * pada layar (UI).
 */
public class UITimer {

    private GamePanel gp;

    // --- Variabel Timer ---
    // Waktu total untuk level (misalnya, 2 menit = 120 detik)
    public int gameTimeSeconds;

    // Variabel untuk melacak kapan 1 detik telah berlalu (menggunakan Delta Time)
    private double timerAccumulator = 0;
    private final double ONE_SECOND_NANO = 1000000000.0; // 1 detik dalam nanodetik

    public UITimer(GamePanel gp, int initialTimeSeconds) {
        this.gp = gp;
        this.gameTimeSeconds = initialTimeSeconds;
    }

    /**
     * Memperbarui status timer berdasarkan waktu yang telah berlalu (deltaTime).
     * Metode ini harus dipanggil di GamePanel.update().
     * * @param deltaTime Waktu yang telah berlalu sejak frame terakhir dalam nanodetik.
     */
    public void update(double deltaTime) {
        // Hanya kurangi waktu jika masih ada waktu tersisa
        if (gameTimeSeconds > 0) {
            timerAccumulator += deltaTime;

            // Jika akumulator melebihi 1 detik
            if (timerAccumulator >= ONE_SECOND_NANO) {
                gameTimeSeconds--; // Kurangi 1 detik
                timerAccumulator -= ONE_SECOND_NANO; // Reset akumulator
            }

            // Cek apakah waktu sudah habis
            if (gameTimeSeconds <= 0) {
                gameTimeSeconds = 0; // Pastikan tidak negatif
                // Mengubah status game, meniru level Overcooked! selesai
                gp.gameState = GameState.PAUSE;
                System.out.println("WAKTU HABIS! LEVEL SELESAI.");
            }
        }
    }

    /**
     * Menggambar teks Timer di sudut kanan bawah GamePanel.
     * Metode ini harus dipanggil di GamePanel.paintComponent().
     * * @param g2 Objek Graphics2D untuk menggambar.
     */
    public void draw(Graphics2D g2) {

        // Konversi detik menjadi format Menit:Detik (MM:SS)
        int minutes = gameTimeSeconds / 60;
        int seconds = gameTimeSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        // Atur Gaya Teks
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(Color.WHITE);

        // Jika waktu kurang dari 10 detik, ubah warna menjadi Merah
        if (gameTimeSeconds <= 10 && gameTimeSeconds > 0) {
            g2.setColor(Color.RED);
        }

        // Teks yang akan digambar
        String text = "Waktu: " + timeString;

        // Tentukan Posisi Sudut Kanan Bawah

        // Lebar total teks yang digambar
        int textWidth = g2.getFontMetrics().stringWidth(text);

        // Posisi X: screenWidth - textWidth - padding (misalnya 20 pixel dari tepi kanan)
        int x = gp.screenWidth - textWidth - 20;

        // Posisi Y: screenHeight - padding (misalnya 20 pixel dari tepi bawah)
        // Note: Koordinat Y adalah dasar teks (baseline)
        int y = gp.screenHeight - 20;

        // Gambar Teks
        g2.drawString(text, x, y);
    }
}