package main;

public class SoundManager {

    private Sound music = new Sound();
    private Sound se = new Sound();

    // Menyimpan ID musik yang sedang diputar agar tidak di-restart berulang kali
    private int currentMusicId = -1;
    private boolean isMusicPlaying = false;

    public SoundManager() {
        // Constructor kosong
    }

    public void playMusic(int i) {
        // Cek: Jika musik yang diminta sama dengan yang sedang main, jangan lakukan apa-apa
        // Ini mencegah lagu restart dari awal setiap kali update loop berjalan
        if (currentMusicId == i && isMusicPlaying) {
            return;
        }

        // Jika lagu beda, stop yang lama, mainkan yang baru
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
            // Kita tidak reset currentMusicId ke -1 disini agar kita tahu
            // lagu apa yang terakhir dimainkan (opsional)
        }
    }

    public void playSE(int i) {
        // Sound Effect (SE) tidak perlu di-loop dan bisa ditumpuk
        se.setFile(i);
        se.play();
    }

    // Method helper jika nanti ingin mute semua suara
    public void stopAll() {
        music.stop();
        se.stop(); // Asumsi class Sound punya method stop
        isMusicPlaying = false;
    }
}