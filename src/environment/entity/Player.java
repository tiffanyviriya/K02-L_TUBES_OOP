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

    private int dashCooldown = 0;
    private final int DASH_MAX_COOLDOWN = 60;
    private final int DASH_DISTANCE = 3;

    private final int THROW_DISTANCE = 4;

    // Identitas Player untuk Multiplayer
    public int myIndex = 0;

    // State tombol terakhir untuk mencegah spam network
    private boolean lastUp, lastDown, lastLeft, lastRight, lastAction, lastInteract;

    /* Konstruktor */
    public Player(GamePanel gp, KeyHandler keyH, int posX, int posY) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;
        pos.x = posX;
        pos.y = posY;

        // [FIX HITBOX] Gunakan nilai RELATIF (Offset dari gambar).
        // 0, 16, 32, 32 = Mulai dari x=0, y=16 (relatif gambar), lebar=32, tinggi=32
        solidArea = new Rectangle(0, 16, 32, 32);

        // Simpan nilai default relatif untuk reset setelah collision check
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValue();
        getPlayerImage();
    }

    /* Set Default Values */
    public void setDefaultValue() {
        speed = 4;
        direction = "down";
        playerState = PlayerState.IDLE;
    }

    /* Load Images */
    public void getPlayerImage() {
        try {
            // Pastikan nama file sesuai dengan resource Anda
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

    /* Main Update Loop */
    public void update() {

        boolean isLocal = false;

        // --- CEK OTORITAS ---
        // Jika Online: Cek ID saya. Jika Offline: Cek ActivePlayerManager.
        if (gp.netClient.isConnected && gp.netClient.myPlayerId != -1) {
            if (this.myIndex == gp.netClient.myPlayerId) isLocal = true;
        } else {
            if (gp.playerM.getActivePlayer() == this) isLocal = true;
        }

        boolean moveUp = false;
        boolean moveDown = false;
        boolean moveLeft = false;
        boolean moveRight = false;
        boolean action = false;   // V (Pick/Drop)
        boolean interact = false; // C (Station Interact)

        if (isLocal) {
            // --- INPUT LOKAL ---
            moveUp = keyH.upPressed;
            moveDown = keyH.downPressed;
            moveLeft = keyH.leftPressed;
            moveRight = keyH.rightPressed;
            action = keyH.actionPressed;     // V
            interact = keyH.interactPressed; // C

            // --- KIRIM KE SERVER ---
            if (gp.netClient.isConnected) {
                if (moveUp != lastUp) { gp.netClient.sendInput("W", moveUp); lastUp = moveUp; }
                if (moveDown != lastDown) { gp.netClient.sendInput("S", moveDown); lastDown = moveDown; }
                if (moveLeft != lastLeft) { gp.netClient.sendInput("A", moveLeft); lastLeft = moveLeft; }
                if (moveRight != lastRight) { gp.netClient.sendInput("D", moveRight); lastRight = moveRight; }
                if (action != lastAction) { gp.netClient.sendInput("V", action); lastAction = action; }
                if (interact != lastInteract) { gp.netClient.sendInput("C", interact); lastInteract = interact; }
            }

        } else {
            // --- INPUT REMOTE (Dari Buffer) ---
            if (gp.netClient.isConnected) {
                moveUp = gp.remoteInputBuffer.up;
                moveDown = gp.remoteInputBuffer.down;
                moveLeft = gp.remoteInputBuffer.left;
                moveRight = gp.remoteInputBuffer.right;
                action = gp.remoteInputBuffer.action;
                interact = gp.remoteInputBuffer.interact;
            }
        }

        // --- LOGIKA AKSI (Local & Remote) ---

        // 1. Pick/Drop (V)
        if (action) {
            interactWithFloor();
            // Reset key lokal untuk mencegah spam toggle (tergantung mekanik game)
            if (isLocal) keyH.actionPressed = false;
        }

        // 2. Interact Station (C)
        if (interact) {
            interactWithStation();
            if (isLocal) keyH.interactPressed = false;
        }

        // 3. Dash & Throw (Sementara Local Only via KeyHandler)
        if (dashCooldown > 0) dashCooldown--;

        if (isLocal && keyH.dashPressed && dashCooldown == 0) {
            performDash();
        }

        if (isLocal && keyH.throwPressed) {
            performThrow();
            keyH.throwPressed = false;
        }

        // --- UPDATE PERGERAKAN ---
        if (moveUp || moveDown || moveLeft || moveRight) {
            // Standardisasi arah menjadi "up", "down", dll.
            if (moveUp) direction = "up";
            else if (moveDown) direction = "down";
            else if (moveLeft) direction = "left";
            else if (moveRight) direction = "right";

            // Cek Tabrakan
            collisionOn = false;
            gp.cChecker.checkTile(this);
            // Tambahkan gp.cChecker.checkObject(this, false) jika perlu

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> pos.y -= speed;
                    case "down" -> pos.y += speed;
                    case "left" -> pos.x -= speed;
                    case "right" -> pos.x += speed;
                }
            }

            // Animasi Sprite
            spriteCounter++;
            if (spriteCounter > 12) {
                if (spriteNum == 1) spriteNum = 2;
                else if (spriteNum == 2) spriteNum = 1;
                spriteCounter = 0;
            }
        }

        // [FIX PENTING] RESET HITBOX
        // Jangan pernah set ke pos.x + 4 di sini.
        // Kita kembalikan ke offset relatif agar kalkulasi collision frame berikutnya benar.
        solidArea.x = solidAreaDefaultX;
        solidArea.y = solidAreaDefaultY;
    }

    /* Aksi Dash */
    private void performDash() {
        for (int i = 0; i < DASH_DISTANCE * gp.tileSize; i += speed) {
            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> pos.y -= speed;
                    case "down" -> pos.y += speed;
                    case "left" -> pos.x -= speed;
                    case "right" -> pos.x += speed;
                }
            } else {
                break;
            }
        }
        dashCooldown = DASH_MAX_COOLDOWN;
    }

    /* Aksi Lempar (Throw) */
    private void performThrow() {
        if (inventory == null) return;

        // Hanya boleh lempar bahan mentah (contoh rule)
        if (inventory instanceof Ingredient) {
            Ingredient ing = (Ingredient) inventory;
            if (ing.state != IngredientState.RAW) return;
        } else {
            return;
        }

        int startX = pos.x;
        int startY = pos.y;
        int finalX = startX;
        int finalY = startY;
        boolean caughtByChef = false;

        for (int i = 1; i <= THROW_DISTANCE; i++) {
            int checkX = startX;
            int checkY = startY;

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
                if (t.collision) break;
            }

            Player otherPlayer = getPlayerAt(checkX, checkY);
            if (otherPlayer != null && otherPlayer != this) {
                if (otherPlayer.inventory == null) {
                    otherPlayer.inventory = this.inventory;
                    this.inventory = null;
                    caughtByChef = true;
                } else {
                    finalX = checkX;
                    finalY = checkY;
                }
                break;
            }
            finalX = checkX;
            finalY = checkY;
        }

        if (!caughtByChef && inventory != null) {
            gp.itemM.addItem(inventory, finalX + gp.itemSize/2, finalY + gp.itemSize);
            inventory = null;
        }
    }

    /* Helper: Cari Player lain */
    private Player getPlayerAt(int x, int y) {
        if (gp.playerM != null && gp.playerM.players != null) {
            for (Player p : gp.playerM.players) {
                if (p == null) continue;
                int dist = (int) Math.sqrt(Math.pow(p.pos.x - x, 2) + Math.pow(p.pos.y - y, 2));
                if (dist < gp.tileSize) return p;
            }
        }
        return null;
    }

    /* Helper: Cari Tile target */
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

    /* Interaksi Station */
    public void interactWithStation() {
        Tile targetTile = getTargetTile();
        if (targetTile == null) return;

        if (targetTile instanceof CuttingStation) ((CuttingStation) targetTile).interact(this);
        else if (targetTile instanceof IngredientStorage) ((IngredientStorage) targetTile).interact(this);
        else if (targetTile instanceof ServingCounter) ((ServingCounter) targetTile).interact(this);
        else if (targetTile instanceof CookingStation) ((CookingStation) targetTile).interact(this);
        else if (targetTile instanceof PlateStorage) ((PlateStorage) targetTile).interact(this);
        else if (targetTile instanceof AssemblyStation) ((AssemblyStation) targetTile).interact(this);
        else if (targetTile instanceof WashingStation) ((WashingStation) targetTile).interact(this);
        else if (targetTile instanceof WashingCounter) ((WashingCounter) targetTile).interact(this);
        else if (targetTile instanceof TrashStation) {
            if (playerState == PlayerState.IDLE) {
                ((TrashStation) targetTile).interact(this);
            }
        }
    }

    /* Interaksi Floor */
    public void interactWithFloor() {
        if (inventory != null) {
            gp.itemM.addItem(inventory, pos.x + gp.itemSize/2, pos.y + gp.itemSize);
            inventory = null;
        } else {
            Item foundItem = gp.itemM.getItemOnPlayer(this);
            if (foundItem != null) {
                inventory = foundItem;
            }
        }
    }

    /* Draw */
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
}