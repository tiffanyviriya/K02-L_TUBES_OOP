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
    // --- DEKLARASI DIFFICULTY SCENE BARU ---
    public DifficultyScene difficultyScene = new DifficultyScene(this);
    // ----------------------------------------
    // --- TAMBAH DEKLARASI RESULT SCENE ---\
    public ResultScene resultScene = new ResultScene(this);

    Thread gameThread;
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    public TileManager tileM = new TileManager(this);
    public ItemManager itemM = new ItemManager(this);
    public PlayerManager playerM = new PlayerManager(this, keyH);
    // ----------------------------------------------------\
    // START: Deklarasi Timer Baru
    // ----------------------------------------------------\
    // Nilai awal 120 detik (2 menit)
    public UITimer uiTimer = new UITimer(this, 120);
    // ----------------------------------------------------\
    // END: Deklarasi Timer Baru
    // ----------------------------------------------------\
    public OrderManager orderM = new OrderManager(this);

    double drawInterval = 1000000000.0/FPS; // 0.01666 seconds
    double nextDrawTime = System.nanoTime() + drawInterval;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Atur state awal ke Main Menu
        gameState = GameState.MAINMENU;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double lastTime = System.nanoTime();
        double currentTime;
        double deltaTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            // 1. UPDATE
            update(deltaTime);

            // 2. DRAW
            // Lakukan repaint (memanggil paintComponent)
            repaint();

            try {
                // Atur interval tidur untuk mempertahankan FPS
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
        if (gameState == GameState.PLAYING) {
            playerM.update();
            orderM.update();
            // Panggil update pada objek timer
            uiTimer.update(deltaTime);
        }
        // State lain seperti MAINMENU, DIFFICULTY_SELECT, PAUSE, dan RESULT tidak perlu update per frame
        // kecuali ada animasi atau logika spesifik di scene tersebut.
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if(gameState == GameState.MAINMENU){
            mainMenuScene.draw(g2);
        } else if (gameState == GameState.DIFFICULTY_SELECT) {
            difficultyScene.draw(g2); // Gambar Difficulty Scene
        } else if (gameState == GameState.PLAYING) {
            playScene.draw(g2);
        } else if (gameState == GameState.RESULT) {  //Ganti State ke RESULT
            playScene.draw(g2);
            resultScene.draw(g2);
        }

        g2.dispose();
    }

    /** KAYAKNYA GA PERLU
     * Ntar coba di buang sementara
     */
    public void update() {
        if(gameState == GameState.MAINMENU){

        }
        else if (gameState == GameState.PLAYING){
            playScene.update();
        }
    }
}