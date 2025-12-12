package environment.item;

import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Plate extends Item {
    public PlateState plateState;
    public Set<Preparable> itemOnPlate = new HashSet<>();

    public Plate(GamePanel gp) {
        super(gp);
        plateState = PlateState.CLEAN;
        solidArea = new Rectangle(0,0, 24,24);

        updateImage();
    }

    public void updateImage() {
        try {
            if (plateState == PlateState.CLEAN) {
                // Pastikan nama file sesuai dengan yang ada di folder res
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_clean.png"));
            } else {
                image = ImageIO.read(getClass().getResourceAsStream("/utensils/plate_dirty.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(Preparable preparable) {
        itemOnPlate.add(preparable);
    }

    public void draw(Graphics2D g2, int x, int y) {
        super.draw(g2, x, y);
        // Hanya gambar makanan jika piring BERSIH
        if (plateState == PlateState.CLEAN) {
            for (Preparable p : itemOnPlate) {
                Item preparables = (Item) p;
                g2.drawImage(preparables.image, x, y, gp.itemSize, gp.itemSize, null);
            }
        }
    }
}