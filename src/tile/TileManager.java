package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {

    GamePanel gp;
    // GANTI: Kita pakai array 2D Tile untuk merepresentasikan dunia nyata
    public Tile[][] worldTiles;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        worldTiles = new Tile[gp.maxScreenCol][gp.maxScreenRow];

        loadMap("/maps/map4.txt"); // Load angka dulu
        setupTiles(); // Baru buat objek Tile berdasarkan angka
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

    // Method baru untuk inisialisasi objek Tile yang UNIK per koordinat
    public void setupTiles() {
        int col = 0;
        int row = 0;

        while(col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileType = mapTileNum[col][row];

            // Factory sederhana berdasarkan angka di map.txt
            switch (tileType) {
                case 0: // Floor
                    worldTiles[col][row] = new Tile(gp);
                    setupImage(worldTiles[col][row], "/tiles/floor_tile.png", false);
                    break;
                case 1: // Wall
                    worldTiles[col][row] = new Tile(gp);
                    setupImage(worldTiles[col][row], "/tiles/wall_tile.png", true);
                    break;
                case 2: // CUTTING STATION (Misal angka 2 di map adalah cutting station)
                    worldTiles[col][row] = new CuttingStation(gp);
                    // Gambar diload di constructor CuttingStation
                    break;
                case 3: // INGREDIENT STORAGE (Misal angka 3)
                    worldTiles[col][row] = new IngredientStorage(gp, "cucumber");
                    break;
                case 4: // Cooking Station
                    worldTiles[col][row] = new CookingStation(gp);
                    break;
                case 5:
                    worldTiles[col][row] = new ServingCounter(gp);
                    break;
                case 6:
                    worldTiles[col][row] = new PlateStorage(gp);
                    break;
                case 7:
                    worldTiles[col][row] = new AssemblyStation(gp);
                    break;
                default: // Default floor
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

    // Helper load image
    private void setupImage(Tile tile, String path, boolean collision) {
        try {
            tile.image = ImageIO.read(getClass().getResourceAsStream(path));
            tile.collision = collision;
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Di class TileManager
public void update() {
    for (int col = 0; col < gp.maxScreenCol; col++) {
        for (int row = 0; row < gp.maxScreenRow; row++) {
            
            // Jika tile tersebut adalah CookingStation, panggil update()-nya
            if (worldTiles[col][row] instanceof CookingStation) {
                ((CookingStation) worldTiles[col][row]).update();
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

            // Jika CuttingStation, panggil draw khusus (biar ada progress bar)
            if (currentTile instanceof CuttingStation) {
                ((CuttingStation)currentTile).draw(g2, x, y);
            }
            else if (currentTile instanceof CookingStation) {
                ((CookingStation)currentTile).draw(g2, x, y);
            }
            // 3. TAMBAHKAN INI: Cek Assembly Station
            else if (currentTile instanceof AssemblyStation) {
                ((AssemblyStation)currentTile).draw(g2, x, y);
            }
            // 4. Default Tile (Lantai/Tembok biasa)
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
