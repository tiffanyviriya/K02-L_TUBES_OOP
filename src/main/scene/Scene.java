package main.scene;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public interface Scene {
    void update();
    void draw(Graphics2D g2);

    // Metode input yang akan dipanggil oleh GamePanel
    void mousePressed(MouseEvent e);
    void mouseMoved(MouseEvent e);
}
