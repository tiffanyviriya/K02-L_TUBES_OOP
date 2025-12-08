package environment;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import main.PlayerState;
import tile.IngredientStorage;
import tile.Tile;

import static java.lang.Math.sqrt;

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
        // --- 1. CEK STATUS BUSY ---
        // Jika sedang memotong, player tidak boleh bergerak.
        if (playerState == PlayerState.BUSY) {
            // Jika tombol interact dilepas, batalkan status BUSY (Pause memotong)
            if (!keyH.interactPressed) {
                playerState = PlayerState.IDLE;
            } else {
                // Jika tombol masih ditahan, lanjutkan interaksi (memotong)
                interact();
            }
            return; // STOP, jangan jalankan kode pergerakan di bawah
        }

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (collisionOn == false) {
                if (direction.equals("up")) {
                    pos.y -= speed;
                }
                if (direction.equals("down")) {
                    pos.y += speed;
                }
                if (direction.equals("right")) {
                    pos.x += speed;
                }
                if (direction.equals("left")) {
                    pos.x -= speed;
                }
            }

            spriteCounter++;
            if(spriteCounter > 12) {
                if(spriteNum == 1) {
                    spriteNum = 2;
                }
                else if(spriteNum == 2) {
                    spriteNum = 3;
                }
                else if(spriteNum == 3) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
        if (keyH.interactPressed) {
            interact();
            // keyH.interactPressed = false; (hapus ya biar tombolnya bisa dihold)
        }
        if (keyH.switchPressed) {
            gp.playerM.switchPlayer();
            keyH.switchPressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch(direction) {
            case "up":
                image = (spriteNum == 1) ? up1 : (spriteNum == 2) ? up2 : up0;
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : (spriteNum == 2) ? down2 : down0;
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : (spriteNum == 2) ? left2 : left0;
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : (spriteNum == 2) ? right2 : right0;
                break;
        }

        g2.drawImage(image, pos.x, pos.y, gp.tileSize, gp.tileSize, null);

        if (inventory != null) {
            inventory.worldX = pos.x + gp.itemSize / 2;
            inventory.worldY = pos.y;

            inventory.draw(g2);
        }
    }

    public void interact() {
        // Hitung koordinat depan player
        int currentWorldX = pos.x + (gp.tileSize / 2);
        int currentWorldY = pos.y + (gp.tileSize / 2);
        int interactX = currentWorldX;
        int interactY = currentWorldY;

        switch (direction) {
            case "up": interactY -= gp.tileSize; break;
            case "down": interactY += gp.tileSize; break;
            case "left": interactX -= gp.tileSize; break;
            case "right": interactX += gp.tileSize; break;
        }

        int col = interactX / gp.tileSize;
        int row = interactY / gp.tileSize;

        // Ambil Tile dari TileManager (Menggunakan array objek dunia, lihat poin 3 di bawah)
        Tile targetTile = null;
        if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {
            // ASUMSI: Kita sudah update TileManager (lihat poin 3)
            targetTile = gp.tileM.worldTiles[col][row];
        }

        // --- INTERAKSI STATION ---

        // 1. Cutting Station
        if (targetTile instanceof CuttingStation) {
            ((CuttingStation) targetTile).interact(this);
            return;
        }

        // 2. Ingredient Storage
        if (targetTile instanceof IngredientStorage) {
            // Storage harus "sekali tekan", bukan "tahan"
            // Kita pakai trick sederhana: hanya jalan jika player IDLE (baru tekan)
            // Dan kita paksa interactPressed false setelah ambil agar tidak ambil beruntun
            if (playerState == PlayerState.IDLE) {
                ((IngredientStorage) targetTile).interact(this);
                keyH.interactPressed = false;
            }
            return;
        }

        // --- INTERAKSI LANTAI (DROP/PICKUP) ---
        // Hanya jalankan jika belum melakukan apa-apa di frame ini
        if (keyH.interactPressed) {
            // Drop Logic
            if (inventory != null) {
                gp.itemM.addItem(inventory, pos.x + gp.itemSize/2, pos.y + gp.itemSize);
                inventory = null;
                keyH.interactPressed = false; // Reset agar tidak langsung pick up lagi
            }
            // Pick Up Logic
            else {
                Item foundItem = gp.itemM.getItemOnPlayer(this);
                if (foundItem != null) {
                    inventory = foundItem;
                    keyH.interactPressed = false; // Reset
                }
            }
        }
    }