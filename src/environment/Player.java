package environment;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import tile.IngredientStorage;
import tile.Tile;

import static java.lang.Math.sqrt;

public class Player extends Entity {

    String id;
    String name;
    KeyHandler keyH;
    double movementX, movementY;

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);

        this.gp = gp;
        this.keyH = keyH;

        setDefaultValue();
        getPlayerImage();

        solidArea = new Rectangle(0, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void setDefaultValue() {
        pos.x = 480;
        pos.y = 480;
        speed = 4;
        direction = "down";
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
        else if (keyH.interactPressed) {
            interact();
            keyH.interactPressed = false;
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
        int currentWorldX = pos.x + (gp.tileSize / 2);
        int currentWorldY = pos.y + (gp.tileSize / 2);

        switch (direction) {
            case "up":
                currentWorldY -= gp.tileSize;
                break;
            case "down":
                currentWorldY += gp.tileSize;
                break;
            case "left":
                currentWorldX -= gp.tileSize;
                break;
            case "right":
                currentWorldX += gp.tileSize;
                break;
        }

        int col = currentWorldX / gp.tileSize;
        int row = currentWorldY / gp.tileSize;

        System.out.println("Cek Tile di Col: " + col + ", Row: " + row);

        if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {

            Tile targetTile;

            int tileNum = gp.tileM.mapTileNum[col][row];
            targetTile = gp.tileM.tile[tileNum];

            if (targetTile != null) {
                // KASUS 1: Interaksi dengan Station/Storage
                if (targetTile instanceof IngredientStorage) {
                    ((IngredientStorage) targetTile).interact(this);
                }

                // KASUS 2: Interaksi dengan Lantai (Tile tidak solid)
                else {

                    // A. DROP ITEM (Tangan Penuh -> Taruh di lantai)
                    if (inventory != null) {
                        // Pastikan di lantai itu belum ada item lain (opsional, biar ga numpuk)
                        Item existingItem = gp.itemM.pickUpItem(col, row);

                        if (existingItem == null) {
                            // Panggil ItemManager untuk simpan item
                            gp.itemM.addItem(inventory, col, row);
                            System.out.println("Drop " + inventory.name + " ke lantai.");
                            inventory = null;
                        } else {
                            // Jika sudah ada item, kembalikan item yg dicek tadi (karena pickUpItem menghapusnya)
                            gp.itemM.addItem(existingItem, col, row);
                            System.out.println("Lantai penuh!");
                        }
                    }

                    // B. PICK UP ITEM (Tangan Kosong -> Ambil dari lantai)
                    else {
                        Item foundItem = gp.itemM.pickUpItem(col, row);

                        if (foundItem != null) {
                            inventory = foundItem;
                            System.out.println("Mengambil " + inventory.name + " dari lantai.");
                        }
                    }
                }
            }
        }
    }
}
