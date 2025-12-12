package tile;

import environment.entity.Entity;
import environment.item.Item;
import main.util.GamePanel;
import main.util.ItemContainer; // Import baru

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class WashingCounter extends Tile {

    // [CUSTOM GENERICS] Menggunakan ItemContainer<Item> untuk tumpukan bersih
    private ItemContainer<Item> cleanStack = new ItemContainer<>();

    public WashingCounter(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadImage();
    }

    private void loadImage() {
        try {
            var is = getClass().getResourceAsStream("/stations/washingcounter.png");
            if (is != null) image = ImageIO.read(is);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void addCleanPlate(Item item) {
        // [CUSTOM GENERICS] Add item
        cleanStack.addItem(item);
    }

    @Override
    public void interact(Entity player) {
        // Player hanya bisa MENGAMBIL dari sini
        if (player.inventory == null && !cleanStack.isEmpty()) {
            // [CUSTOM GENERICS] Take item
            player.inventory = cleanStack.takeItem();
            System.out.println("Player mengambil piring bersih.");
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }

        if (!cleanStack.isEmpty()) {
            // [CUSTOM GENERICS] Peek item
            Item topItem = cleanStack.peekItem();

            if (topItem != null && topItem.image != null) {
                g2.drawImage(topItem.image, x + 12, y + 10, gp.itemSize, gp.itemSize, null);
            }

            if (cleanStack.size() > 1) {
                g2.setColor(Color.BLUE);
                g2.fillOval(x + 30, y + 30, 15, 15);
                g2.setColor(Color.WHITE);
                g2.drawString(String.valueOf(cleanStack.size()), x + 34, y + 42);
            }
        }
    }
}