package main.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UITimer {

    private GamePanel gp;

    public int gameTimeSeconds;

    private double timerAccumulator = 0;
    private final double ONE_SECOND_NANO = 1000000000.0;

    /* Konstruktor untuk inisialisasi timer dengan referensi panel game dan waktu awal */
    public UITimer(GamePanel gp, int initialTimeSeconds) {
        this.gp = gp;
        this.gameTimeSeconds = initialTimeSeconds;
    }

    /* Mereset waktu permainan ke nilai baru saat level dimulai */
    public void resetTime(int newTimeSeconds) {
        this.gameTimeSeconds = newTimeSeconds;
        this.timerAccumulator = 0;
    }

    /* Memperbarui status timer berdasarkan waktu yang berlalu dan mengubah state jika waktu habis */
    public void update(double deltaTime) {
        if (gp.gameState == GameState.PLAYING && gameTimeSeconds > 0) {
            timerAccumulator += deltaTime;

            if (timerAccumulator >= ONE_SECOND_NANO) {
                gameTimeSeconds--;
                timerAccumulator -= ONE_SECOND_NANO;
            }

            if (gameTimeSeconds <= 0) {
                gameTimeSeconds = 0;
                gp.gameState = GameState.RESULT;
            }
        }
    }

    /* Mengembalikan status apakah waktu permainan telah habis */
    public boolean isTimeUp(){
        return gameTimeSeconds <= 0;
    }

    /* Menggambar teks timer pada posisi sudut kanan bawah layar */
    public void draw(Graphics2D g2) {

        if (gp.gameState != GameState.PLAYING) {
            return;
        }

        int minutes = gameTimeSeconds / 60;
        int seconds = gameTimeSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(Color.WHITE);

        if (gameTimeSeconds <= 10 && gameTimeSeconds > 0) {
            g2.setColor(Color.RED);
        }

        String text = "Waktu: " + timeString;

        int textWidth = g2.getFontMetrics().stringWidth(text);

        int x = gp.screenWidth - textWidth - 20;

        int y = gp.screenHeight - 20;

        g2.drawString(text, x, y);
    }
}