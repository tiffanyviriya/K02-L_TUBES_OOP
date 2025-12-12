package main.util;

import environment.entity.Entity;
import environment.item.Item;
import tile.Tile;

public class CollisionChecker {

    protected GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.pos.x + entity.solidArea.x;
        int entityRightWorldX = entity.pos.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.pos.y + entity.solidArea.y;
        int entityBottomWorldY = entity.pos.y + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        Tile tile1, tile2;

        try {
            switch(entity.direction) {
                case "up":
                    entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                    tile1 = gp.tileM.worldTiles[entityLeftCol][entityTopRow];
                    tile2 = gp.tileM.worldTiles[entityRightCol][entityTopRow];
                    if(tile1.collision || tile2.collision) {
                        entity.collisionOn = true;
                    }
                    break;
                case "down":
                    entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                    tile1 = gp.tileM.worldTiles[entityLeftCol][entityBottomRow];
                    tile2 = gp.tileM.worldTiles[entityRightCol][entityBottomRow];
                    if(tile1.collision || tile2.collision) {
                        entity.collisionOn = true;
                    }
                    break;
                case "left":
                    entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                    tile1 = gp.tileM.worldTiles[entityLeftCol][entityTopRow];
                    tile2 = gp.tileM.worldTiles[entityLeftCol][entityBottomRow];
                    if(tile1.collision || tile2.collision) {
                        entity.collisionOn = true;
                    }
                    break;
                case "right":
                    entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                    tile1 = gp.tileM.worldTiles[entityRightCol][entityTopRow];
                    tile2 = gp.tileM.worldTiles[entityRightCol][entityBottomRow];
                    if(tile1.collision || tile2.collision) {
                        entity.collisionOn = true;
                    }
                    break;
            }
        } catch (Exception e) {
            entity.collisionOn = true;
        }
    }

    public Item checkItem(Entity entity) {
        Item foundItem = null;

        for (int i = 0; i < gp.itemM.itemsOnFloor.size(); i++) {
            Item target = gp.itemM.itemsOnFloor.get(i);

            if (target != null) {
                entity.solidArea.x = entity.pos.x + entity.solidArea.x;
                entity.solidArea.y = entity.pos.y + entity.solidArea.y;

                target.solidArea.x = target.worldX + target.solidArea.x;
                target.solidArea.y = target.worldY + target.solidArea.y;

                if (entity.solidArea.intersects(target.solidArea)) {
                    foundItem = target;
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;

                target.solidArea.x = target.solidAreaDefaultX;
                target.solidArea.y = target.solidAreaDefaultY;

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

        gp.playerM.getActivePlayer().solidArea.x = gp.playerM.getActivePlayer().pos.x + gp.playerM.getActivePlayer().solidArea.x;
        gp.playerM.getActivePlayer().solidArea.y = gp.playerM.getActivePlayer().pos.y + gp.playerM.getActivePlayer().solidArea.y;

        switch(entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        if(entity.solidArea.intersects(gp.playerM.getActivePlayer().solidArea)) {
            entity.collisionOn = true;
        }

        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.playerM.getActivePlayer().solidArea.x = gp.playerM.getActivePlayer().solidAreaDefaultX;
        gp.playerM.getActivePlayer().solidArea.y = gp.playerM.getActivePlayer().solidAreaDefaultY;
    }
}