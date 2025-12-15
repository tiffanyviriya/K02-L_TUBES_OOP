package main.handler;

import main.scene.LobbyScene;
import java.awt.event.MouseEvent;

// Handler kosong karena Lobby hanya butuh Keyboard
public class LobbySceneMouseHandler {
    LobbyScene scene;
    public LobbySceneMouseHandler(LobbyScene scene) {
        this.scene = scene;
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}