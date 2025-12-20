package main.manager;

import environment.food_related.Dish;
import environment.food_related.IngredientState;
import environment.food_related.Order;
import environment.food_related.Recipe;
import environment.item.*;
import main.util.GamePanel;
import main.util.GameState;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;
import java.util.List;

/* Manajer untuk mengatur pesanan, resep, skor, dan kondisi permainan (nyawa/game over) */
public class OrderManager {
    protected GamePanel gp;

    public ArrayList<Order> activeOrders = new ArrayList<>();
    public ArrayList<Recipe> levelRecipes = new ArrayList<>();

    private final int MAX_ORDERS = 5;
    public int score = 0;
    public int failedOrders = 0;
    private int spawnTimer = 0;

    public int lives = 5;
    private final int LIVES_PENALTY = 1;

    /* Menginisialisasi manajer pesanan dan memuat resep level */
    public OrderManager(GamePanel gp) {
        this.gp = gp;
        setupRecipes();
    }

    /* Mengatur ulang semua status permainan untuk sesi baru */
    public void reset() {
        activeOrders.clear();
        score = 0;
        failedOrders = 0;
        spawnTimer = 0;
        lives = 5;
    }

    /* Mendefinisikan daftar resep yang tersedia untuk level ini */
    private void setupRecipes() {
        Recipe kappaMaki = new Recipe("Kappa Maki", 500, 60);
        kappaMaki.addIngredient("nori", IngredientState.RAW);
        kappaMaki.addIngredient("rice", IngredientState.COOKED);
        kappaMaki.addIngredient("cucumber", IngredientState.CHOPPED);

        Recipe sakanaMaki = new Recipe("Sakana Maki", 500, 60);
        sakanaMaki.addIngredient("nori", IngredientState.RAW);
        sakanaMaki.addIngredient("rice", IngredientState.COOKED);
        sakanaMaki.addIngredient("fish", IngredientState.CHOPPED);

        Recipe fishcucumberRoll = new Recipe("Fish Cucumber Roll", 500, 60);
        // SINKRONISASI RECIPE: Menggunakan addIngredient agar format string konsisten (name_STATE)
        fishcucumberRoll.addIngredient("nori", IngredientState.RAW);
        fishcucumberRoll.addIngredient("rice", IngredientState.COOKED);
        fishcucumberRoll.addIngredient("fish", IngredientState.CHOPPED);
        fishcucumberRoll.addIngredient("cucumber", IngredientState.CHOPPED);

        Recipe ebiMaki = new Recipe("Ebi Maki", 500, 60);
        ebiMaki.addIngredient("nori", IngredientState.RAW);
        ebiMaki.addIngredient("rice", IngredientState.COOKED);
        ebiMaki.addIngredient("shrimp", IngredientState.COOKED);

        levelRecipes.add(kappaMaki);
        levelRecipes.add(sakanaMaki);
        levelRecipes.add(ebiMaki);
        levelRecipes.add(fishcucumberRoll);
    }

