package main.manager;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class FontManager {

    private static Font baseFont;

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