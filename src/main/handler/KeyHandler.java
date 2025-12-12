package main.handler;

import main.util.GamePanel;
import main.util.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    protected GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, interactPressed, switchPressed, actionPressed;
    // Tambahan untuk Dash dan Throw
    public boolean dashPressed, throwPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(gp.gameState == GameState.PLAYING) {
            if(code == KeyEvent.VK_W) { upPressed = true; }
            if(code == KeyEvent.VK_S) { downPressed = true; }
            if(code == KeyEvent.VK_A) { leftPressed = true; }
            if(code == KeyEvent.VK_D) { rightPressed = true; }
            if(code == KeyEvent.VK_C) { interactPressed = true; }
            if(code == KeyEvent.VK_V) { actionPressed = true;}
            if(code == KeyEvent.VK_B) { switchPressed = true; }

            // Tambahan Input Bonus
            if(code == KeyEvent.VK_Q) { dashPressed = true; }   // Dash
            if(code == KeyEvent.VK_E) { throwPressed = true; }  // Lempar
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if(code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if(code == KeyEvent.VK_C) {
            interactPressed = false;
        }
        if(code == KeyEvent.VK_V) {
            actionPressed = false;
        }
        if(code == KeyEvent.VK_B) {
            switchPressed = false;
        }

        // Reset Dash & Throw saat dilepas
        if(code == KeyEvent.VK_Q) {
            dashPressed = false;
        }
        if(code == KeyEvent.VK_E) {
            throwPressed = false;
        }
    }
}