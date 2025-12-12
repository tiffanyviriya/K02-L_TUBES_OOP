package main.scene;

import main.util.GamePanel;
import main.handler.PlaySceneMouseHandler;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PlayScene implements Scene {

    public GamePanel gp;
    private PlaySceneMouseHandler mouseHandler;

    // Tombol Pause (Pojok kanan atas)
    private Rectangle pauseButton;
    private Rectangle recipeButton;
    private boolean recipeHover = false;
    private boolean pauseHover = false;

    public PlayScene(GamePanel gp){
        this.gp = gp;
        this.mouseHandler = new PlaySceneMouseHandler(this);

        // Membuat tombol pause di pojok kanan atas
        // Ukuran 40x40, margin 10px dari kanan dan atas
        int btnSize = 40;
        pauseButton = new Rectangle(gp.screenWidth - btnSize - 10, 10, btnSize, btnSize);
        recipeButton = new Rectangle(gp.screenWidth - btnSize - 10, 60, btnSize, btnSize);
    }

    @Override
    public void update(){
        gp.playerM.update();
        gp.orderM.update();
        gp.tileM.update();
        // uiTimer diupdate di GamePanel karena butuh deltaTime
    }

    @Override
    public void draw(Graphics2D g2){
        gp.tileM.draw(g2); // Draw tile layer paling bawah
        gp.itemM.draw(g2);
        gp.playerM.draw(g2);
        gp.uiTimer.draw(g2);
        gp.orderM.draw(g2); // UI digambar terakhir (paling atas)

        drawPauseButton(g2);
        drawRecipeButton(g2);
    }

    private void drawPauseButton(Graphics2D g2) {
        g2.setColor(pauseHover ? new Color(255, 200, 200) : new Color(240, 240, 240));
        g2.fillRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 10, 10);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 10, 10);

        // Simbol Pause (Dua garis vertikal)
        g2.fillRect(pauseButton.x + 12, pauseButton.y + 10, 6, 20);
        g2.fillRect(pauseButton.x + 22, pauseButton.y + 10, 6, 20);
    }

    private void drawRecipeButton(Graphics2D g2) {
        g2.setColor(recipeHover ? new Color(255, 255, 200) : new Color(240, 240, 200)); // Warna Krem/Kuning
        g2.fillRoundRect(recipeButton.x, recipeButton.y, recipeButton.width, recipeButton.height, 10, 10);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(recipeButton.x, recipeButton.y, recipeButton.width, recipeButton.height, 10, 10);

        // Gambar Ikon Buku Sederhana (Kotak dengan garis)
        g2.drawRect(recipeButton.x + 8, recipeButton.y + 8, 24, 24);
        g2.drawLine(recipeButton.x + 12, recipeButton.y + 14, recipeButton.x + 28, recipeButton.y + 14);
        g2.drawLine(recipeButton.x + 12, recipeButton.y + 20, recipeButton.x + 28, recipeButton.y + 20);
        g2.drawLine(recipeButton.x + 12, recipeButton.y + 26, recipeButton.x + 28, recipeButton.y + 26);
    }

    // --- Implementasi Interface Scene ---

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    // --- Getters & Setters ---
    public Rectangle getPauseButton() { return pauseButton; }
    public void setPauseHover(boolean pauseHover) { this.pauseHover = pauseHover; }
    public Rectangle getRecipeButton() { return recipeButton; }
    public void setRecipeHover(boolean hover) { this.recipeHover = hover; }
}