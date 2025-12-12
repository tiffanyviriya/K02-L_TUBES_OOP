package main.util;

import environment.entity.Entity;
import environment.entity.Player;
import environment.item.Item;
import tile.Tile;

public class CollisionChecker {

    protected GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // Cek tabrakan dengan Tile (Dinding/Station)
    public void checkTile(Entity entity) {
        // Hitung posisi hitbox di dunia
        int entityLeftWorldX = entity.pos.x + entity.solidArea.x;
        int entityRightWorldX = entity.pos.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.pos.y + entity.solidArea.y;
        int entityBottomWorldY = entity.pos.y + entity.solidArea.y + entity.solidArea.height;

        // Hitung kolom/baris grid
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        Tile tile1, tile2;

        try {
            switch(entity.direction) {
                case "up":
                    entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                    // Cek batas map agar tidak ArrayOutOfBounds
                    if (entityTopRow >= 0 && entityLeftCol >= 0 && entityRightCol < gp.maxScreenCol) {
                        tile1 = gp.tileM.worldTiles[entityLeftCol][entityTopRow];
                        tile2 = gp.tileM.worldTiles[entityRightCol][entityTopRow];
                        if (tile1.collision || tile2.collision) {
                            entity.collisionOn = true;
                        }
                    } else {
                        entity.collisionOn = true; // Anggap luar map adalah dinding
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
            // Fallback safety
            entity.collisionOn = true;
        }
    }

    // Cek tabrakan dengan Item di lantai (untuk Pick Up)
    public Item checkItem(Entity entity) {
        Item foundItem = null;

        // Loop semua item yang ada di lantai
        for (int i = 0; i < gp.itemM.itemsOnFloor.size(); i++) {
            Item target = gp.itemM.itemsOnFloor.get(i);

            if (target != null) {
                // Update posisi solidArea entity ke posisi world
                entity.solidArea.x = entity.pos.x + entity.solidArea.x;
                entity.solidArea.y = entity.pos.y + entity.solidArea.y;

                // Update posisi solidArea target item ke posisi world
                target.solidArea.x = target.worldX + target.solidArea.x;
                target.solidArea.y = target.worldY + target.solidArea.y;

                // Cek intersection
                if (entity.solidArea.intersects(target.solidArea)) {
                    foundItem = target;
                }

                // Reset posisi solidArea ke default (relative)
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

    // Cek tabrakan dengan Player lain (Chef vs Chef)
    public void checkPlayer(Entity entity) {
        // Ambil daftar semua player dari PlayerManager
        Player[] players = gp.playerM.getPlayers();

        // Update posisi solidArea entity (yang sedang bergerak) ke world position
        entity.solidArea.x = entity.pos.x + entity.solidArea.x;
        entity.solidArea.y = entity.pos.y + entity.solidArea.y;

        // Prediksi posisi entity di frame berikutnya berdasarkan arah gerak
        switch(entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        // Loop untuk mengecek tabrakan dengan player LAIN
        for (Player target : players) {
            if (target != null && target != entity) { // Jangan cek diri sendiri

                // Update posisi solidArea target (player lain) ke world position
                target.solidArea.x = target.pos.x + target.solidArea.x;
                target.solidArea.y = target.pos.y + target.solidArea.y;

                // Cek intersection
                if (entity.solidArea.intersects(target.solidArea)) {
                    entity.collisionOn = true;
                }

                // Reset posisi solidArea target ke default
                target.solidArea.x = target.solidAreaDefaultX;
                target.solidArea.y = target.solidAreaDefaultY;
            }
        }

        // Reset posisi solidArea entity ke default
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
    }
}