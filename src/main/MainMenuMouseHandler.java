package main;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class MainMenuMouseHandler {

    private MainMenuScene scene;

    public MainMenuMouseHandler(MainMenuScene scene) {
        this.scene = scene;
    }

    public void mousePressed(MouseEvent e) {
        // Pastikan kita hanya memproses klik jika state memang MAINMENU
        // (Jaga-jaga jika input bocor dari state lain)
        if (scene.gp.gameState != GameState.MAINMENU) return;

        Point p = e.getPoint();

        if (scene.getPlayButton().contains(p)) {
            // Sesuai kode conflict Anda: Masuk ke Difficulty Select
            scene.gp.gameState = GameState.DIFFICULTY_SELECT;

            // Opsi: Reset hover agar bersih saat kembali ke menu nanti
            scene.setPlayHover(false);

            // Trigger repaint atau load scene baru
            scene.gp.repaint();

        } else if (scene.getExitButton().contains(p)) {
            System.exit(0);
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (scene.gp.gameState != GameState.MAINMENU) return;

        Point p = e.getPoint();
        boolean needsRepaint = false;

        // 1. Cek Hover Play Button
        boolean isPlayHovering = scene.getPlayButton().contains(p);
        // Kita cek state sebelumnya (lewat getter field scene atau logic sendiri)
        // Disini kita overwrite saja, tapi idealnya cek dulu biar hemat resource
        scene.setPlayHover(isPlayHovering);

        // 2. Cek Hover Exit Button
        boolean isExitHovering = scene.getExitButton().contains(p);
        scene.setExitHover(isExitHovering);

        // Kita panggil repaint agar visual tombol berubah warna (Kuning/Putih/Darken)
        // Kalau mau optimal: cek if (oldState != newState) needsRepaint = true;
        scene.gp.repaint();
    }
}