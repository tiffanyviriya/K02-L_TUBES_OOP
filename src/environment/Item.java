package environment;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Item {
    String name;
    BufferedImage image;
    public Rectangle solidArea = new Rectangle(0, 0, 40, 40);

    boolean isHold;
}
