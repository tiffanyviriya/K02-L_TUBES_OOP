package tile;

import environment.Entity;
import environment.Item;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Stack;

public class WashingCounter extends Tile {

    private Stack<Item> cleanStack = new Stack<>();

    public WashingCounter(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadImage();
    }

    private void loadImage() {
        try {
            var is = getClass().getResourceAsStream("/tiles/OOPTile.png"); //ganti
            if (is != null) image = ImageIO.read(is);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Method ini akan dipanggil oleh WashingStation (Tetangga)
    public synchronized void addCleanPlate(Item item) {
        cleanStack.push(item);
    }

    @Override
    public void interact(Entity player) {
        // Player hanya bisa MENGAMBIL dari sini
        if (player.inventory == null && !cleanStack.isEmpty()) {
            player.inventory = cleanStack.pop();
            System.out.println("Player mengambil piring bersih.");
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }

        // Visualisasi Tumpukan Piring Bersih
        if (!cleanStack.isEmpty()) {
            g2.drawImage(cleanStack.peek().image, x + 12, y + 10, gp.itemSize, gp.itemSize, null);

            if (cleanStack.size() > 1) {
                g2.setColor(Color.BLUE);
                g2.fillOval(x + 30, y + 30, 15, 15);
                g2.setColor(Color.WHITE);
                g2.drawString(String.valueOf(cleanStack.size()), x + 34, y + 42);
            }
        }
    }
}