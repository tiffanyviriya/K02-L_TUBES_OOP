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

    public final int tileSize = originalTileSize * scale;
    public final int itemSize = 24;
    public final int maxScreenCol = 18;
    public final int maxScreenRow = 14;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    int FPS = 60;

    // Ubah akses gameState menjadi private agar dipaksa lewat setter (optional, tapi disarankan)
    public GameState gameState;

    // Scene objects
    PlayScene playScene = new PlayScene(this);
    MainMenuScene mainMenuScene = new MainMenuScene(this);
    PauseScene pauseScene = new PauseScene(this);

    Thread gameThread;
    public ScheduledExecutorService globalExecutor = Executors.newScheduledThreadPool(4);
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    // --- PERUBAHAN 1: Ganti Sound manual dengan SoundManager ---
    public SoundManager soundM = new SoundManager();

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

        MouseAdapter globalMouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameState == GameState.MAINMENU) mainMenuScene.mousePressed(e);
                else if (gameState == GameState.PLAYING) playScene.mousePressed(e);
                else if (gameState == GameState.PAUSE) pauseScene.mousePressed(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == GameState.MAINMENU) mainMenuScene.mouseMoved(e);
                else if (gameState == GameState.PLAYING) playScene.mouseMoved(e);
                else if (gameState == GameState.PAUSE) pauseScene.mouseMoved(e);
            }
        };
        this.addMouseListener(globalMouseHandler);
        this.addMouseMotionListener(globalMouseHandler);

        uiTimer = new UITimer(this, 150);

        // --- PERUBAHAN 2: Set state awal menggunakan method changeGameState ---
        // Agar musik main menu langsung main saat game mulai
        changeGameState(GameState.MAINMENU);
    }

    // --- PERUBAHAN 3: Method Khusus untuk Ganti State & Musik ---
    // Panggil method ini setiap kali ingin ganti layar (misal dari tombol Play)
    public void changeGameState(GameState newState) {
        this.gameState = newState;

        // Logika Ganti Musik Berdasarkan State
        switch (gameState) {
            case MAINMENU:
                soundM.playMusic(0); // Mainkan lagu index 0 (Menu Theme)
                break;

            case PLAYING:
                soundM.stopMusic();
                System.out.println("Musik berganti");
                // soundM.playMusic(1); // Uncomment jika punya lagu in-game (index 1)
                break;

            case PAUSE:
                // Biasanya pause tidak ganti lagu, atau volume dikecilkan
                // Jadi biarkan kosong atau atur logika lain
                break;
        }
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

            update(deltaTime);
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;
                if(remainingTime < 0) remainingTime = 0;
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

    public void update(double deltaTime) {
        if (gameState == GameState.MAINMENU) {
            mainMenuScene.update();
        }
        else if (gameState == GameState.PLAYING) {
            playScene.update();
            uiTimer.update(deltaTime);
        } else if (gameState == GameState.PAUSE) {
            pauseScene.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // --- PERUBAHAN 4: Hapus logika playMusic dari sini! ---
        // paintComponent hanya untuk menggambar (visual)

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