package tile;

import environment.entity.Entity;
import environment.entity.Player;
import environment.entity.PlayerState;
import environment.food_related.Ingredient;
import environment.food_related.IngredientState;
import environment.item.Item;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class CuttingStation extends Tile {

    public Item itemOnTop = null;
    private Entity activePlayer = null;
    private int currentProgress = 0;
    private final int TIME_TO_CUT = 180;

    /* Konstruktor untuk inisialisasi stasiun pemotong dan memuat gambarnya */
    public CuttingStation(GamePanel gp) {
        super(gp);
        this.collision = true;
        loadStationImage();
    }

    /* Memuat gambar visual untuk stasiun pemotong */
    private void loadStationImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cutting_station.png"));
        } catch (IOException e) {  }
    }

    /* Menangani interaksi pemain: menaruh bahan, memulai/menghentikan pemotongan, atau mengambil hasil */
    @Override
    public void interact(Entity player) {
        if (itemOnTop == null && player.inventory != null) {
            if (player.inventory instanceof Ingredient) {
                Ingredient ing = (Ingredient) player.inventory;
                boolean isCuttable = ing.name.equalsIgnoreCase("shrimp") ||
                        ing.name.equalsIgnoreCase("fish") ||
                        ing.name.equalsIgnoreCase("cucumber");
                if (!isCuttable) {
                    gp.soundM.playSE(6);
                    return;
                }
            } else {
                gp.soundM.playSE(6);
                return;
            }

            itemOnTop = player.inventory;
            player.inventory = null;
            return;
        }

        if (itemOnTop != null) {
            boolean isRaw = false;
            if (itemOnTop instanceof Ingredient) {
                if (((Ingredient) itemOnTop).state == IngredientState.RAW) isRaw = true;
            }

            if (isRaw && player.inventory == null) {
                if (activePlayer != null && activePlayer != player) {
                    return;
                }

                if (activePlayer == null) {
                    startCutting(player);
                } else {
                    stopCutting();
                }
            }

            else if (player.inventory == null) {
                if (activePlayer != null && activePlayer != player) return;

                stopCutting();
                player.inventory = itemOnTop;
                itemOnTop = null;
                currentProgress = 0;
            }
        }
    }

    /* Memperbarui progress pemotongan jika ada pemain yang aktif */
    public void update() {
        if (activePlayer != null && itemOnTop != null) {
            if (activePlayer instanceof Player) {
                ((Player) activePlayer).playerState = PlayerState.BUSY;
            }

            currentProgress++;

            if (currentProgress >= TIME_TO_CUT) {
                finishCutting();
            }
        }
        else if (activePlayer != null && itemOnTop == null) {
            stopCutting();
        }
    }

    /* Memulai proses pemotongan dan mengunci status pemain menjadi sibuk */
    private void startCutting(Entity player) {
        activePlayer = player;
        if (player instanceof Player) ((Player) player).playerState = PlayerState.BUSY;

        gp.soundM.playSELoop(9);
    }

    /* Menghentikan proses pemotongan dan mengembalikan status pemain menjadi idle */
    private void stopCutting() {
        if (activePlayer instanceof Player) {
            ((Player) activePlayer).playerState = PlayerState.IDLE;
        }
        activePlayer = null;

        gp.soundM.stopSELoop();
    }

    /* Menyelesaikan proses pemotongan dan mengubah status bahan */
    private void finishCutting() {
        if (itemOnTop instanceof Ingredient) {
            ((Ingredient) itemOnTop).chop();
        }
        currentProgress = 0;
        stopCutting();
    }

    /* Menggambar stasiun, item di atasnya, dan progress bar pemotongan */
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        if (itemOnTop != null) {
            g2.drawImage(itemOnTop.image, x + 12, y + 12, gp.itemSize, gp.itemSize, null);
            if (currentProgress > 0) {
                int barWidth = 32;
                int screenX = x + 8;
                int screenY = y - 10;
                g2.setColor(Color.RED); g2.fillRect(screenX, screenY, barWidth, 6);
                int greenBar = (int) (((double)currentProgress / TIME_TO_CUT) * barWidth);
                g2.setColor(Color.GREEN); g2.fillRect(screenX, screenY, greenBar, 6);
                g2.setColor(Color.BLACK); g2.drawRect(screenX, screenY, barWidth, 6);
            }
        }
    }
}