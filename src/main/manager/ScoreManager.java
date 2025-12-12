package main.manager;

import java.util.HashSet;
import java.util.Set;

/**
 * Manager untuk menyimpan status permainan SEMENTARA (Session-based).
 * Data akan hilang saat game ditutup (Reset on Exit).
 * Juga menyimpan konfigurasi Target Score.
 */
public class ScoreManager {

    // Target Score untuk setiap level (Bisa disesuaikan)
    public static final int TARGET_SCORE_EASY = 100;
    public static final int TARGET_SCORE_MEDIUM = 200;
    public static final int TARGET_SCORE_HARD = 300;

    // Menyimpan daftar level yang sudah diselesaikan dalam sesi ini
    private Set<String> clearedLevels;

    public ScoreManager() {
        this.clearedLevels = new HashSet<>();
    }

    /**
     * Mengambil Target Skor berdasarkan difficulty string.
     */
    public int getTargetScore(String difficulty) {
        if (difficulty == null) return TARGET_SCORE_EASY;

        switch (difficulty.toUpperCase()) {
            case "MEDIUM": return TARGET_SCORE_MEDIUM;
            case "HARD": return TARGET_SCORE_HARD;
            case "EASY":
            default: return TARGET_SCORE_EASY;
        }
    }

    /**
     * Menandai level sebagai "SUCCESS" / "CLEARED".
     */
    public void setLevelCleared(String difficulty) {
        if (difficulty != null) {
            clearedLevels.add(difficulty.toUpperCase());
        }
    }

    /**
     * Mengecek apakah level sudah diselesaikan.
     */
    public boolean isLevelCleared(String difficulty) {
        if (difficulty == null) return false;
        return clearedLevels.contains(difficulty.toUpperCase());
    }
}