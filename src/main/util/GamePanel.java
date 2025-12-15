package main.util;

import main.handler.KeyHandler;
import main.manager.*;
import main.scene.*;
import tile.TileManager;
import net.NetworkClient; // Import Net

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

    // --- NETWORK & BUFFER ---
    public NetworkClient netClient;
    public InputBuffer remoteInputBuffer = new InputBuffer();
    // ------------------------

    // Scene objects
    public PlayScene playScene = new PlayScene(this);
    public MainMenuScene mainMenuScene = new MainMenuScene(this);
    public DifficultyScene difficultyScene = new DifficultyScene(this);
    public ResultScene resultScene = new ResultScene(this);
    public PauseScene pauseScene = new PauseScene(this);
    public TutorialScene tutorialScene = new TutorialScene(this);
    public RecipeBookScene recipeBookScene;
    public LobbyScene lobbyScene; // Tambah Lobby

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

        // Init Network & Lobby
        netClient = new NetworkClient(this);
        lobbyScene = new LobbyScene(this);

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
                // Lobby tidak perlu mouse
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

    public void resetGame() {
        System.out.println("Resetting Game...");
        orderM.reset();
        itemM.reset();
        playerM.reset();
        tileM.reset();
        uiTimer.resetTime(0);
        remoteInputBuffer.reset(); // Reset buffer input juga
    }

    public void changeGameState(GameState newState) {
        if (this.gameState == newState) return;

        this.gameState = newState;

        switch (newState) {
            case MAINMENU:
                soundM.stopAmbience();
                soundM.playMusic(0);
                break;
            case DIFFICULTY_SELECT:
                if (!soundM.isMusicPlaying()) soundM.playMusic(0);
                break;

            case LOBBY: // Stop lagu menu saat masuk lobby (opsional)
                break;

            case PLAYING:
                soundM.stopMusic();
                soundM.playAmbience(5);
                break;
            case PAUSE:
                break;
            case RESULT:
                soundM.stopMusic();
                soundM.stopAmbience();
                soundM.stopAllCookingSounds();
                soundM.stopSELoop();
                if (scoreM.isLevelCleared(currentDifficulty)) {
                    soundM.playSE(8);
                } else {
                    soundM.playSE(7);
                }
                break;
            case RECIPE_BOOK:
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

            if (gameState == GameState.PLAYING) {
                if (orderM.lives <= 0) {
                    changeGameState(GameState.RESULT);
                }
                if (uiTimer.isTimeUp()) {
                    changeGameState(GameState.RESULT);
                }
            }

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
        else if (gameState == GameState.LOBBY) { // Update Lobby
            lobbyScene.update();
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
        } else if (gameState == GameState.RECIPE_BOOK) {
            playScene.draw(g2);
            recipeBookScene.draw(g2);
        } else if (gameState == GameState.LOBBY) { // Draw Lobby
            lobbyScene.draw(g2);
        }
        g2.dispose();
    }
}