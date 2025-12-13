package main.manager;

import main.util.Sound;

/* Manajer untuk mengatur pemutaran musik, suara latar, efek suara, dan suara masakan */
public class SoundManager {

    private Sound music = new Sound();
    private Sound ambient = new Sound();
    private Sound se = new Sound();
    private Sound seLoop = new Sound();

    private Sound soundPot = new Sound();
    private Sound soundPan = new Sound();

    private int currentMusicId = -1;
    private boolean isMusicPlaying = false;

    /* Konstruktor default untuk inisialisasi manajer suara */
    public SoundManager() {
    }

    /* Memainkan musik latar berdasarkan indeks jika belum dimainkan */
    public void playMusic(int i) {
        if (currentMusicId == i && isMusicPlaying) return;
        stopMusic();
        music.setFile(i);
        music.play();
        music.loop();
        currentMusicId = i;
        isMusicPlaying = true;
    }

    /* Menghentikan pemutaran musik latar saat ini */
    public void stopMusic() {
        if (isMusicPlaying) {
            music.stop();
            isMusicPlaying = false;
        }
    }

    /* Mengecek apakah musik sedang dimainkan */
    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    /* Memainkan suara latar (ambience) secara berulang dengan volume tertentu */
    public void playAmbience(int i) {
        ambient.setFile(i);
        ambient.setVolume(0.7f);
        ambient.play();
        ambient.loop();
    }

    /* Menghentikan suara latar */
    public void stopAmbience() {
        ambient.stop();
    }

    /* Memainkan efek suara sekali putar */
    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    /* Menghentikan semua suara lain dan memainkan suara kemenangan */
    public void playWinSound() {
        stopMusic();
        stopAmbience();
        stopAllCookingSounds();
        stopSELoop();

        playSE(8);
    }

    /* Menghentikan semua suara lain dan memainkan suara kekalahan */
    public void playLoseSound() {
        stopMusic();
        stopAmbience();
        stopAllCookingSounds();
        stopSELoop();

        playSE(7);
    }

    /* Memainkan efek suara secara berulang */
    public void playSELoop(int i) {
        seLoop.setFile(i);
        seLoop.play();
        seLoop.loop();
    }

    /* Menghentikan efek suara berulang */
    public void stopSELoop() {
        seLoop.stop();
    }

    /* Memainkan suara panci mendidih secara berulang */
    public void playPotSound() {
        soundPot.setFile(1);
        soundPot.play();
        soundPot.loop();
    }

    /* Menghentikan suara panci */
    public void stopPotSound() {
        soundPot.stop();
    }

    /* Memainkan suara wajan menggoreng secara berulang */
    public void playPanSound() {
        soundPan.setFile(2);
        soundPan.play();
        soundPan.loop();
    }

    /* Menghentikan suara wajan */
    public void stopPanSound() {
        soundPan.stop();
    }

    /* Menghentikan semua suara memasak (panci dan wajan) */
    public void stopAllCookingSounds() {
        stopPotSound();
        stopPanSound();
    }
}