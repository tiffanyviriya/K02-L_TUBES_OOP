package environment.food_related;

import java.awt.*;

public class Order {
    public int id; // Posisi Order (0, 1, 2, dst)
    public Recipe recipe;
    public int currentTimer; // Dalam frame/tick (detik * FPS)
    public int maxTimer;
    public boolean isExpired = false;

    // Untuk visualisasi
    private final int width = 60;
    private final int height = 80;

    public Order(int id, Recipe recipe, int fps) {
        this.id = id;
        this.recipe = recipe;
        // Konversi detik ke tick game (misal 60 detik * 60 FPS = 3600 tick)
        this.maxTimer = recipe.timeLimit * fps;
        this.currentTimer = maxTimer;
    }

    public void update() {
        if (currentTimer > 0) {
            currentTimer--;
        } else {
            isExpired = true;
        }
    }

    public void draw(Graphics2D g2, int startX, int startY) {
        int x = startX + (id * (width + 10));
        int y = startY;

        // 1. Gambar Background Kertas (Opsional, biar kelihatan ala struk)
        g2.setColor(new Color(240, 240, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        // 2. GAMBAR SPRITE MAKANAN (PENTING)
        if (recipe.image != null) {
            // Gambar sprite di tengah kertas order
            // Ukuran disesuaikan (misal 32x32 atau 40x40) agar muat di kertas
            g2.drawImage(recipe.image, x + 10, y + 10, 40, 40, null);
        } else {
            // Backup jika gambar tidak ada (tulis nama saja)
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString(recipe.name, x + 5, y + 30);
        }

        // 3. Gambar Timer Bar (Tetap sama)
        int barHeight = 5;
        float percentage = (float) currentTimer / maxTimer;

        if (percentage > 0.5) g2.setColor(Color.GREEN);
        else if (percentage > 0.2) g2.setColor(Color.ORANGE);
        else g2.setColor(Color.RED);

        int barWidth = (int) ((width - 10) * percentage);
        // Posisi bar di bawah gambar
        g2.fillRect(x + 5, y + height - 10, barWidth, barHeight);
    }
}