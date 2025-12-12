package main.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class FontManager {

    // Menyimpan font dasar agar tidak perlu load dari file berulang kali (Caching)
    private static Font baseFont;

    /**
     * Mengambil font pixel kustom dengan ukuran tertentu.
     * @param size Ukuran font yang diinginkan (float).
     * @return Font object (ByteBounce atau Monospaced jika gagal).
     */
    public static Font getPixelFont(float size) {
        if (baseFont == null) {
            loadFont();
        }
        // Mengembalikan turunan font dengan ukuran yang diminta
        return baseFont.deriveFont(size);
    }

    private static void loadFont() {
        try {
            // Pastikan path sesuai dengan struktur folder resources kamu
            InputStream fontStream = FontManager.class.getResourceAsStream("/font/ByteBounce.ttf");

            if (fontStream != null) {
                baseFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            } else {
                System.out.println("Warning: Custom font '/font/ByteBounce.ttf' not found. Using default.");
                baseFont = new Font("Monospaced", Font.BOLD, 32);
            }
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.out.println("Error loading font. Using fallback.");
            baseFont = new Font("Monospaced", Font.BOLD, 32);
        }
    }
}