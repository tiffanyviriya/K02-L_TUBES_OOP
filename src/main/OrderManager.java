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

    // Timer spawn (agar tidak spawn instan di detik ke-0 jika mau delay)
    private int spawnTimer = 0;

    public OrderManager(GamePanel gp) {
        this.gp = gp;
        setupRecipes(); // Load resep sesuai level
    }

    // Definisikan resep-resep di sini (Sesuaikan dengan Map Type)
    private void setupRecipes() {
        // CONTOH: Setup resep untuk Map Type A (Sushi)
        // 1. Kappa Maki: Nori (Raw) + Nasi (Cooked) + Timun (Chopped)
        Recipe kappaMaki = new Recipe("Kappa Maki", 120, 60);
        kappaMaki.addIngredient("nori", IngredientState.RAW);
        kappaMaki.addIngredient("rice", IngredientState.COOKED);
        kappaMaki.addIngredient("cucumber", IngredientState.CHOPPED);

        // 2. Sakana Maki: Nori + Nasi + Ikan
        Recipe sakanaMaki = new Recipe("Sakana Maki", 150, 60);
        sakanaMaki.addIngredient("nori", IngredientState.RAW);
        sakanaMaki.addIngredient("rice", IngredientState.COOKED);
        sakanaMaki.addIngredient("fish", IngredientState.RAW);

        // Masukkan ke daftar kemungkinan resep
        levelRecipes.add(kappaMaki);
        levelRecipes.add(sakanaMaki);

        // Tambahkan resep lain sesuai spec...
    }

    public void update() {
        // 1. Spawn Order Baru jika slot kosong
        if (activeOrders.size() < MAX_ORDERS) {
            // Bisa tambah delay spawnTimer di sini jika ingin ada jeda antar order
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

                // Re-index ID order agar visualnya geser rapi (Opsional)
                reindexOrders();
            }
        }
    }

    private void spawnOrder() {
        if (levelRecipes.isEmpty()) return;

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

        // 1. Validasi apakah item adalah Piring/Dish yang valid
        // (Asumsi: Anda punya class Dish atau Item container yang punya list ingredients)
        // Di sini saya asumsikan servedItem punya method/list getIngredients()

        if (servedItem == null) return false;

        // Konversi isi piring player menjadi List String untuk dibandingkan
        // Contoh: ["nori_RAW", "rice_COOKED", "fish_RAW"]
        ArrayList<String> plateContents = new ArrayList<>();

        // TODO: Sesuaikan dengan struktur class Item/Dish Anda.
        // Misal item tersebut adalah Plate yang punya list ingredients:
        if (servedItem instanceof KitchenUtensil) { // Atau class Plate
            KitchenUtensil plate = (KitchenUtensil) servedItem;
            for (Ingredient ing : plate.ingredients) {
                plateContents.add(ing.name + "_" + ing.state);
            }
        } else {
            return false; // Bukan makanan valid
        }

        // 2. Cek Order (FIFO - Dari index 0 ke belakang)
        // Spec: "Jika ada dua order yang sama, selesaikan yang paling awal masuk."
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

        // Jika sampai sini, berarti makanan salah (Penalty)
        System.out.println("Makanan Salah! Penalti -50");
        score -= 50;
        return false;
    }

    // Helper untuk membandingkan isi piring vs resep
    private boolean isRecipeMatch(Recipe recipe, ArrayList<String> plateContents) {
        if (recipe.requiredIngredients.size() != plateContents.size()) {
            return false;
        }

        // Cek apakah semua bahan resep ada di piring
        // Kita gunakan copy list agar tidak merusak data asli saat remove
        ArrayList<String> tempPlate = new ArrayList<>(plateContents);

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
            order.draw(g2, 20, 10); // Koordinat X: 20, Y: 10
        }

        // Gambar Score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + score, gp.screenWidth - 150, 30);
    }
}