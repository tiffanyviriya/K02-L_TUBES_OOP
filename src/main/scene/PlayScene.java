package main.scene;

import main.util.GamePanel;
import main.handler.PlaySceneMouseHandler;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PlayScene implements Scene {

    public GamePanel gp;
    private PlaySceneMouseHandler mouseHandler;

    private Rectangle pauseButton;
    private Rectangle recipeButton;
    private boolean recipeHover = false;
    private boolean pauseHover = false;

    /* Konstruktor untuk inisialisasi scene permainan, handler input, dan tombol antarmuka */
    public PlayScene(GamePanel gp){
        this.gp = gp;
        this.mouseHandler = new PlaySceneMouseHandler(this);

        int btnSize = 40;
        pauseButton = new Rectangle(gp.screenWidth - btnSize - 10, 10, btnSize, btnSize);
        recipeButton = new Rectangle(gp.screenWidth - btnSize - 10, 60, btnSize, btnSize);
    }

    /* Memperbarui logika permainan termasuk pemain, pesanan, dan tile setiap frame */
    @Override
    public void update(){
        gp.playerM.update();
        gp.orderM.update();
        gp.tileM.update();
    }

    /* Menggambar seluruh elemen permainan dari layer terbawah hingga UI teratas */
    @Override
    public void draw(Graphics2D g2){
        gp.tileM.draw(g2);
        gp.itemM.draw(g2);
        gp.playerM.draw(g2);
        gp.uiTimer.draw(g2);
        gp.orderM.draw(g2);

        drawPauseButton(g2);
        drawRecipeButton(g2);
    }

    /* Menggambar tombol jeda dengan efek visual saat kursor berada di atasnya */
    private void drawPauseButton(Graphics2D g2) {
        g2.setColor(pauseHover ? new Color(255, 200, 200) : new Color(240, 240, 240));
        g2.fillRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 10, 10);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 10, 10);

        g2.fillRect(pauseButton.x + 12, pauseButton.y + 10, 6, 20);
        g2.fillRect(pauseButton.x + 22, pauseButton.y + 10, 6, 20);
    }

    /* Menggambar tombol buku resep dengan ikon buku sederhana */
    private void drawRecipeButton(Graphics2D g2) {
        g2.setColor(recipeHover ? new Color(255, 255, 200) : new Color(240, 240, 200));
        g2.fillRoundRect(recipeButton.x, recipeButton.y, recipeButton.width, recipeButton.height, 10, 10);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(recipeButton.x, recipeButton.y, recipeButton.width, recipeButton.height, 10, 10);

        g2.drawRect(recipeButton.x + 8, recipeButton.y + 8, 24, 24);
        g2.drawLine(recipeButton.x + 12, recipeButton.y + 14, recipeButton.x + 28, recipeButton.y + 14);
        g2.drawLine(recipeButton.x + 12, recipeButton.y + 20, recipeButton.x + 28, recipeButton.y + 20);
        g2.drawLine(recipeButton.x + 12, recipeButton.y + 26, recipeButton.x + 28, recipeButton.y + 26);
    }

    /* Menangani event penekanan tombol mouse */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.mousePressed(e);
    }

    /* Menangani event pergerakan mouse */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.mouseMoved(e);
    }

    /* Mendapatkan referensi area tombol jeda */
    public Rectangle getPauseButton() { return pauseButton; }

    /* Mengatur status hover pada tombol jeda */
    public void setPauseHover(boolean pauseHover) { this.pauseHover = pauseHover; }

    /* Mendapatkan referensi area tombol resep */
    public Rectangle getRecipeButton() { return recipeButton; }

    /* Mengatur status hover pada tombol resep */
    public void setRecipeHover(boolean hover) { this.recipeHover = hover; }
}