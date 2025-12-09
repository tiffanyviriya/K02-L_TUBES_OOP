package environment;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Plate extends Item{
    public PlateState plateState;

    public Set<Preparable> itemOnPlate = new HashSet<>();

    public Plate(GamePanel gp) {
        super(gp);
        plateState = PlateState.CLEAN;

        solidArea = new Rectangle(0,0, 24,24);

        loadPlateImage();
    }

    private void loadPlateImage() {
        try {
            if(plateState == PlateState.CLEAN){
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_clean.png"));
            } else {
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_dirty.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem (Preparable preparable){
        itemOnPlate.add(preparable);
    }

    @Override
    public void draw(Graphics2D g2){
        super.draw(g2);
        for(Preparable p : itemOnPlate){
            Item preparables = (Item) p;
            preparables.draw(g2);
        }
    }
}
