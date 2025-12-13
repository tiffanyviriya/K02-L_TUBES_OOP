package main.handler;

import main.util.GameState;
import main.scene.MainMenuScene;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class MainMenuSceneMouseHandler {

    private MainMenuScene scene;

    /* Konstruktor untuk inisialisasi handler mouse pada menu utama */
    public MainMenuSceneMouseHandler(MainMenuScene scene) {
        this.scene = scene;
    }

    /* Menangani event penekanan tombol mouse pada menu utama */
    public void mousePressed(MouseEvent e) {
        if (scene.gp.gameState != GameState.MAINMENU) return;

        Point p = e.getPoint();

        if (scene.getPlayButton().contains(p)) {
            scene.gp.gameState = GameState.DIFFICULTY_SELECT;
            scene.setPlayHover(false);
            scene.gp.repaint();

        } else if (scene.getTutorialButton().contains(p)) {
            scene.gp.gameState = GameState.TUTORIAL;
            scene.setTutorialHover(false);
            scene.gp.repaint();

        } else if (scene.getExitButton().contains(p)) {
            System.exit(0);
        }
    }

    /* Menangani event pergerakan mouse untuk efek visual pada tombol */
    public void mouseMoved(MouseEvent e) {
        if (scene.gp.gameState != GameState.MAINMENU) return;

        Point p = e.getPoint();
        boolean needsRepaint = false;

        boolean isPlayHovering = scene.getPlayButton().contains(p);
        scene.setPlayHover(isPlayHovering);

        boolean isTutorialHovering = scene.getTutorialButton().contains(p);
        scene.setTutorialHover(isTutorialHovering);

        boolean isExitHovering = scene.getExitButton().contains(p);
        scene.setExitHover(isExitHovering);

        scene.gp.repaint();
    }
}