    /* Memperbarui logika pesanan, spawning, dan pengecekan kedaluwarsa */
    public void update() {
        // SINKRONISASI SERVER: Spawning lokal dinonaktifkan agar tidak desinkron antar client
        /*
        if (activeOrders.size() < MAX_ORDERS) {
            spawnOrder();
        }
        */

        Iterator<Order> iterator = activeOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            order.update();

            // SINKRONISASI SERVER: Pengecekan expired lokal tetap ada untuk visual,
            // tapi idealnya server yang mengirim sinyal ORDER_REMOVE jika durasi habis.
            if (order.isExpired) {
                score -= 50;
                failedOrders++;
                lives -= LIVES_PENALTY;
                iterator.remove();
                reindexOrders();
                checkGameOver();
            }
        }
    }

    // SINKRONISASI SERVER: Fungsi penambah order berdasarkan instruksi server
    public void applyOrderSpawn(String recipeName, int duration) {
        Recipe selectedRecipe = null;
        for (Recipe r : levelRecipes) {
            if (r.name.equalsIgnoreCase(recipeName)) {
                selectedRecipe = r;
                break;
            }
        }

        if (selectedRecipe != null && activeOrders.size() < MAX_ORDERS) {
            int newId = activeOrders.size();
            Order newOrder = new Order(newId, selectedRecipe, gp.FPS);
            // Anda bisa mengatur durasi spesifik dari server jika perlu:
            // newOrder.timeLeft = duration;
            activeOrders.add(newOrder);
        }
    }

    // SINKRONISASI SERVER: Fungsi penghapus order berdasarkan index (instruksi server)
    public void applyOrderRemove(int index) {
        if (index >= 0 && index < activeOrders.size()) {
            activeOrders.remove(index);
            reindexOrders();
        }
    }

    /* Spawning lokal dinonaktifkan untuk mode multiplayer */
    private void spawnOrder() {
        // Method ini sekarang tidak dipanggil di update() jika multiplayer aktif
        if (levelRecipes.isEmpty()) return;
        spawnTimer++;
        if (spawnTimer < 200) return;
        spawnTimer = 0;

        Random rand = new Random();
        int index = rand.nextInt(levelRecipes.size());
        Recipe selectedRecipe = levelRecipes.get(index);

        int newId = activeOrders.size();
        Order newOrder = new Order(newId, selectedRecipe, gp.FPS);
        activeOrders.add(newOrder);
    }

    /* Mengatur ulang ID pesanan agar berurutan sesuai posisi dalam list */
    private void reindexOrders() {
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).id = i;
        }
    }

    /* Memeriksa apakah bahan di piring cocok dengan salah satu pesanan aktif */
    public void checkServing(ArrayList<String> plateIngredients) {
        if (plateIngredients.isEmpty()) return;

        boolean matchFound = false;

        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);

            if (isRecipeMatch(order.recipe, plateIngredients)) {
                score += order.recipe.reward;
                gp.soundM.playSE(3);

                activeOrders.remove(i);
                reindexOrders();
                matchFound = true;
                // SINKRONISASI SERVER: Di sini client harus mengirim pesan ke server
                // bahwa order index 'i' telah selesai agar server bisa membroadcast ke player lain.
                break;
            }
        }

        if (!matchFound) {
            score -= 50;
            gp.soundM.playSE(6);
            lives -= LIVES_PENALTY;
            failedOrders++;

            checkGameOver();
        }
    }

    /* Membandingkan isi piring dengan kebutuhan bahan resep */
    private boolean isRecipeMatch(Recipe recipe, ArrayList<String> plateContents) {
        if (recipe.requiredIngredients.size() != plateContents.size()) return false;
        ArrayList<String> tempPlate = new ArrayList<>(plateContents);
        for (String req : recipe.requiredIngredients) {
            if (tempPlate.contains(req)) {
                tempPlate.remove(req);
            } else {
                return false;
            }
        }
        return true;
    }

    /* Mengecek kondisi kekalahan jika nyawa habis */
    private void checkGameOver() {
        if (lives <= 0) {
            gp.changeGameState(GameState.RESULT);
        }
    }

    /* Menggambar antarmuka pesanan, skor, dan nyawa ke layar */
    public void draw(Graphics2D g2) {
        for (Order order : activeOrders) {
            order.draw(g2, 20, 10);
        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + score, gp.screenWidth - 150, 30);
        drawLives(g2);
    }

    /* Menggambar indikator visual nyawa pemain */
    private void drawLives(Graphics2D g2) {
        int startX = 20;
        int startY = gp.screenHeight - 20;

        g2.setColor(Color.WHITE);
        g2.drawString("Lives:", startX, startY);

        int MAX_LIVES = 5;
        for (int i = 0; i < MAX_LIVES; i++) {
            int x = startX + 60 + (i * 25);
            int y = startY - 15;

            if (i < lives) {
                g2.setColor(Color.RED);
                g2.fillOval(x, y, 20, 20);
            } else {
                g2.setColor(Color.GRAY);
                g2.drawOval(x, y, 20, 20);
            }
        }
    }

    public List<Order> getActiveOrders() { return activeOrders; }
}