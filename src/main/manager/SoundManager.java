package main.manager;

import main.util.Sound;

public class SoundManager {

    private Sound music = new Sound();
    private Sound se = new Sound();
    private Sound seLoop = new Sound(); // Untuk Cutting

    // [BARU] Channel khusus alat masak
    private Sound soundPot = new Sound();
    private Sound soundPan = new Sound();

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

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public void playSELoop(int i) {
        seLoop.setFile(i);
        seLoop.play();
        seLoop.loop();
    }

    public void stopSELoop() {
        seLoop.stop();
    }

    // --- METHOD BARU UNTUK ALAT MASAK ---

    public void playPotSound() {
        // Cek agar tidak restart sound kalau sudah main
        // (Sound class sederhana mungkin butuh logic tambahan, tapi play() biasanya aman)
        soundPot.setFile(1); // Index 1: Boiling Pot
        soundPot.play();
        soundPot.loop();
    }

    public void stopPotSound() {
        soundPot.stop();
    }

    public void playPanSound() {
        soundPan.setFile(2); // Index 2: Cooking Pan
        soundPan.play();
        soundPan.loop();
    }

    public void stopPanSound() {
        soundPan.stop();
    }

    public void stopAllCookingSounds() {
        stopPotSound();
        stopPanSound();
    }
}