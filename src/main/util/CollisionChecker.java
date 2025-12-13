package main.util;

import environment.entity.Entity;
import environment.entity.Player;
import environment.item.Item;
import tile.Tile;

public class CollisionChecker {

    protected GamePanel gp;

    /* Konstruktor untuk inisialisasi pemeriksa tabrakan dengan referensi panel game */
    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    /* Memeriksa deteksi tabrakan antara entitas dengan tile dinding atau batas peta berdasarkan arah gerak */
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
                    if (entityTopRow >= 0 && entityLeftCol >= 0 && entityRightCol < gp.maxScreenCol) {
                        tile1 = gp.tileM.worldTiles[entityLeftCol][entityTopRow];
                        tile2 = gp.tileM.worldTiles[entityRightCol][entityTopRow];
                        if (tile1.collision || tile2.collision) {
                            entity.collisionOn = true;
                        }
                    } else {
                        entity.collisionOn = true;
                    }
                    break;
                case "down":
                    entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                    if (entityBottomRow < gp.maxScreenRow && entityLeftCol >= 0 && entityRightCol < gp.maxScreenCol) {
                        tile1 = gp.tileM.worldTiles[entityLeftCol][entityBottomRow];
                        tile2 = gp.tileM.worldTiles[entityRightCol][entityBottomRow];
                        if (tile1.collision || tile2.collision) {
                            entity.collisionOn = true;
                        }
                    } else {
                        entity.collisionOn = true;
                    }
                    break;
                case "left":
                    entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                    if (entityLeftCol >= 0 && entityTopRow >= 0 && entityBottomRow < gp.maxScreenRow) {
                        tile1 = gp.tileM.worldTiles[entityLeftCol][entityTopRow];
                        tile2 = gp.tileM.worldTiles[entityLeftCol][entityBottomRow];
                        if (tile1.collision || tile2.collision) {
                            entity.collisionOn = true;
                        }
                    } else {
                        entity.collisionOn = true;
                    }
                    break;
                case "right":
                    entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                    if (entityRightCol < gp.maxScreenCol && entityTopRow >= 0 && entityBottomRow < gp.maxScreenRow) {
                        tile1 = gp.tileM.worldTiles[entityRightCol][entityTopRow];
                        tile2 = gp.tileM.worldTiles[entityRightCol][entityBottomRow];
                        if (tile1.collision || tile2.collision) {
                            entity.collisionOn = true;
                        }
                    } else {
                        entity.collisionOn = true;
                    }
                    break;
            }
        } catch (Exception e) {
            entity.collisionOn = true;
        }
    }

    /* Memeriksa apakah entitas bersentuhan dengan item yang berada di lantai untuk interaksi pengambilan */
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

    /* Memeriksa deteksi tabrakan antara entitas dengan pemain lain untuk mencegah tumpang tindih */
    public void checkPlayer(Entity entity) {
        Player[] players = gp.playerM.getPlayers();

        entity.solidArea.x = entity.pos.x + entity.solidArea.x;
        entity.solidArea.y = entity.pos.y + entity.solidArea.y;

        switch(entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        for (Player target : players) {
            if (target != null && target != entity) {

                target.solidArea.x = target.pos.x + target.solidArea.x;
                target.solidArea.y = target.pos.y + target.solidArea.y;

                if (entity.solidArea.intersects(target.solidArea)) {
                    entity.collisionOn = true;
                }

                target.solidArea.x = target.solidAreaDefaultX;
                target.solidArea.y = target.solidAreaDefaultY;
            }
        }

        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
    }
}