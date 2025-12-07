package environment;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Ingredient extends Item implements Preparable{

    public IngredientState state;

    private BufferedImage imgRaw, imgChopped, imgCooked, imgBurned;

    public Ingredient(GamePanel gp, String name) {
        super(gp);
        this.name = name;

        this.state = IngredientState.RAW;

        loadImages();
        updateImage();
    }

    private void loadImages() {
        try {
            imgRaw = ImageIO.read(getClass().getResourceAsStream("/ingredients/" + name + "_raw.png"));

            try {
                imgChopped = ImageIO.read(getClass().getResourceAsStream("/ingredients/" + name + "_chopped.png"));
            } catch (Exception e) { imgChopped = imgRaw; }

            try {
                imgCooked = ImageIO.read(getClass().getResourceAsStream("/ingredients/" + name + "_cooked.png"));
            } catch (Exception e) { imgCooked = imgRaw; }

            try {
                imgBurned = ImageIO.read(getClass().getResourceAsStream("/ingredients/" + name + "_burned.png"));
            } catch (Exception e) { imgBurned = imgRaw; }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat gambar untuk: " + name);
        }
    }

    public void updateImage() {
        switch (state) {
            case RAW:
                image = imgRaw;
                break;
            case CHOPPED:
                image = imgChopped;
                break;
            case COOKED:
                image = imgCooked;
                break;
            case BURNED:
                image = imgBurned;
                break;
            default:
                image = imgRaw;
                break;
        }
    }

    @Override
    public boolean canBeChopped() {
        return state == IngredientState.RAW;
    }

    @Override
    public boolean canBeCooked() {
        return state == IngredientState.RAW || state == IngredientState.CHOPPED;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return true;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            state = IngredientState.CHOPPED;
            updateImage();
            //Untuk Debug
            System.out.println(name + " berhasil dipotong!");
        }
    }

    @Override
    public void cook() {
        if(canBeCooked()){
            state = IngredientState.COOKED;
            updateImage();
            //Untuk Debug
            System.out.println(name + " matang!");
        }
    }

    @Override
    public void burn() {
        state = IngredientState.BURNED;
        updateImage();
        //Untuk Debug
        System.out.println(name + " gosong!");
    }
}
