package main;

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
        // Pastikan hanya mendeteksi klik jika sedang di layar RESULT
        if (gp.gameState == GameState.RESULT) {
            Point p = e.getPoint();
            // Cek apakah tombol menu diklik
            if (resultScene.getMenuButton().contains(p)) {
                // Kembali ke Menu Utama
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