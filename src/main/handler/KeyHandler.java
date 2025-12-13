package main.handler;

import main.util.GamePanel; // Pastikan import ini ada
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp; // Tambahkan referensi ke GamePanel

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean actionPressed; // V
    public boolean interactPressed; // C
    public boolean switchPressed; // B

    // Tombol Dash dan Throw
    public boolean dashPressed;   // L
    public boolean throwPressed;  // T

    // [PERBAIKAN] Tambahkan Constructor ini
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;

        if (code == KeyEvent.VK_V) actionPressed = true;
        if (code == KeyEvent.VK_C) interactPressed = true;
        if (code == KeyEvent.VK_B) switchPressed = true;

        if (code == KeyEvent.VK_L) dashPressed = true;
        if (code == KeyEvent.VK_T) throwPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;

        if (code == KeyEvent.VK_V) actionPressed = false;
        if (code == KeyEvent.VK_C) interactPressed = false;
        if (code == KeyEvent.VK_B) switchPressed = false;

        if (code == KeyEvent.VK_L) dashPressed = false;
        if (code == KeyEvent.VK_T) throwPressed = false;
    }
}