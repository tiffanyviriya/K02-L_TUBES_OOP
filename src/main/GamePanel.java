package main;

import tile.TileManager;
import tile.UITimer;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    public final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;  // 48x48 tile
    public final int itemSize = 24;
    public final int maxScreenCol = 18;
    public final int maxScreenRow = 14;
    public final int screenWidth = tileSize * maxScreenCol; // 768
    public final int screenHeight = tileSize * maxScreenRow; // 576

    int FPS = 60;

    public GameState gameState;
    PlayScene playScene = new PlayScene(this);
    MainMenuScene mainMenuScene = new MainMenuScene(this);

    Thread gameThread;
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    public TileManager tileM = new TileManager(this);
    public ItemManager itemM = new ItemManager(this);
    public PlayerManager playerM = new PlayerManager(this, keyH);
    // ----------------------------------------------------
    // START: Deklarasi Timer Baru
    // ----------------------------------------------------
    public UITimer uiTimer;
    // ----------------------------------------------------
    // END: Deklarasi Timer Baru
    // ----------------------------------------------------
    public OrderManager orderM = new OrderManager(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        gameState = GameState.PLAYING;
        // Inisialisasi Timer dengan waktu awal (misalnya 150 detik)
        uiTimer = new UITimer(this, 150);
        gameState = GameState.MAINMENU;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        long lastTime = System.nanoTime(); // Waktu terakhir untuk perhitungan Delta Time

        while(gameThread != null) {
            long currentTime = System.nanoTime();
            // Delta time: Waktu yang telah berlalu sejak frame terakhir, dalam nanodetik
            double deltaTime = (double) (currentTime - lastTime);
            lastTime = currentTime;

            if (gameState == GameState.PLAYING) {
                update(deltaTime); // Meneruskan deltaTime ke update
            }

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

    public void update(double deltaTime) {
        playerM.update();
        orderM.update();

        // Panggil update pada objek timer
        uiTimer.update(deltaTime);
    }

    public void update() {
        if(gameState == GameState.MAINMENU){

        }
        else if (gameState == GameState.PLAYING){
            playScene.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if(gameState == GameState.MAINMENU){
            mainMenuScene.draw(g2);
        } else if (gameState == GameState.PLAYING) {
            playScene.draw(g2);
        }
        g2.dispose();
    }

}
