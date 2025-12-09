package tile;

import environment.Entity;
import environment.Ingredient;
import environment.Plate;
import environment.PlateState;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.util.Stack;

public class PlateStorage extends Tile{
    private int numOfPlate = 2;
    public Stack<Plate> plateOnStorage = new Stack<>();

    public PlateStorage(GamePanel gp) {
        super(gp);
        this.collision = true;

        loadStorageImage();
        loadPlates();
    }

    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/plate_storage.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlates(){
        for (int i = 0; i < numOfPlate;i++){
            plateOnStorage.add(new Plate(gp));
        }
    }

    public void storePlate(Plate plate){
        plate.plateState = PlateState.DIRTY;
        plateOnStorage.push(plate);
    }

    public Plate takePlate(){
        Plate takenPlate = plateOnStorage.pop();
        return takenPlate;
    }

    public void interact(Entity player){
        if (player.inventory == null) {
            Plate takenPlate = takePlate();
            player.inventory = takenPlate;
            System.out.println("Player mengambil piring.");
        } else {
            System.out.println("Player Tangan penuh! Tidak bisa mengambil item.");
        }
    }
}
