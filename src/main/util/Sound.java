package main.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[30];
    FloatControl fc;
    float volume = 1f;

    public Sound() {
        try {
            // --- Index Fix dari Anda ---
            soundURL[0] = getClass().getResource("/sound/nimonsbeat.wav"); // BGM
            soundURL[1] = getClass().getResource("/sound/Boiling_Pot.wav");
            soundURL[2] = getClass().getResource("/sound/Cooking Pan.wav");
            soundURL[3] = getClass().getResource("/sound/Serving(_).wav");
            soundURL[6] = getClass().getResource("/sound/Error_Action.wav");
            soundURL[9] = getClass().getResource("/sound/Cutting.wav");

            // --- [WAJIB ADA] Index Tambahan untuk Ambience & Result ---
            // Tanpa ini, suara keramaian dan game over akan bisu/rusak!

            // Index 5: Ambience (Orang Berisik)
            soundURL[5] = getClass().getResource("/sound/orangberisikdiresto.wav");

            // Index 7: Game Over (Kalah)
            soundURL[7] = getClass().getResource("/sound/Game_Over.wav");

            // Index 8: Menang
            soundURL[8] = getClass().getResource("/sound/Menang.wav");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFile(int i) {
        try {
            if (soundURL[i] == null) {
                // System.out.println("Sound index " + i + " is null/not set!");
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(volume);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close(); // Penting: Close clip untuk menghemat memory resource
        }
    }

    public void setVolume(float v) {
        this.volume = v;
        if (fc != null) {
            // Rumus konversi float (0.0 - 1.0) ke Decibel
            // Mencegah error jika v <= 0
            if (v <= 0f) v = 0.0001f;
            float dB = (float) (Math.log(v) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
        }
    }
}