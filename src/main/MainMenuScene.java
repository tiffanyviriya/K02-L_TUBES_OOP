package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MainMenuScene implements Scene {

    GamePanel gp;

    private final Rectangle playButton = new Rectangle(334, 200, 200, 60);
    private final Rectangle exitButton = new Rectangle(334, 300, 200, 60);

    private boolean playHover = false;
    private boolean exitHover = false;

    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;

    private Font pixelFont;

    private MainMenuMouseHandler mouseHandler;

    public MainMenuScene(GamePanel gp) {
        this.gp = gp;

        // -----------------------------------------------------------
        // 1. Setup Posisi Tombol
        // -----------------------------------------------------------
        int buttonWidth = 200;
        int buttonHeight = 60;

        int buttonX = (gp.screenWidth / 2) - (buttonWidth / 2);
        int playButtonY = (gp.screenHeight / 2) - buttonHeight;
        int exitButtonY = playButtonY + buttonHeight + 40;

        playButton = new Rectangle(buttonX, playButtonY, buttonWidth, buttonHeight);
        exitButton = new Rectangle(buttonX, exitButtonY, buttonWidth, buttonHeight);

        // -----------------------------------------------------------
        // 2. Load Resources (Gambar & Font)
        // -----------------------------------------------------------
        try {
            // Load Gambar
            // Pastikan path sesuai dengan struktur folder project Anda
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/nimonscooked.png"));
            buttonImage = ImageIO.read(getClass().getResourceAsStream("/ui/buttonUI.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gp.gameState == GameState.MAINMENU) { // Tambahkan pengecekan state
                    Point p = e.getPoint();
            // Load Custom Font
            // Ganti "/font/pixel.ttf" dengan nama file font yang Anda miliki
            InputStream is = getClass().getResourceAsStream("/font/ByteBounce.ttf");
            if (is != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(36f);
            } else {
                // Fallback jika file tidak ditemukan
                System.out.println("Warning: Font file not found, using default.");
                pixelFont = new Font("Monospaced", Font.BOLD, 32);
            }

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.out.println("Warning: Failed to load resources.");
            // Fallback font aman jika terjadi error
            pixelFont = new Font("Monospaced", Font.BOLD, 32);
        }
                    if (playButton.contains(p)) {
                        // Aksi baru: Pindah ke layar pemilihan kesulitan
                        gp.gameState = GameState.DIFFICULTY_SELECT;
                        gp.repaint(); // Repaint untuk menampilkan DifficultyScene
                    } else if (exitButton.contains(p)) {
                        System.exit(0);
                    }
                }
            }
        });

        mouseHandler = new MainMenuMouseHandler(this);
    }

        gp.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gp.gameState == GameState.MAINMENU) { // Tambahkan pengecekan state
                    Point p = e.getPoint();
                    boolean updated = false;

                    if (playHover != playButton.contains(p)) { playHover = !playHover; updated = true; }
                    if (exitHover != exitButton.contains(p)) { exitHover = !exitHover; updated = true; }

                    if (updated) {
                        gp.repaint(); // Panggil repaint agar efek hover terlihat
                    }
                }
            }
        });
    }

    public void update(){
        // Tidak ada yang perlu diperbarui secara berkala di menu
    @Override
    public void update() {
        // Logika update animasi menu
    }

    @Override
    public void draw(Graphics2D g2) {
        // A. Gambar Background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        // B. Gambar Tombol
        drawButton(g2, playButton, "PLAY", playHover);
        drawButton(g2, exitButton, "EXIT", exitHover);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    // --- Helper Methods ---

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {

        // 1. Gambar Background Tombol
        if (buttonImage != null) {
            g2.drawImage(buttonImage, rect.x, rect.y, rect.width, rect.height, null);
        } else {
            g2.setColor(Color.GRAY);
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // 2. Efek Gelap saat Hover (Overlay)
        if (hover) {
            g2.setColor(new Color(0, 0, 0, 80)); // Hitam transparan
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // 3. Set Custom Font
        // Kita menggunakan variabel pixelFont yang sudah di-load di konstruktor
        g2.setFont(pixelFont);

        // 4. Hitung Posisi Teks (Center Alignment)
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // Menggunakan Ascent agar lebih akurat untuk font pixel

        int tx = rect.x + (rect.width - textWidth) / 2 - 2;
        int ty = rect.y + (rect.height + textHeight) / 2 + 2;

        // Sedikit penyesuaian vertikal (fine-tuning) tergantung fontnya
        // Kadang font pixel titik tengahnya agak beda, sesuaikan angka -4 ini jika perlu
        ty -= 4;

        // 5. Gambar Teks dengan Shadow
        if (hover) {
            // Shadow
            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2); // Shadow lebih tebal (3px) agar retro banget
            // Main Text
            g2.setColor(Color.YELLOW);
            g2.drawString(text, tx, ty);
        } else {
            // Shadow
            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2);
            // Main Text
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);
        }
    }

    public Rectangle getPlayButton() { return playButton; }
    public Rectangle getExitButton() { return exitButton; }
    public void setPlayHover(boolean playHover) { this.playHover = playHover; }
    public void setExitHover(boolean exitHover) { this.exitHover = exitHover; }
}