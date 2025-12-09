package environment;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import main.PlayerState;
import tile.CookingStation;
import tile.IngredientStorage;
import tile.ServingCounter;
import tile.Tile;
import tile.CuttingStation;

public class Player extends Entity {

    KeyHandler keyH;
    public PlayerState playerState;

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
        // --- 1. LOGIKA HOLD & BUSY (Cutting) ---
        // Menggunakan tombol V (actionPressed)
        if (playerState == PlayerState.BUSY) {
            // Jika tombol V dilepas saat sedang memotong -> BERHENTI
            if (!keyH.actionPressed) {
                playerState = PlayerState.IDLE;
            } else {
                // Jika tombol V ditahan -> LANJUT MEMOTONG
                interactWithStation();
            }
            return; // Jangan jalankan kode movement jika sedang busy
        }

        // --- 2. PERGERAKAN ---
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

        // --- 3. INPUT HANDLING ---

        // TOMBOL V: Interaksi Station (Cut, Cook, Serve, Storage)
        if (keyH.actionPressed) {
            interactWithStation();

            // Reset tombol hanya jika TIDAK sedang memotong (agar tidak spamming ambil bahan)
            // Jika sedang memotong (BUSY), tombol dibiarkan true agar terdeteksi "ditahan"
            if (playerState != PlayerState.BUSY) {
                keyH.actionPressed = false;
            }
        }

        // TOMBOL C: Drop / Pick Up (Lantai)
        if (keyH.interactPressed) {
            interactWithFloor();
            keyH.interactPressed = false; // Selalu reset karena drop/pick sekali tekan
        }

        // Switch Player
        if (keyH.switchPressed) {
            gp.playerM.switchPlayer();
            keyH.switchPressed = false;
        }
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
            inventory.worldX = pos.x + gp.itemSize / 2;
            inventory.worldY = pos.y;
            inventory.draw(g2);
        }
    }

    // --- METHOD INTERAKSI KHUSUS STATION (Key V) ---
    public void interactWithStation() {
        Tile targetTile = getTargetTile();

        if (targetTile == null) return;

        // 1. Cutting Station
        if (targetTile instanceof CuttingStation) {
            ((CuttingStation) targetTile).interact(this);
        }
        // 2. Ingredient Storage
        else if (targetTile instanceof IngredientStorage) {
            // Hanya bisa ambil jika player sedang IDLE (mencegah ambil beruntun saat tahan V)
            if (playerState == PlayerState.IDLE) {
                ((IngredientStorage) targetTile).interact(this);
            }
        }
        // 3. Serving Counter
        else if (targetTile instanceof ServingCounter) {
            ((ServingCounter) targetTile).interact(this);
        }
        // 4. Cooking Station (Jika ada)
        else if (targetTile instanceof CookingStation) {
            ((CookingStation) targetTile).interact(this);
        }
    }

    // --- METHOD INTERAKSI KHUSUS LANTAI (Key C) ---
    public void interactWithFloor() {
        // Drop Logic
        if (inventory != null) {
            // Cek apakah di depan tembok/station? (Opsional, tapi biasanya drop di koordinat player)
            // Versi simple: Drop tepat di kaki/depan player
            gp.itemM.addItem(inventory, pos.x + gp.itemSize/2, pos.y + gp.itemSize);
            inventory = null;
            System.out.println("Item dropped on floor.");
        }
        // Pick Up Logic
        else {
            Item foundItem = gp.itemM.getItemOnPlayer(this);
            if (foundItem != null) {
                inventory = foundItem;
                System.out.println("Item picked up from floor.");
            }
        }
    }

    // Helper untuk mencari Tile di depan player
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