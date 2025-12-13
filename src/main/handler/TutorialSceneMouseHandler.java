package main.handler;

import main.util.GameState;
import main.scene.TutorialScene;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TutorialSceneMouseHandler extends MouseAdapter {

    private final TutorialScene scene;

    public TutorialSceneMouseHandler(TutorialScene scene) {
        this.scene = scene;
        scene.gp.addMouseListener(this);
        scene.gp.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (scene.gp.gameState == GameState.TUTORIAL) {
            Point p = e.getPoint();

            if (scene.getBackButton().contains(p)) {
                scene.gp.gameState = GameState.MAINMENU;
                scene.gp.repaint();
                System.out.println("Kembali ke Main Menu.");
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (scene.gp.gameState == GameState.TUTORIAL) {
            Point p = e.getPoint();
            boolean newHover = scene.getBackButton().contains(p);

            if (scene.backHover != newHover) {
                scene.setBackHover(newHover);
                scene.gp.repaint();
            }
        }
    }
}