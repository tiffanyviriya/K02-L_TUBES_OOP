package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import environment.KitchenUtensil;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {
    GamePanel gp;
    public Tile[][] worldTiles;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        worldTiles = new Tile[gp.maxScreenCol][gp.maxScreenRow];

        loadMap("/maps/map4.txt");
        setupTiles();

        setupDefaultUtensils();
    }

    public void loadMap(String mapfile) {
        try {
            InputStream is = getClass().getResourceAsStream(mapfile);
            BufferedReader br  = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while(col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();
                String numbers[] = line.split(" ");

                while(col < gp.maxScreenCol) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void setupTiles() {
        int col = 0;
        int row = 0;

        while(col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileType = mapTileNum[col][row];

            switch (tileType) {
                case 0: // Floor
                    worldTiles[col][row] = new Tile(gp);
                    setupImage(worldTiles[col][row], "/tiles/floor_tile.png", false);
                    break;
                case 1: // Wall
                    worldTiles[col][row] = new Tile(gp);
                    setupImage(worldTiles[col][row], "/tiles/wall_tile.png", true);
                    break;
                case 2: // Cutting Station
                    worldTiles[col][row] = new CuttingStation(gp);
                    break;
                case 3: // Ingredient Storage (Cucumber) - Contoh
                    worldTiles[col][row] = new IngredientStorage(gp, "cucumber");
                    break;
                case 9:
                    worldTiles[col][row] = new IngredientStorage(gp, "rice"); // Beras
                    break;
                case 10:
                    worldTiles[col][row] = new IngredientStorage(gp, "fish"); // Ikan
                    break;
                case 11:
                    worldTiles[col][row] = new IngredientStorage(gp, "shrimp"); // Udang
                    break;
                case 12:
                    worldTiles[col][row] = new IngredientStorage(gp, "nori"); // Nori
                    break;
                case 4: // Cooking Station
                    worldTiles[col][row] = new CookingStation(gp);
                    break;
                case 5: // Serving Counter
                    worldTiles[col][row] = new ServingCounter(gp);
                    break;
                case 6: // Plate Storage
                    worldTiles[col][row] = new PlateStorage(gp);
                    break;

                // [PERBAIKAN UTAMA DI SINI]
                case 7: // Assembly Station (Horizontal)
                    worldTiles[col][row] = new AssemblyStation(gp, "horizontal");
                    break;

                case 8: // Assembly Station (Vertical) - [BARU]
                    worldTiles[col][row] = new AssemblyStation(gp, "vertical");
                    break;

                default: // Default Floor
                    worldTiles[col][row] = new Tile(gp);
                    setupImage(worldTiles[col][row], "/tiles/floor_tile.png", false);
                    break;
            }

            col++;
            if (col == gp.maxScreenCol) {
                col = 0;
                row++;
            }
        }
    }

    public void setupDefaultUtensils() {
        placeUtensilOnStation(12, 2, "Pot");
        placeUtensilOnStation(13, 2, "Pot");
        placeUtensilOnStation(14, 2, "Pan");
    }

    private void placeUtensilOnStation(int col, int row, String utensilName) {
        if (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            if (worldTiles[col][row] instanceof CookingStation) {
                CookingStation cs = (CookingStation) worldTiles[col][row];
                cs.utensilOnStation = new KitchenUtensil(gp, utensilName);
                System.out.println("Spawned " + utensilName + " at [" + col + "," + row + "]");
            }
        }
    }

    private void setupImage(Tile tile, String path, boolean collision) {
        try {
            tile.image = ImageIO.read(getClass().getResourceAsStream(path));
            tile.collision = collision;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void update() {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {

                Tile currentTile = worldTiles[col][row];

                if (currentTile instanceof CookingStation) {
                    ((CookingStation) currentTile).update();
                }
                else if (currentTile instanceof ServingCounter) {
                    ((ServingCounter) currentTile).update();
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while(col < gp.maxScreenCol && row < gp.maxScreenRow) {

            Tile currentTile = worldTiles[col][row];

            // Panggil method draw khusus untuk setiap tipe station
            if (currentTile instanceof CuttingStation) {
                ((CuttingStation)currentTile).draw(g2, x, y);
            }
            else if (currentTile instanceof CookingStation) {
                ((CookingStation)currentTile).draw(g2, x, y);
            }
            else if (currentTile instanceof AssemblyStation) {
                ((AssemblyStation)currentTile).draw(g2, x, y);
            }
            else if (currentTile instanceof PlateStorage) {
                ((PlateStorage)currentTile).draw(g2, x, y);
            }
            else if (currentTile instanceof ServingCounter) {
                ((ServingCounter)currentTile).draw(g2, x, y);
            }
            // Default draw (untuk lantai/dinding biasa)
            else if (currentTile != null && currentTile.image != null) {
                g2.drawImage(currentTile.image, x, y, gp.tileSize, gp.tileSize, null);
            }

            col++;
            x += gp.tileSize;
            if(col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}