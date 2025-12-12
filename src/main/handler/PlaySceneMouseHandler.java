package main.handler;

import main.util.GameState;
import main.scene.PlayScene;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class PlaySceneMouseHandler {

    private PlayScene scene;

    public PlaySceneMouseHandler(PlayScene scene) {
        this.scene = scene;
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();

        // Cek jika tombol Pause diklik
        if (scene.getPauseButton().contains(p)) {
            scene.gp.changeGameState(GameState.PAUSE);
        }
    }

    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        // Cek hover untuk tombol Pause
        boolean hover = scene.getPauseButton().contains(p);
        scene.setPauseHover(hover);
    }
}