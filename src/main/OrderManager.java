package main;

import environment.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

public class OrderManager {
    GamePanel gp;

    // Daftar Order Aktif
    public ArrayList<Order> activeOrders = new ArrayList<>();

    // Daftar Resep yang MUNGKIN muncul di level ini
    public ArrayList<Recipe> levelRecipes = new ArrayList<>();

    // Konfigurasi Game
    private final int MAX_ORDERS = 5;
    public int score = 0;
    public int failedOrders = 0;

    // Timer spawn
    private int spawnTimer = 0;

    public OrderManager(GamePanel gp) {
        this.gp = gp;
        setupRecipes(); // Load resep sesuai level
    }

    // Definisikan resep-resep di sini
    private void setupRecipes() {
        // 1. Kappa Maki
        Recipe kappaMaki = new Recipe("Kappa Maki", 120, 60);
        kappaMaki.addIngredient("nori", IngredientState.RAW);
        kappaMaki.addIngredient("rice", IngredientState.COOKED);
        kappaMaki.addIngredient("cucumber", IngredientState.CHOPPED);

        // 2. Sakana Maki
        Recipe sakanaMaki = new Recipe("Sakana Maki", 150, 60);
        sakanaMaki.addIngredient("nori", IngredientState.RAW);
        sakanaMaki.addIngredient("rice", IngredientState.COOKED);
        sakanaMaki.addIngredient("fish", IngredientState.RAW); // Sesuai spec map A

        // Masukkan ke daftar kemungkinan resep
        levelRecipes.add(kappaMaki);
        levelRecipes.add(sakanaMaki);
    }

    public void update() {
        // 1. Spawn Order Baru jika slot kosong
        if (activeOrders.size() < MAX_ORDERS) {
            spawnOrder();
        }

        // 2. Update Timer setiap Order
        Iterator<Order> iterator = activeOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            order.update();

            // Cek Expired (Time Limit Habis)
            if (order.isExpired) {
                System.out.println("Order " + order.recipe.name + " GAGAL! (Waktu Habis)");
                score -= 50; // Penalty sesuai spec
                failedOrders++;
                iterator.remove(); // Hapus order
                reindexOrders();
            }
        }
    }

    private void spawnOrder() {
        if (levelRecipes.isEmpty()) return;

        // Cegah spawn terlalu cepat (opsional logic, di sini simple saja)
        spawnTimer++;
        if (spawnTimer < 100) return; // Delay spawn
        spawnTimer = 0;

        // Pilih resep random
        Random rand = new Random();
        int index = rand.nextInt(levelRecipes.size());
        Recipe selectedRecipe = levelRecipes.get(index);

        // Buat Order baru di posisi terakhir
        int newId = activeOrders.size();
        Order newOrder = new Order(newId, selectedRecipe, gp.FPS);

        activeOrders.add(newOrder);
        System.out.println("New Order: " + selectedRecipe.name);
    }

    // Mengurutkan ulang ID agar order bergeser ke kiri saat ada yang selesai
    private void reindexOrders() {
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).id = i;
        }
    }

    // --- LOGIKA VALIDASI ORDER (SAAT SERVING) ---
    // Dipanggil saat Player menaruh piring di Serving Counter
    public boolean checkServing(Item servedItem) {

        if (servedItem == null) return false;

        ArrayList<String> plateContents = new ArrayList<>();

        // 1. Validasi apakah item adalah Piring (Plate)
        if (servedItem instanceof Plate) {
            Plate plate = (Plate) servedItem;
            // Ambil semua bahan di atas piring
            for (environment.Preparable p : plate.itemOnPlate) {
                // Casting ke Ingredient untuk ambil nama & state
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    // Format: "nama_STATE" (contoh: "fish_RAW")
                    plateContents.add(ing.name + "_" + ing.state);
                }
            }
        }
        else {
            // Jika bukan piring, serving gagal (tidak bisa serve panci langsung)
            return false;
        }

        // 2. Cek Order (FIFO)
        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);

            // Bandingkan isi piring dengan resep order
            if (isRecipeMatch(order.recipe, plateContents)) {
                // MATCH FOUND!
                System.out.println("Order Selesai: " + order.recipe.name);

                // Beri Reward
                score += order.recipe.reward;

                // Hapus Order
                activeOrders.remove(i);

                // Geser urutan visual
                reindexOrders();

                return true; // Sukses serve
            }
        }

        // Jika sampai sini, berarti tidak ada order yang cocok (Penalty)
        System.out.println("Makanan Salah! Penalti -50");
        score -= 50;
        return false;
    }

    // Helper untuk membandingkan isi piring vs resep
    private boolean isRecipeMatch(Recipe recipe, ArrayList<String> plateContents) {
        // Jumlah bahan harus sama
        if (recipe.requiredIngredients.size() != plateContents.size()) {
            return false;
        }

        // Gunakan copy list agar aman
        ArrayList<String> tempPlate = new ArrayList<>(plateContents);

        // Cek setiap bahan yang dibutuhkan resep
        for (String req : recipe.requiredIngredients) {
            if (tempPlate.contains(req)) {
                tempPlate.remove(req); // Hapus biar kalau ada bahan double terhandle
            } else {
                return false; // Bahan kurang atau salah state
            }
        }

        return true;
    }

    public void draw(Graphics2D g2) {
        // Gambar UI Order di bagian atas layar
        for (Order order : activeOrders) {
            order.draw(g2, 20, 10);
        }

        // Gambar Score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + score, gp.screenWidth - 150, 30);
    }
}