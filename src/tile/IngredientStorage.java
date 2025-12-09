package tile;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import environment.Entity;
import environment.Ingredient;
import environment.Item;

public class IngredientStorage extends Tile {

    GamePanel gp;
    String ingredientName;

    public Item itemOnTop = null;

    public IngredientStorage(GamePanel gp, String ingredientName) {
        super(gp);
        this.gp = gp;
        this.ingredientName = ingredientName;

        this.collision = true;

        loadStorageImage();
    }

    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/OOPtile.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interact(Entity player) {

        if (itemOnTop != null) {
            if (player.inventory == null) {
                player.inventory = itemOnTop;
                itemOnTop = null;
                System.out.println("Player mengambil " + player.inventory.name + " dari atas storage.");
            } else {
                System.out.println("Tangan penuh! Tidak bisa mengambil item.");
            }
        }
        else {
            if (player.inventory == null) {
                player.inventory = new Ingredient(gp, ingredientName);
            } else {
                itemOnTop = player.inventory;
                player.inventory = null;
                //debug
                System.out.println("Player menaruh " + itemOnTop.name + " di atas storage " + ingredientName);
            }
        }
    }
}