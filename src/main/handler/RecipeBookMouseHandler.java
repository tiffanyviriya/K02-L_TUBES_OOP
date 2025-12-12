package main.handler;

import main.scene.RecipeBookScene;
import main.util.GamePanel;
import main.util.GameState;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RecipeBookMouseHandler extends MouseAdapter {

    private RecipeBookScene scene;
    private GamePanel gp;

    public RecipeBookMouseHandler(RecipeBookScene scene, GamePanel gp) {
        this.scene = scene;
        this.gp = gp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gp.gameState == GameState.RECIPE_BOOK) {
            Point p = e.getPoint();
            // Cek tombol Close
            if (scene.getCloseButton().contains(p)) {
                gp.changeGameState(GameState.PLAYING); // Kembali main
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gp.gameState == GameState.RECIPE_BOOK) {
            Point p = e.getPoint();
            scene.setCloseHover(scene.getCloseButton().contains(p));
        }
    }
}