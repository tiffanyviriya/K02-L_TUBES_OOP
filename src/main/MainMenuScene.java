package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainMenuScene {
    GamePanel gp;

    private final Rectangle playButton = new Rectangle(334, 250, 200, 60);
    private final Rectangle exitButton = new Rectangle(334, 350, 200, 60);

    private boolean playHover = false;
    private boolean exitHover = false;

    private BufferedImage backgroundImage;

    public MainMenuScene(GamePanel gp){
        this.gp = gp;
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/ui/nimonscooked.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gp.gameState == GameState.MAINMENU) { // Tambahkan pengecekan state
                    Point p = e.getPoint();

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
    }

    public void draw(Graphics2D g2){
        g2.drawImage(backgroundImage, 0, 0, gp.getWidth(), gp.getHeight(), null);

        drawButton(g2, playButton, "PLAY", playHover);

        drawButton(g2, exitButton, "EXIT", exitHover);
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hover) {
        g2.setColor(hover ? new Color(180, 180, 255) : new Color(140, 140, 200));
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int tx = rect.x + (rect.width - textWidth) / 2;
        int ty = rect.y + (rect.height + textHeight) / 2 - 6;

        g2.setColor(Color.BLACK);
        g2.drawString(text, tx, ty);
    }
}