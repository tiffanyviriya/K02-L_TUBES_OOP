package main.handler;

import main.scene.DifficultyScene;
import main.util.GamePanel;
import main.util.GameState;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DifficultySceneMouseHandler extends MouseAdapter {

    private DifficultyScene difficultyScene;
    private GamePanel gp;

    public DifficultySceneMouseHandler(DifficultyScene difficultyScene, GamePanel gp) {
        this.difficultyScene = difficultyScene;
        this.gp = gp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gp.gameState == GameState.DIFFICULTY_SELECT) {
            Point p = e.getPoint();

            // Cek tombol mana yang diklik
            if (difficultyScene.getEasyButton().contains(p)) {
                difficultyScene.startGame(DifficultyScene.LEVEL_EASY);
            } else if (difficultyScene.getMediumButton().contains(p)) {
                difficultyScene.startGame(DifficultyScene.LEVEL_MEDIUM);
            } else if (difficultyScene.getHardButton().contains(p)) {
                difficultyScene.startGame(DifficultyScene.LEVEL_HARD);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gp.gameState == GameState.DIFFICULTY_SELECT) {
            Point p = e.getPoint();
            boolean updated = false;

            // Cek hover untuk setiap tombol dan update status di scene
            // Kita menggunakan setter untuk mengubah nilai boolean di Scene
            boolean isEasy = difficultyScene.getEasyButton().contains(p);
            if (difficultyScene.isEasyHover() != isEasy) {
                difficultyScene.setEasyHover(isEasy);
                updated = true;
            }

            boolean isMedium = difficultyScene.getMediumButton().contains(p);
            if (difficultyScene.isMediumHover() != isMedium) {
                difficultyScene.setMediumHover(isMedium);
                updated = true;
            }

            boolean isHard = difficultyScene.getHardButton().contains(p);
            if (difficultyScene.isHardHover() != isHard) {
                difficultyScene.setHardHover(isHard);
                updated = true;
            }

            // Hanya repaint jika ada perubahan status hover agar performa terjaga
            if (updated) {
                gp.repaint();
            }
        }
    }
}