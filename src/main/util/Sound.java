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
            // Index 0: BGM
            soundURL[0] = getClass().getResource("/sound/nimonsbeat.wav");

            // Index 1-2: Cooking
            soundURL[1] = getClass().getResource("/sound/Boiling_Pot.wav");
            soundURL[2] = getClass().getResource("/sound/Cooking Pan.wav");

            // Index 3: Serving
            soundURL[3] = getClass().getResource("/sound/Serving(_).wav");

            // [PENTING] Index 5: Ambience (Suara Resto)
            soundURL[5] = getClass().getResource("/sound/orangberisikdiresto.wav");

            // Index 6: Error
            soundURL[6] = getClass().getResource("/sound/Error_Action.wav");

            // [PENTING] Index 7: Game Over
            soundURL[7] = getClass().getResource("/sound/Game_Over.wav");

            // [PENTING] Index 8: Menang / Victory
            soundURL[8] = getClass().getResource("/sound/Menang.wav");

            // Index 9: Cutting
            soundURL[9] = getClass().getResource("/sound/Cutting.wav");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFile(int i) {
        try {
            if (soundURL[i] == null) return;
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
            clip.close(); // Close agar resource lepas
        }
    }

    public void setVolume(float v) {
        this.volume = v;
        if (fc != null) {
            if (v <= 0f) v = 0.0001f;
            float dB = (float) (Math.log(v) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
        }
    }
}