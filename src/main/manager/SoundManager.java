package main.manager;

import main.util.Sound;

public class SoundManager {

    private Sound music = new Sound();
    private Sound se = new Sound(); // Sound Effect biasa
    private Sound seLoop = new Sound(); // Sound Effect khusus Looping (Cutting)

    private int currentMusicId = -1;
    private boolean isMusicPlaying = false;

    public SoundManager() {
    }

    public void playMusic(int i) {
        if (currentMusicId == i && isMusicPlaying) return;
        stopMusic();
        music.setFile(i);
        music.play();
        music.loop();
        currentMusicId = i;
        isMusicPlaying = true;
    }

    public void stopMusic() {
        if (isMusicPlaying) {
            music.stop();
            isMusicPlaying = false;
        }
    }

    // Play Sound Effect sekali (Error, Serving, dll)
    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    // [BARU] Play Sound Effect Looping (Cutting)
    public void playSELoop(int i) {
        seLoop.setFile(i);
        seLoop.play();
        seLoop.loop();
    }

    // [BARU] Stop Sound Effect Looping
    public void stopSELoop() {
        seLoop.stop();
    }
}