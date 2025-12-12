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
    float volume = 1;

    public Sound() {
        try {
            // Index 0: Background Music
            soundURL[0] = getClass().getResource("/sound/nimonsbeat.wav");

            // Index 1: Suara Boiling Pot
            soundURL[1] = getClass().getResource("/sound/Boiling_Pot.wav");

            // Index 2: Suara Cooking Pan
            soundURL[2] = getClass().getResource("/sound/Cooking Pan.wav");

            // Index 3: Serving
            soundURL[3] = getClass().getResource("/sound/Serving(_).wav");

            // Index 6: Error Action (Bahan salah/Ga bisa ditaruh)
            soundURL[6] = getClass().getResource("/sound/Error_Action.wav");

            // Index 9: Cutting (Looping saat memotong)
            soundURL[9] = getClass().getResource("/sound/Cutting.wav");

            // Tambahan (Opsional, agar index tidak null jika dipanggil kode lama)
            soundURL[4] = getClass().getResource("/sound/Poin.wav"); // Misal untuk skor

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFile(int i) {
        try {
            if (soundURL[i] == null) return; // Safety check
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (fc != null) fc.setValue(volume);
    }

    public void play() {
        if (clip != null) clip.start();
    }

    public void loop() {
        if (clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null) clip.stop();
    }
}