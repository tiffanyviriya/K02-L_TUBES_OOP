package main.handler;

import main.util.GameState;
import main.scene.PauseScene;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class PauseSceneMouseHandler {

    private PauseScene scene;

    public PauseSceneMouseHandler(PauseScene scene) {
        this.scene = scene;
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();

        if (scene.getResumeButton().contains(p)) {
            scene.gp.changeGameState(GameState.PLAYING);
        } else if (scene.getMenuButton().contains(p)) {
            scene.gp.changeGameState(GameState.MAINMENU);
        }
    }

    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        scene.setResumeHover(scene.getResumeButton().contains(p));
        scene.setMenuHover(scene.getMenuButton().contains(p));
    }
}