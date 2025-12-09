package tile;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

import environment.*;
import main.GamePanel;

public class AssemblyStation extends Tile {

    public Item itemOnTop = null;

    public AssemblyStation(GamePanel gp) {
        super(gp);

        this.collision = true;
        loadStorageImage();
    }

    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/table.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interact(Entity player) {

        if (itemOnTop != null) {

            if (itemOnTop instanceof Plate && player.inventory != null && player.inventory instanceof Preparable) {
                ((Plate) itemOnTop).addItem((Preparable) player.inventory);
                player.inventory = null;
                System.out.println("Player menaruh bahan ke dalam piring di meja assembly.");
            }
            else if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari meja assembly.");
            }
            else {
                System.out.println("Tangan penuh! Tidak bisa menukar item saat ini.");
            }
        }
        else {
            if (player.inventory == null) {
                System.out.println("Meja kosong.");
            } else {
                itemOnTop = player.inventory;
                player.inventory = null;
                System.out.println("Player menaruh " + itemOnTop.name + " di meja assembly.");
            }
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        super.draw(g2, x, y);

        if(itemOnTop != null){
            int offset = 8;
            int size = gp.tileSize - (offset * 2);

            itemOnTop.draw(g2, x + offset, y + offset);
        }
    }
}