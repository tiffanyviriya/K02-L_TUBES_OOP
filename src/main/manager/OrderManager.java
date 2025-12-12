package main.manager;

import environment.food_related.Dish;
import environment.food_related.IngredientState;
import environment.food_related.Order;
import environment.food_related.Recipe;
import environment.item.*;
import main.util.GamePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

public class OrderManager {
    protected GamePanel gp;

    public ArrayList<Order> activeOrders = new ArrayList<>();
    public ArrayList<Recipe> levelRecipes = new ArrayList<>();

    private final int MAX_ORDERS = 5;
    public int score = 0;
    public int failedOrders = 0;
    private int spawnTimer = 0;

    public OrderManager(GamePanel gp) {
        this.gp = gp;
        setupRecipes();
    }

    // Method reset untuk memulai sesi baru
    public void reset() {
        activeOrders.clear();
        score = 0;
        failedOrders = 0;
        spawnTimer = 0;
    }

    private void setupRecipes() {
        // Setup resep (Kode sama seperti sebelumnya)
        Recipe kappaMaki = new Recipe("Kappa Maki", 120, 60);
        kappaMaki.addIngredient("nori", IngredientState.RAW);
        kappaMaki.addIngredient("rice", IngredientState.COOKED);
        kappaMaki.addIngredient("cucumber", IngredientState.CHOPPED);

        Recipe sakanaMaki = new Recipe("Sakana Maki", 150, 60);
        sakanaMaki.addIngredient("nori", IngredientState.RAW);
        sakanaMaki.addIngredient("rice", IngredientState.COOKED);
        sakanaMaki.addIngredient("fish", IngredientState.CHOPPED);

        Recipe fishcucumberRoll = new Recipe("Fish Cucumber Roll", 150, 60);
        fishcucumberRoll.addIngredient("nori", IngredientState.RAW);
        fishcucumberRoll.addIngredient("rice", IngredientState.COOKED);
        fishcucumberRoll.addIngredient("fish", IngredientState.CHOPPED);
        fishcucumberRoll.addIngredient("cucumber", IngredientState.CHOPPED);

        Recipe ebiMaki = new Recipe("Ebi Maki", 150, 60);
        ebiMaki.addIngredient("nori", IngredientState.RAW);
        ebiMaki.addIngredient("rice", IngredientState.COOKED);
        ebiMaki.addIngredient("shrimp", IngredientState.COOKED);

        levelRecipes.add(kappaMaki);
        levelRecipes.add(sakanaMaki);
        levelRecipes.add(ebiMaki);
        levelRecipes.add(fishcucumberRoll);
    }

    public void update() {
        if (activeOrders.size() < MAX_ORDERS) {
            spawnOrder();
        }

        Iterator<Order> iterator = activeOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            order.update();

            if (order.isExpired) {
                System.out.println("Order " + order.recipe.name + " GAGAL! (Waktu Habis)");
                score -= 50;
                failedOrders++;
                iterator.remove();
                reindexOrders();
            }
        }
    }

    private void spawnOrder() {
        if (levelRecipes.isEmpty()) return;
        spawnTimer++;
        if (spawnTimer < 200) return; // Delay spawn agak lamaan dikit
        spawnTimer = 0;

        Random rand = new Random();
        int index = rand.nextInt(levelRecipes.size());
        Recipe selectedRecipe = levelRecipes.get(index);

        int newId = activeOrders.size();
        Order newOrder = new Order(newId, selectedRecipe, gp.FPS);
        activeOrders.add(newOrder);
        System.out.println("New Order: " + selectedRecipe.name);
    }

    private void reindexOrders() {
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).id = i;
        }
    }

    // --- LOGIKA VALIDASI ORDER ---
    public void checkServing(ArrayList<String> plateIngredients) {
        if (plateIngredients.isEmpty()) return;

        boolean matchFound = false;

        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);

            if (isRecipeMatch(order.recipe, plateIngredients)) {
                System.out.println("Order Selesai: " + order.recipe.name);
                score += order.recipe.reward;
                gp.soundM.playSE(3);

                activeOrders.remove(i);
                reindexOrders();
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            System.out.println("Makanan Salah! Penalti -50.");
            score -= 50;
            gp.soundM.playSE(6);
        }
    }

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

    public void draw(Graphics2D g2) {
        for (Order order : activeOrders) {
            order.draw(g2, 20, 10);
        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + score, gp.screenWidth - 150, 30);
    }
}