package main.manager;

import main.util.Sound;

public class SoundManager {

    // CHANNEL SUARA
    private Sound music = new Sound();
    private Sound ambient = new Sound();
    private Sound se = new Sound();
    private Sound seLoop = new Sound();
    private Sound soundPot = new Sound();
    private Sound soundPan = new Sound();

    private int currentMusicId = -1;
    private boolean isMusicPlaying = false;

    public SoundManager() {
    }

    // --- MUSIC CONTROL ---
    public void playMusic(int i) {
        // Cek agar tidak merestart lagu yang sama jika sedang main
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

    // [PERBAIKAN] Tambahkan method ini agar GamePanel tidak error
    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    // --- AMBIENCE CONTROL ---
    public void playAmbience(int i) {
        ambient.setFile(i);
        ambient.setVolume(0.6f);
        ambient.play();
        ambient.loop();
    }

    public void stopAmbience() {
        ambient.stop();
    }

    // --- SFX CONTROL ---
    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public void playWinSound() {
        stopMusic();
        stopAmbience();
        playSE(8); // Index 8: Menang
    }

    public void playLoseSound() {
        stopMusic();
        stopAmbience();
        playSE(7); // Index 7: Game_Over
    }

    public void playSELoop(int i) {
        seLoop.setFile(i);
        seLoop.play();
        seLoop.loop();
    }

    public void stopSELoop() {
        seLoop.stop();
    }

    // --- COOKING SOUNDS ---
    public void playPotSound() {
        soundPot.setFile(1);
        soundPot.play();
        soundPot.loop();
    }

    public void stopPotSound() {
        soundPot.stop();
    }

    public void playPanSound() {
        soundPan.setFile(2);
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