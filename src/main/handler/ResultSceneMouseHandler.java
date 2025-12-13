package main.handler;

import main.util.GamePanel;
import main.util.GameState;
import main.scene.ResultScene;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResultSceneMouseHandler extends MouseAdapter {

    private ResultScene resultScene;
    private GamePanel gp;

    public ResultSceneMouseHandler(ResultScene resultScene, GamePanel gp) {
        this.resultScene = resultScene;
        this.gp = gp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gp.gameState == GameState.RESULT) {
            Point p = e.getPoint();
            if (resultScene.getMenuButton().contains(p)) {
                gp.changeGameState(GameState.MAINMENU);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gp.gameState == GameState.RESULT) {
            Point p = e.getPoint();
            // Cek apakah mouse berada di atas tombol (hover), lalu update status di ResultScene
            boolean isHovering = resultScene.getMenuButton().contains(p);
            resultScene.setMenuHover(isHovering);
        }
    }
}