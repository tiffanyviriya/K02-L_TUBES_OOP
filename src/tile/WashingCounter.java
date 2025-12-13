package tile;

import environment.entity.Entity;
import environment.item.Item;
import main.util.GamePanel;
import main.util.ItemContainer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class WashingCounter extends Tile {

    private ItemContainer<Item> cleanStack = new ItemContainer<>();

    /* Konstruktor untuk inisialisasi counter pencucian */
    public WashingCounter(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadImage();
    }

    /* Memuat gambar visual untuk counter pencucian */
    private void loadImage() {
        try {
            var is = getClass().getResourceAsStream("/stations/washingcounter.png");
            if (is != null) image = ImageIO.read(is);
        } catch (IOException e) {  }
    }

    /* Menambahkan piring bersih ke dalam tumpukan di counter */
    public synchronized void addCleanPlate(Item item) {
        cleanStack.addItem(item);
    }

    /* Menangani interaksi pemain mengambil piring bersih dari counter */
    @Override
    public void interact(Entity player) {
        if (player.inventory == null && !cleanStack.isEmpty()) {
            player.inventory = cleanStack.takeItem();
        }
    }

    /* Menggambar counter dan tumpukan piring bersih */
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }

        if (!cleanStack.isEmpty()) {
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