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

    // --- [CONFIG FITUR BARU] ---
    private int dashCooldown = 0;
    private final int DASH_MAX_COOLDOWN = 60; // 1 Detik (60 frames)
    private final int DASH_DISTANCE = 3;      // Jarak Dash (dalam kotak)

    private final int THROW_DISTANCE = 4;     // Jarak Lempar (dalam kotak)
    // ---------------------------

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
        // ... (Kode load image tetap sama, tidak berubah) ...
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
        // Kurangi cooldown dash setiap frame
        if (dashCooldown > 0) dashCooldown--;

        // 1. PRIORITAS UTAMA: SWITCH CHEF
        if (keyH.switchPressed) {
            gp.playerM.switchPlayer();
            keyH.switchPressed = false;
            return;
        }

        // 2. CEK TOMBOL INTERAKSI (V - Action)
        if (keyH.actionPressed) {
            interactWithStation();
            keyH.actionPressed = false;
        }

        // 3. CEK STATUS BUSY
        if (playerState == PlayerState.BUSY) {
            return;
        }

        // --- [FITUR BARU 1: DASH (Tombol L)] ---
        if (keyH.dashPressed && dashCooldown == 0 && playerState == PlayerState.IDLE) {
            performDash();
            keyH.dashPressed = false; // Reset agar tidak dash terus menerus
        }

        // --- [FITUR BARU 2: THROW (Tombol T)] ---
        if (keyH.throwPressed && playerState == PlayerState.IDLE) {
            performThrow();
            keyH.throwPressed = false;
        }

        // 4. LOGIKA PERGERAKAN NORMAL
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
        if (keyH.interactPressed && playerState == PlayerState.IDLE) {
            interactWithFloor();
            keyH.interactPressed = false;
        }
    }

    // --- METHOD DASH ---
    private void performDash() {
        System.out.println("DASH!");
        // Coba maju beberapa langkah (tile) sekaligus
        for (int i = 0; i < DASH_DISTANCE * gp.tileSize; i += speed) {
            collisionOn = false;
            gp.cChecker.checkTile(this); // Cek apakah di depan ada tembok

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> pos.y -= speed;
                    case "down" -> pos.y += speed;
                    case "left" -> pos.x -= speed;
                    case "right" -> pos.x += speed;
                }
            } else {
                break; // Berhenti jika nabrak tembok
            }
        }
        dashCooldown = DASH_MAX_COOLDOWN; // Set cooldown
    }

    // --- METHOD THROW ---
    private void performThrow() {
        // 1. Validasi: Harus pegang Item
        if (inventory == null) return;

        // 2. Validasi: Harus Ingredient Mentah (Raw)
        if (inventory instanceof Ingredient) {
            Ingredient ing = (Ingredient) inventory;
            if (ing.state != IngredientState.RAW) {
                System.out.println("Hanya bahan mentah yang bisa dilempar!");
                return;
            }
        } else {
            System.out.println("Item ini tidak bisa dilempar!");
            return; // Piring/Panci tidak bisa dilempar
        }

        System.out.println("THROW!");

        int startX = pos.x;
        int startY = pos.y;
        int finalX = startX;
        int finalY = startY;

        boolean caughtByChef = false;

        // Loop simulasi pergerakan item (per Tile)
        for (int i = 1; i <= THROW_DISTANCE; i++) {
            int checkX = startX;
            int checkY = startY;

            // Hitung koordinat tile ke-i di depan
            switch (direction) {
                case "up" -> checkY -= (i * gp.tileSize);
                case "down" -> checkY += (i * gp.tileSize);
                case "left" -> checkX -= (i * gp.tileSize);
                case "right" -> checkX += (i * gp.tileSize);
            }

            // A. Cek Tabrakan Tembok/Station
            int col = (checkX + gp.tileSize/2) / gp.tileSize;
            int row = (checkY + gp.tileSize/2) / gp.tileSize;

            if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {
                Tile t = gp.tileM.worldTiles[col][row];
                // Jika tile tersebut solid (tembok/meja), item jatuh di tile SEBELUMNYA
                if (t.collision) {
                    System.out.println("Lemparan kena tembok!");
                    break; // Stop loop, gunakan finalX/Y terakhir yang valid
                }
            }

            // B. Cek Tabrakan dengan Chef Lain (CATCH)
            Player otherPlayer = getPlayerAt(checkX, checkY);
            if (otherPlayer != null && otherPlayer != this) {
                // Chef lain ada di jalur lemparan!
                if (otherPlayer.inventory == null) {
                    // TANGKAP!
                    otherPlayer.inventory = this.inventory;
                    this.inventory = null;
                    caughtByChef = true;
                    System.out.println("NICE CATCH!");
                    // Mainkan suara catch jika ada (optional)
                } else {
                    // Chef lain tangan penuh, item jatuh di kakinya
                    finalX = checkX;
                    finalY = checkY;
                }
                break; // Stop lemparan
            }

            // Jika aman, update posisi target jatuh
            finalX = checkX;
            finalY = checkY;
        }

        // Jika tidak ditangkap chef, jatuhkan item di lantai (posisi terakhir valid)
        if (!caughtByChef && inventory != null) {
            // Drop item
            gp.itemM.addItem(inventory, finalX + gp.itemSize/2, finalY + gp.itemSize);
            inventory = null;
            // Mainkan suara lempar/jatuh
        }
    }

    // Helper untuk mencari player di koordinat tertentu (untuk fitur Catch)
    private Player getPlayerAt(int x, int y) {
        // Kita butuh akses ke daftar semua player.
        // Asumsi gp.playerM.players adalah array/list player
        if (gp.playerM != null && gp.playerM.players != null) {
            for (Player p : gp.playerM.players) {
                if (p == null) continue;
                // Cek jarak euclidean sederhana atau bounding box
                int dist = (int) Math.sqrt(Math.pow(p.pos.x - x, 2) + Math.pow(p.pos.y - y, 2));
                if (dist < gp.tileSize) { // Jika dalam radius 1 tile
                    return p;
                }
            }
        }
        return null;
    }

    // ... (Sisa method draw, interactWithStation, interactWithFloor TETAP SAMA) ...
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

    public void interactWithStation() {
        Tile targetTile = getTargetTile();
        if (targetTile == null) return;

        if (targetTile instanceof CuttingStation) ((CuttingStation) targetTile).interact(this);
        else if (targetTile instanceof IngredientStorage) ((IngredientStorage) targetTile).interact(this);
        else if (targetTile instanceof ServingCounter) ((ServingCounter) targetTile).interact(this);
        else if (targetTile instanceof CookingStation) ((CookingStation) targetTile).interact(this);
        else if (targetTile instanceof PlateStorage) ((PlateStorage) targetTile).interact(this);
        else if (targetTile instanceof AssemblyStation) ((AssemblyStation) targetTile).interact(this);
        else if (targetTile instanceof WashingStation) {
            ((WashingStation) targetTile).interact(this);
        }
        else if (targetTile instanceof WashingCounter) ((WashingCounter) targetTile).interact(this);
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
            System.out.println("Item dropped on floor.");
        } else {
            Item foundItem = gp.itemM.getItemOnPlayer(this);
            if (foundItem != null) {
                inventory = foundItem;
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