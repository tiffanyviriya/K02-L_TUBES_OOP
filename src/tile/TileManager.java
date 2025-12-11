package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import environment.KitchenUtensil; // Jangan lupa import ini
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
        
        // TAMBAHAN: Panggil method ini untuk menaruh panci
        setupDefaultUtensils();
    }

    // ... (method loadMap dan setupTiles biarkan sama) ...

    // TAMBAHAN METHOD BARU
    public void setupDefaultUtensils() {
        // Koordinat ini didapat dari melihat map4.txt
        // Row 2 (Baris ke-3 dari atas)
        // Col 12, 13, 14 adalah Cooking Station (angka 4)
        
        // Taruh Pot 1
        placeUtensilOnStation(12, 2, "Pot");
        
        // Taruh Pot 2
        placeUtensilOnStation(13, 2, "Pot");
        
        // Taruh Pan 1
        placeUtensilOnStation(14, 2, "Pan");
    }

    // Helper untuk menaruh utensil secara aman
    private void placeUtensilOnStation(int col, int row, String utensilName) {
        // Cek dulu apakah tile di koordinat itu benar-benar CookingStation
        if (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            if (worldTiles[col][row] instanceof CookingStation) {
                CookingStation cs = (CookingStation) worldTiles[col][row];
                
                // Spawn Utensil baru dan taruh di station
                cs.utensilOnStation = new KitchenUtensil(gp, utensilName);
                
                System.out.println("Spawned " + utensilName + " at [" + col + "," + row + "]");
            }
        }
    }
    
    // ... (method draw dan update biarkan sama) ...
}
