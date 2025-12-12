package main.util;

import main.handler.KeyHandler;
import main.manager.*;
import main.scene.*;
import tile.TileManager;

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

    public int FPS = 60;

    public GameState gameState;

    public String currentDifficulty = "EASY";

    public ScoreManager scoreM = new ScoreManager();

    // Scene objects
    public PlayScene playScene = new PlayScene(this);
    public MainMenuScene mainMenuScene = new MainMenuScene(this);
    public DifficultyScene difficultyScene = new DifficultyScene(this);
    public ResultScene resultScene = new ResultScene(this);
    public PauseScene pauseScene = new PauseScene(this);
    public TutorialScene tutorialScene = new TutorialScene(this);
    public RecipeBookScene recipeBookScene;

    Thread gameThread;
    public ScheduledExecutorService globalExecutor = Executors.newScheduledThreadPool(4);
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    public SoundManager soundM = new SoundManager();

    public TileManager tileM = new TileManager(this);
    public ItemManager itemM = new ItemManager(this);
    public PlayerManager playerM = new PlayerManager(this, keyH);

    public UITimer uiTimer;
    public OrderManager orderM = new OrderManager(this);

    double drawInterval = 1000000000.0/FPS;
    double nextDrawTime = System.nanoTime() + drawInterval;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        recipeBookScene = new RecipeBookScene(this);

        MouseAdapter globalMouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameState == GameState.MAINMENU) mainMenuScene.mousePressed(e);
                else if (gameState == GameState.PLAYING) playScene.mousePressed(e);
                else if (gameState == GameState.PAUSE) pauseScene.mousePressed(e);
                else if (gameState == GameState.DIFFICULTY_SELECT) difficultyScene.mousePressed(e);
                else if (gameState == GameState.RESULT) resultScene.mousePressed(e);
                else if (gameState == GameState.RECIPE_BOOK) recipeBookScene.mousePressed(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == GameState.MAINMENU) mainMenuScene.mouseMoved(e);
                else if (gameState == GameState.PLAYING) playScene.mouseMoved(e);
                else if (gameState == GameState.PAUSE) pauseScene.mouseMoved(e);
                else if (gameState == GameState.DIFFICULTY_SELECT) difficultyScene.mouseMoved(e);
                else if (gameState == GameState.RESULT) resultScene.mouseMoved(e);
                else if (gameState == GameState.RECIPE_BOOK) recipeBookScene.mousePressed(e);
            }
        };
        this.addMouseListener(globalMouseHandler);
        this.addMouseMotionListener(globalMouseHandler);

        uiTimer = new UITimer(this, 150);

        changeGameState(GameState.MAINMENU);
    }

    // --- METHOD RESET SENTRAL ---
    public void resetGame() {
        System.out.println("Resetting Game...");
        orderM.reset();
        itemM.reset();
        playerM.reset();
        tileM.reset();
        uiTimer.resetTime(0);
    }

    public void changeGameState(GameState newState) {
        this.gameState = newState;

        switch (newState) {
            case MAINMENU:
                // [UBAH] Masuk Menu -> Mainkan Nimonsbeat, Matikan Ambience
                soundM.stopAmbience();
                soundM.playMusic(0);
                break;

            case DIFFICULTY_SELECT:
                // (Opsional) Tetap mainkan Nimonsbeat di layar pilih level
                if (!soundM.isMusicPlaying()) {
                    soundM.playMusic(0);
                }
                break;

            case PLAYING:
                // [UBAH] Masuk Game -> Matikan Nimonsbeat, Mainkan Suara Resto
                soundM.stopMusic();     // Stop lagu menu
                soundM.playAmbience(5); // Play suara orang berisik (Looping)
                break;

            case PAUSE:
                // Tidak ada perubahan suara saat pause (tetap bunyi resto)
                break;

            case RESULT:
                // [UBAH] Selesai -> Matikan semua suara background
                soundM.stopMusic();
                soundM.stopAmbience();
                soundM.stopAllCookingSounds();
                soundM.stopSELoop();

                // Mainkan suara Menang/Kalah
                if (scoreM.isLevelCleared(currentDifficulty)) {
                    soundM.playWinSound();
                } else {
                    soundM.playLoseSound();
                }
                break;

            case RECIPE_BOOK:
                // Tidak ada perubahan
                break;
        }
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
        }
    }



    public void update(double deltaTime) {
        if (gameState == GameState.MAINMENU) {
            mainMenuScene.update();
            soundM.playMusic(0);
        }
        else if (gameState == GameState.PLAYING) {
            playScene.update();
            uiTimer.update(deltaTime);
        } else if (gameState == GameState.PAUSE) {
            pauseScene.update();
        }
        else if (gameState == GameState.RECIPE_BOOK) {
            recipeBookScene.update();
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if(gameState == GameState.MAINMENU){
            mainMenuScene.draw(g2);
        } else if (gameState == GameState.DIFFICULTY_SELECT) {
            difficultyScene.draw(g2);
        } else if (gameState == GameState.PLAYING) {
            playScene.draw(g2);
        } else if (gameState == GameState.PAUSE) {
            playScene.draw(g2);
            pauseScene.draw(g2);
        } else if (gameState == GameState.RESULT) {
            resultScene.draw(g2);
        } else if (gameState == GameState.TUTORIAL) {
            tutorialScene.draw(g2);
        }
        else if (gameState == GameState.RECIPE_BOOK) {
            playScene.draw(g2); // Gambar game di belakangnya
            recipeBookScene.draw(g2); // Gambar buku di atasnya
        }
        g2.dispose();
    }
}