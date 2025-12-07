package main;

import environment.*;
import tile.IngredientStorage;

import java.io.IOException;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX = entity.pos.x + entity.solidArea.x + 8;
        int entityRightWorldX = entity.pos.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.pos.y + entity.solidArea.y;
        int entityBottomWorldY = entity.pos.y + entity.solidArea.y + entity.solidArea.height - 4;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        int tileNum1, tileNum2;

        switch(entity.direction) {
            case "up" :
                entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down" :
                entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right" :
                entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left" :
                entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }
    
    public void interact(Entity entity) throws IOException {
        int currentWorldX = entity.pos.x + (gp.tileSize / 2);
        int currentWorldY = entity.pos.y + (gp.tileSize / 2);

        switch (entity.direction) {
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

        if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {

            int tileNum = gp.tileM.mapTileNum[col][row];

            if (gp.tileM.tile[tileNum] instanceof IngredientStorage) {

                IngredientStorage station = (IngredientStorage) gp.tileM.tile[tileNum];

                station.interact(entity);
            }
        }
    }

//    public int checkEntity(Entity entity, Entity[] target) {
//        int index = 999;
//
//        for(int i = 0; i < target.length; i++) {


    public void checkPlayer(Entity entity) {
        entity.solidArea.x = entity.pos.x + entity.solidArea.x;
        entity.solidArea.y = entity.pos.y + entity.solidArea.y;

        gp.player.solidArea.x = gp.player.pos.x + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.pos.y + gp.player.solidArea.y;

        switch(entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        if(entity.solidArea.intersects(gp.player.solidArea)) {
            entity.collisionOn = true;
        }

        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
    }
}
