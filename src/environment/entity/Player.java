package environment.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import environment.food_related.Ingredient;
import environment.food_related.IngredientState;
import environment.item.Item;
import main.util.GamePanel;
import main.handler.KeyHandler;
import tile.*;

public class Player extends Entity {

    KeyHandler keyH;
    public PlayerState playerState;

    // --- DASH VARIABLES ---
    private int dashCooldown = 0;
    private final int DASH_COOLDOWN_MAX = 120; // 2 detik (60 FPS)
    private final int DASH_DISTANCE = 3; // Jarak dash dalam tile

    public Player(GamePanel gp, KeyHandler keyH, int posX, int posY) {
        super(gp);

        this.gp = gp;
        this.keyH = keyH;

        pos.x = posX;
        pos.y = posY;

        setDefaultValue();
        getPlayerImage();

        solidArea = new Rectangle(0, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void setDefaultValue() {
        speed = 4;
        direction = "down";
        playerState = PlayerState.IDLE;
    }

    public void getPlayerImage() {
        try {
            up0 = ImageIO.read(getClass().getResourceAsStream("/player/north_0.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/north_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/north_2.png"));

            down0 = ImageIO.read(getClass().getResourceAsStream("/player/south_0.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/south_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/south_2.png"));

            left0 = ImageIO.read(getClass().getResourceAsStream("/player/west_0.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/west_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/west_2.png"));

            right0 = ImageIO.read(getClass().getResourceAsStream("/player/east_0.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/east_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/east_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {

        // 1. PRIORITAS UTAMA: SWITCH CHEF
        // Ini ditaruh paling atas supaya bisa ganti chef KAPANPUN,
        // bahkan saat chef sedang BUSY memotong.
        if (keyH.switchPressed) {
            gp.playerM.switchPlayer();
            keyH.switchPressed = false; // Reset input
            return; // Keluar agar tidak memproses movement di frame ini
        }

        // 2. CEK TOMBOL INTERAKSI (V - Action)
        // Kita cek ini SEBELUM cek BUSY, supaya pemain bisa menekan V
        // untuk membatalkan/stop proses memotong.
        if (keyH.actionPressed) {
            interactWithStation();
            keyH.actionPressed = false; // [PENTING] Reset agar tidak terbaca double
        }

        // 3. CEK STATUS BUSY
        // Jika sedang sibuk (memotong/mencuci), stop di sini.
        // Player tidak boleh bergerak, tapi progress bar di station tetap jalan (diurus Station).
        if (playerState == PlayerState.BUSY) {
            return;
        }

        // 4. LOGIKA PERGERAKAN (Hanya jalan jika IDLE)
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> pos.y -= speed;
                    case "down" -> pos.y += speed;
                    case "left" -> pos.x -= speed;
                    case "right" -> pos.x += speed;
                }
            }

            spriteCounter++;
            if(spriteCounter > 12) {
                if(spriteNum == 1) spriteNum = 2;
                else if(spriteNum == 2) spriteNum = 3;
                else if(spriteNum == 3) spriteNum = 1;
                spriteCounter = 0;
            }
        }

        // 5. TOMBOL PICKUP/DROP (C - Interact)
        // Hanya bisa dilakukan jika IDLE (tangan bebas/tidak sedang masak)
        if (keyH.interactPressed) {
            interactWithFloor();
            keyH.interactPressed = false;
        }
    }

    // --- LOGIKA DASH (Refactor: Gunakan cChecker) ---
    private void performDash() {
        if (dashCooldown > 0) return;

        // Loop per tile agar tidak tembus tembok tebal
        for (int i = 0; i < DASH_DISTANCE * gp.tileSize; i += gp.tileSize) {
            // Prediksi posisi maju 1 langkah tile
            int tempX = pos.x;
            int tempY = pos.y;

            switch (direction) {
                case "up" -> pos.y -= gp.tileSize;
                case "down" -> pos.y += gp.tileSize;
                case "left" -> pos.x -= gp.tileSize;
                case "right" -> pos.x += gp.tileSize;
            }

            // Cek kolisi di posisi baru
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkPlayer(this); // Cek nabrak teman juga saat dash

            // Jika nabrak, kembalikan ke posisi sebelumnya dan berhenti
            if (collisionOn) {
                pos.x = tempX;
                pos.y = tempY;
                break;
            }
        }

        System.out.println("DASH!");
        dashCooldown = DASH_COOLDOWN_MAX;
    }

    // --- LOGIKA LEMPAR ---
    private void performThrow() {
        if (inventory == null) return;

        if (inventory instanceof Ingredient) {
            Ingredient ing = (Ingredient) inventory;
            if (ing.state != IngredientState.RAW) {
                System.out.println("Gagal Lempar: Bahan sudah diproses!");
                return;
            }
        } else {
            System.out.println("Gagal Lempar: Hanya bahan mentah yang bisa dilempar!");
            return;
        }

        int throwDist = 3;
        int targetX = pos.x;
        int targetY = pos.y;

        // Simulasi lintasan lemparan
        for (int i = 1; i <= throwDist; i++) {
            int checkX = pos.x;
            int checkY = pos.y;

            switch (direction) {
                case "up" -> checkY -= (i * gp.tileSize);
                case "down" -> checkY += (i * gp.tileSize);
                case "left" -> checkX -= (i * gp.tileSize);
                case "right" -> checkX += (i * gp.tileSize);
            }

            int col = (checkX + gp.tileSize/2) / gp.tileSize;
            int row = (checkY + gp.tileSize/2) / gp.tileSize;

            if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {
                Tile t = gp.tileM.worldTiles[col][row];
                if (t.collision) {
                    // Nabrak tembok -> jatuh di tile sebelumnya
                    break;
                } else {
                    targetX = checkX;
                    targetY = checkY;
                }
            }
        }

        // Cek apakah ada player lain di target untuk "Catch"
        Player[] players = gp.playerM.getPlayers();
        for (Player other : players) {
            if (other != this && other != null) {
                // Cek jarak sederhana
                int dist = (int) Math.sqrt(Math.pow(other.pos.x - targetX, 2) + Math.pow(other.pos.y - targetY, 2));

                // Jika lemparan mendarat dekat player lain (toleransi 1 tile)
                if (dist < gp.tileSize) {
                    if (other.inventory == null) {
                        other.inventory = this.inventory;
                        this.inventory = null;
                        System.out.println("CATCH! Ditangkap oleh " + other);
                        return;
                    }
                }
            }
        }

        // Jika tidak ditangkap, jatuh ke lantai
        gp.itemM.addItem(inventory, targetX + gp.itemSize/2, targetY + gp.itemSize/2);
        System.out.println("Melempar " + inventory.name + "!");
        inventory = null;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch(direction) {
            case "up" -> image = (spriteNum == 1) ? up1 : (spriteNum == 2) ? up2 : up0;
            case "down" -> image = (spriteNum == 1) ? down1 : (spriteNum == 2) ? down2 : down0;
            case "left" -> image = (spriteNum == 1) ? left1 : (spriteNum == 2) ? left2 : left0;
            case "right" -> image = (spriteNum == 1) ? right1 : (spriteNum == 2) ? right2 : right0;
        }
        g2.drawImage(image, pos.x, pos.y, gp.tileSize, gp.tileSize, null);

        if (inventory != null) {
            int invX = pos.x + gp.itemSize / 2;
            int invY = pos.y;
            inventory.draw(g2, invX, invY);
        }
    }

    // --- METHOD INTERAKSI (Tidak Berubah) ---
    public void interactWithStation() {
        Tile targetTile = getTargetTile();
        if (targetTile == null) return;

        if (targetTile instanceof CuttingStation) {
            ((CuttingStation) targetTile).interact(this);
        }
        else if (targetTile instanceof IngredientStorage) {
            ((IngredientStorage) targetTile).interact(this);
        }
        else if (targetTile instanceof ServingCounter) {
            ((ServingCounter) targetTile).interact(this);
        }
        else if (targetTile instanceof CookingStation) {
            ((CookingStation) targetTile).interact(this);
        }
        else if (targetTile instanceof PlateStorage) {
            ((PlateStorage) targetTile).interact(this);
        }
        else if (targetTile instanceof AssemblyStation) {
            ((AssemblyStation) targetTile).interact(this);
        }
        else if (targetTile instanceof WashingStation) {
            ((WashingStation) targetTile).interact(this);
        }
        else if (targetTile instanceof WashingCounter) {
            ((WashingCounter) targetTile).interact(this);
        }
        else if (targetTile instanceof TrashStation) {
            if (playerState == PlayerState.IDLE) {
                ((TrashStation) targetTile).interact(this);
                keyH.interactPressed = false;
            }
        }
    }

    public void interactWithFloor() {
        if (inventory != null) {
            gp.itemM.addItem(inventory, pos.x + gp.itemSize/2, pos.y + gp.itemSize);
            inventory = null;
        } else {
            Item foundItem = gp.cChecker.checkItem(this); // Gunakan CollisionChecker!
            if (foundItem != null) {
                inventory = foundItem;
                gp.itemM.itemsOnFloor.remove(foundItem); // Pastikan item dihapus dari lantai
                System.out.println("Item picked up from floor.");
            }
        }
    }

    private Tile getTargetTile() {
        int currentWorldX = pos.x + (gp.tileSize / 2);
        int currentWorldY = pos.y + (gp.tileSize / 2);
        int interactX = currentWorldX;
        int interactY = currentWorldY;

        switch (direction) {
            case "up" -> interactY -= gp.tileSize;
            case "down" -> interactY += gp.tileSize;
            case "left" -> interactX -= gp.tileSize;
            case "right" -> interactX += gp.tileSize;
        }

        int col = interactX / gp.tileSize;
        int row = interactY / gp.tileSize;

        if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {
            return gp.tileM.worldTiles[col][row];
        }
        return null;
    }
}