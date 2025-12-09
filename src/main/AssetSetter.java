package main;

import environment.BoilingPot;
import environment.FryingPan;

public class AssetSetter {

    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        // --- CONTOH UNTUK MAP A (SUSHI) ---
        // Berdasarkan PDF hal 19, Stove (R) ada di kolom 11, 12, 13 pada Baris 1
        // Koordinat array (index mulai dari 0): col 10, 11, 12 | row 0
        
        // Spawn Boiling Pot 1
        BoilingPot pot1 = new BoilingPot(gp);
        gp.itemM.addItem(pot1, 11 * gp.tileSize, 3 * gp.tileSize); 

        // Spawn Boiling Pot 2
        BoilingPot pot2 = new BoilingPot(gp);
        gp.itemM.addItem(pot2, 12 * gp.tileSize, 3 * gp.tileSize);

        // Spawn Frying Pan
        FryingPan pan1 = new FryingPan(gp);
        gp.itemM.addItem(pan1, 13 * gp.tileSize, 3 * gp.tileSize);
        
        // --- NOTE POSISI ---
        // Sesuaikan angka 10, 11, 12 dengan koordinat 'R' (Cooking Station) 
        // di file map.txt yang kamu load.
    }
}
