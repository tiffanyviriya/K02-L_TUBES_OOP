package main;

import environment.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    public final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;  // 48x48 tile
    public final int itemSize = 24;
    public final int maxScreenCol = 18;
    public final int maxScreenRow = 14;
    final int screenWidth = tileSize * maxScreenCol; // 768
    final int screenHeight = tileSize * maxScreenRow; // 576

    int FPS = 60;

    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;

    Thread gameThread;
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    public TileManager tileM = new TileManager(this);
    public ItemManager itemM = new ItemManager(this);
    public PlayerManager playerM = new PlayerManager(this, keyH);
    public AssetSetter aSetter = new AssetSetter(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        gameState = playState;
    }

    public void setupGame() {
        aSetter.setObject();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;

                if(remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime  += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


public void update() {
    System.out.println("Game loop running");
    playerM.update();
    
    // Update logic untuk Tile (terutama Cooking Station)
    for (int i = 0; i < maxScreenCol; i++) {
        for (int j = 0; j < maxScreenRow; j++) {
            int tileNum = tileM.mapTileNum[i][j];
            if (tileM.tile[tileNum] instanceof tile.CookingStation) {
                ((tile.CookingStation) tileM.tile[tileNum]).update();
            }
        }
    }
}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);
        itemM.draw(g2);
        playerM.draw(g2);

        g2.dispose();
    }

}
