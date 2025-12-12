package main;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class MainMenuMouseHandler {

    private MainMenuScene scene;

    public MainMenuMouseHandler(MainMenuScene scene) {
        this.scene = scene;
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();

        if (scene.getPlayButton().contains(p)) {
            // Ubah state game menjadi PLAYING
            scene.gp.changeGameState(GameState.PLAYING);
        } else if (scene.getExitButton().contains(p)) {
            // Keluar dari aplikasi
            System.exit(0);
        }
    }

    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        // Cek hover untuk tombol Play dan Exit
        boolean isPlayHovering = scene.getPlayButton().contains(p);
        boolean isExitHovering = scene.getExitButton().contains(p);

        scene.setPlayHover(isPlayHovering);
        scene.setExitHover(isExitHovering);
    }
}