package environment;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

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

//            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
//            interactNPC(npcIndex);

//            if (collisionOn == false) {
//                if (direction.equals("up")) {
//                    movementY += 1;
//                }
//                if (direction.equals("down")) {
//                    movementY -= 1;
//                }
//                if (direction.equals("right")) {
//                    movementX -= 1;
//                }
//                if (direction.equals("left")) {
//                    movementX += 1;
//                }
//
//                normalize();
//                movementY *= speed;
//                movementY *= speed;
//                this.pos.x += movementX;
//                this.y += movementY;
//            }

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
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
//        BufferedImage image = neutral;

        switch(direction) {
            case "up":
                if(spriteNum == 1) {
                    image = up1;
                }
                else if(spriteNum == 2) {
                    image = up2;
                }
                else if(spriteNum == 3) {
                    image = up0;
                }
                break;

            case "down":
                if(spriteNum == 1) {
                    image = down1;
                }
                else if(spriteNum == 2) {
                    image = down2;
                }
                else if(spriteNum == 3) {
                    image = down0;
                }
                break;

            case "left":
                if(spriteNum == 1) {
                    image = left1;
                }
                else if(spriteNum == 2) {
                    image = left2;
                }
                else if(spriteNum == 3) {
                    image = left0;
                }
                break;

            case "right":
                if(spriteNum == 1) {
                    image = right1;
                }
                else if(spriteNum == 2) {
                    image = right2;
                }
                else if(spriteNum == 3) {
                    image = right0;
                }
                break;
        }

        g2.drawImage(image, pos.x, pos.y, gp.tileSize, gp.tileSize, null);
    }

    private void normalize() {
        double length = sqrt(movementX * movementX + movementY * movementY);
        movementX /= length;
        movementY /= length;
    }
}
