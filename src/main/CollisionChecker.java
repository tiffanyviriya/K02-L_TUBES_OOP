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

    public Item checkItem(Entity entity) {

        Item foundItem = null;

        // Loop melalui semua item yang ada di ItemManager
        for (int i = 0; i < gp.itemM.itemsOnFloor.size(); i++) {

            Item target = gp.itemM.itemsOnFloor.get(i);

            if (target != null) {
                // 1. Dapatkan posisi SolidArea Entity (Player) secara Global
                entity.solidArea.x = entity.pos.x + entity.solidArea.x;
                entity.solidArea.y = entity.pos.y + entity.solidArea.y;

                // 2. Dapatkan posisi SolidArea Target (Item) secara Global
                target.solidArea.x = target.worldX + target.solidArea.x;
                target.solidArea.y = target.worldY + target.solidArea.y;

                // 3. Cek Intersect (Apakah kotak merah bersentuhan?)
                if (entity.solidArea.intersects(target.solidArea)) {
                    foundItem = target;
                }

                // 4. RESET SolidArea ke default (Sangat Penting!)
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;

                target.solidArea.x = target.solidAreaDefaultX;
                target.solidArea.y = target.solidAreaDefaultY;

                // Jika sudah ketemu satu, langsung kembalikan (agar tidak ambil 2 item sekaligus)
                if (foundItem != null) {
                    break;
                }
            }
        }

        return foundItem;
    }

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
