package main.manager;

import main.util.Sound;

public class SoundManager {

    private Sound music = new Sound();
    private Sound ambient = new Sound(); // Channel khusus Ambience
    private Sound se = new Sound();
    private Sound seLoop = new Sound();

    private Sound soundPot = new Sound();
    private Sound soundPan = new Sound();

    private int currentMusicId = -1;
    private boolean isMusicPlaying = false;

    public SoundManager() {
    }

    // --- MUSIC ---
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

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    // --- AMBIENCE (Suara Resto) ---
    public void playAmbience(int i) {
        ambient.setFile(i);
        ambient.setVolume(0.7f); // Volume 70%
        ambient.play();
        ambient.loop(); // Loop terus sampai di-stop
    }

    public void stopAmbience() {
        ambient.stop();
    }

    // --- SFX & WIN/LOSE ---
    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public void playWinSound() {
        // Matikan semua suara background sebelum mainkan victory
        stopMusic();
        stopAmbience();
        stopAllCookingSounds();
        stopSELoop();

        playSE(8); // Index 8: Menang.wav
    }

    public void playLoseSound() {
        // Matikan semua suara background sebelum mainkan game over
        stopMusic();
        stopAmbience();
        stopAllCookingSounds();
        stopSELoop();

        playSE(7); // Index 7: Game_Over.wav
    }

    // --- COOKING & CUTTING ---
    public void playSELoop(int i) {
        seLoop.setFile(i);
        seLoop.play();
        seLoop.loop();
    }

    public void stopSELoop() {
        seLoop.stop();
    }

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