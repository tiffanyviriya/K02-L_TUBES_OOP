package environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Ingredient extends Item{

    public Ingredient(String name){
        super.name = name;
        super.isHold = false;
    }

    public void getImage(String name) throws IOException {
        super.image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + name + ".png"));
    }
}
