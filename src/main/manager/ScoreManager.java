package main.manager;

import java.util.HashSet;
import java.util.Set;

/* Manager untuk menyimpan status permainan sementara dan konfigurasi target score */
public class ScoreManager {

    public static final int TARGET_SCORE_EASY = 100;
    public static final int TARGET_SCORE_MEDIUM = 200;
    public static final int TARGET_SCORE_HARD = 300;

    private Set<String> clearedLevels;

    /* Menginisialisasi set untuk menyimpan level yang telah diselesaikan */
    public ScoreManager() {
        this.clearedLevels = new HashSet<>();
    }

    /* Mengambil target skor berdasarkan tingkat kesulitan */
    public int getTargetScore(String difficulty) {
        if (difficulty == null) return TARGET_SCORE_EASY;

        switch (difficulty.toUpperCase()) {
            case "MEDIUM": return TARGET_SCORE_MEDIUM;
            case "HARD": return TARGET_SCORE_HARD;
            case "EASY":
            default: return TARGET_SCORE_EASY;
        }
    }

    /* Menandai level tertentu sebagai selesai */
    public void setLevelCleared(String difficulty) {
        if (difficulty != null) {
            clearedLevels.add(difficulty.toUpperCase());
        }
    }

    /* Mengecek apakah level tertentu sudah diselesaikan */
    public boolean isLevelCleared(String difficulty) {
        if (difficulty == null) return false;
        return clearedLevels.contains(difficulty.toUpperCase());
    }
}