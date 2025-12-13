package environment.food_related;

import java.awt.*;

public class Order {
    public int id;
    public Recipe recipe;
    public int currentTimer;
    public int maxTimer;
    public boolean isExpired = false;

    private final int width = 60;
    private final int height = 80;

    /* Konstruktor untuk inisialisasi pesanan dengan ID, resep, dan batas waktu berdasarkan FPS */
    public Order(int id, Recipe recipe, int fps) {
        this.id = id;
        this.recipe = recipe;
        this.maxTimer = recipe.timeLimit * fps;
        this.currentTimer = maxTimer;
    }

    /* Memperbarui timer pesanan setiap frame dan menandai jika sudah kedaluwarsa */
    public void update() {
        if (currentTimer > 0) {
            currentTimer--;
        } else {
            isExpired = true;
        }
    }

    /* Menggambar visualisasi pesanan termasuk gambar makanan dan bar waktu */
    public void draw(Graphics2D g2, int startX, int startY) {
        int x = startX + (id * (width + 10));
        int y = startY;

        g2.setColor(new Color(240, 240, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        if (recipe.image != null) {
            g2.drawImage(recipe.image, x + 10, y + 10, 40, 40, null);
        } else {
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString(recipe.name, x + 5, y + 30);
        }

        int barHeight = 5;
        float percentage = (float) currentTimer / maxTimer;

        if (percentage > 0.5) g2.setColor(Color.GREEN);
        else if (percentage > 0.2) g2.setColor(Color.ORANGE);
        else g2.setColor(Color.RED);

        int barWidth = (int) ((width - 10) * percentage);
        g2.fillRect(x + 5, y + height - 10, barWidth, barHeight);
    }
}