package main;

import tile.TileManager;
import tile.UITimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    // Scene objects
    PlayScene playScene = new PlayScene(this);
    MainMenuScene mainMenuScene = new MainMenuScene(this);
    PauseScene pauseScene = new PauseScene(this);

    Thread gameThread;
    public ScheduledExecutorService globalExecutor = Executors.newScheduledThreadPool(4);
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    public TileManager tileM = new TileManager(this);
    public ItemManager itemM = new ItemManager(this);
    public PlayerManager playerM = new PlayerManager(this, keyH);

    public UITimer uiTimer;
    public OrderManager orderM = new OrderManager(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // --- MOUSE LISTENER GLOBAL (PENTING untuk MainMenuScene) ---
        MouseAdapter globalMouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameState == GameState.MAINMENU) {
                    mainMenuScene.mousePressed(e);
                }
                else if (gameState == GameState.PLAYING) {
                    playScene.mousePressed(e);
                }
                else if (gameState == GameState.PAUSE) {
                    pauseScene.mousePressed(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == GameState.MAINMENU) {
                    mainMenuScene.mouseMoved(e);
                }
                else if (gameState == GameState.PLAYING) {
                    playScene.mouseMoved(e);
                }
                else if (gameState == GameState.PAUSE) {
                    pauseScene.mouseMoved(e);
                }
            }
        };
        this.addMouseListener(globalMouseHandler);
        this.addMouseMotionListener(globalMouseHandler);
        // -----------------------------------------------------------

        // Inisialisasi Timer
        uiTimer = new UITimer(this, 150);

        // Set state awal
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
        long lastTime = System.nanoTime();

        while(gameThread != null) {
            long currentTime = System.nanoTime();
            double deltaTime = (double) (currentTime - lastTime);
            lastTime = currentTime;

            // Panggil method update tunggal (tanpa if gameState disini)
            // Biarkan method update yang mengurus pengecekan state
            update(deltaTime);

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

    public void stopGame() {
        gameThread = null;
        if (globalExecutor != null && !globalExecutor.isShutdown()) {
            globalExecutor.shutdownNow();
            System.out.println("Global Executor stopped.");
        }
    }

    /**
     * INI ADALAH METHOD UPDATE GABUNGAN
     * Method ini menangani logika routing scene dan timer
     */
    public void update(double deltaTime) {
        if (gameState == GameState.MAINMENU) {
            mainMenuScene.update();
        }
        else if (gameState == GameState.PLAYING) {
            // 1. Update logika Scene (Player, Order, dll ada di dalam sini)
            playScene.update();

            // 2. Update Timer (karena butuh deltaTime)
            uiTimer.update(deltaTime);
        } else if (gameState == GameState.PAUSE) {
            pauseScene.update();
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
        } else if (gameState == GameState.PAUSE) {
            playScene.draw(g2);
            pauseScene.draw(g2);
        }
        g2.dispose();
    }
}