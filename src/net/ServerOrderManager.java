package net;

import java.util.Random;

/**
 * Kelas ini berjalan di sisi Server untuk mengatur siklus hidup pesanan.
 * Server yang menentukan kapan order muncul dan jenisnya apa.
 */
public class ServerOrderManager {
    private GameServer server;
    private long lastSpawnTime;
    private final long SPAWN_INTERVAL = 15000; // Muncul setiap 15 detik
    private final String[] availableRecipes = {"Kappa Maki", "Ebi Maki", "Sakana Maki", "Fish Cucumber Roll"};
    private Random random = new Random();

    public ServerOrderManager(GameServer server) {
        this.server = server;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void update() {
        // Logika spawning order berdasarkan waktu
        if (System.currentTimeMillis() - lastSpawnTime > SPAWN_INTERVAL) {
            spawnNewOrder();
            lastSpawnTime = System.currentTimeMillis();
        }
    }

    private void spawnNewOrder() {
        String recipe = availableRecipes[random.nextInt(availableRecipes.length)];
        int duration = 60; // 60 detik
        // Broadcast ke semua client agar spawn order yang sama
        server.broadcast("ORDER_SPAWN|" + recipe + "|" + duration);
    }

    public void reset() {
        this.lastSpawnTime = System.currentTimeMillis();
    }
}