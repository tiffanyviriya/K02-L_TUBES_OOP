package environment.food_related;

import environment.item.Item;
import environment.item.Preparable;
import main.util.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Ingredient extends Item implements Preparable {

    public IngredientState state;

    private BufferedImage imgRaw, imgChopped, imgCooked, imgBurned;

    /* Konstruktor untuk inisialisasi bahan makanan dengan nama dan state awal */
    public Ingredient(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.state = IngredientState.RAW;

        loadImages();
        updateImage();

        solidArea = new Rectangle(0, 0, 24, 24);
    }

    /* Memuat gambar bahan makanan untuk berbagai kondisi (mentah, potong, masak, gosong) */
    private void loadImages() {
        String rawPath = "/ingredients/" + name + "_raw.png";
        String choppedPath = "/ingredients/" + name + "_chopped.png";
        String cookedPath = "/ingredients/" + name + "_cooked.png";
        String burnedPath = "/ingredients/" + name + "_burned.png";

        if (name.equalsIgnoreCase("fish")) {
            rawPath = "/ingredients/fish_raw.png";
            choppedPath = "/ingredients/chopped_fish.png";
            cookedPath = "/ingredients/chopped_fish.png";
        }
        else if (name.equalsIgnoreCase("shrimp")) {
            rawPath = "/ingredients/shrimp.png";
            choppedPath = "/ingredients/chopped_shrimp.png";
            cookedPath = "/ingredients/chopped_shrimp.png";
        }
        else if (name.equalsIgnoreCase("rice")) {
            rawPath = "/ingredients/rice_grains.png";
            cookedPath = "/ingredients/rice.png";
        }
        else if (name.equalsIgnoreCase("nori")) {
            rawPath = "/ingredients/nori.png";
        }
        else if (name.equalsIgnoreCase("cucumber")) {
            rawPath = "/ingredients/cucumber.png";
            choppedPath = "/ingredients/chopped_cucumber.png";
        }

        try {
            imgRaw = loadImageSafe(rawPath);

            imgChopped = loadImageSafe(choppedPath);
            if (imgChopped == null) imgChopped = imgRaw;

            imgCooked = loadImageSafe(cookedPath);
            if (imgCooked == null) imgCooked = imgRaw;

            imgBurned = loadImageSafe(burnedPath);
            if (imgBurned == null) imgBurned = imgRaw;

        } catch (Exception e) {

        }
    }

    /* Membaca file gambar dengan aman tanpa melempar exception jika tidak ditemukan */
    private BufferedImage loadImageSafe(String path) {
        try {
            if (getClass().getResource(path) != null) {
                return ImageIO.read(getClass().getResourceAsStream(path));
            }
        } catch (IOException e) {
        }
        return null;
    }

    /* Memperbarui tampilan gambar bahan sesuai dengan state saat ini */
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

    /* Memeriksa apakah bahan dapat dipotong */
    @Override
    public boolean canBeChopped() {

        if (name.equalsIgnoreCase("nori") || name.equalsIgnoreCase("rice")) {
            return false;
        }
        return state == IngredientState.RAW;
    }

    /* Memeriksa apakah bahan dapat dimasak */
    @Override
    public boolean canBeCooked() {
        if (name.equalsIgnoreCase("nori")
                || name.equalsIgnoreCase("fish")
                || name.equalsIgnoreCase("cucumber")) return false;

        if (name.equalsIgnoreCase("rice")) return state == IngredientState.RAW;

        return state == IngredientState.CHOPPED || state == IngredientState.RAW;
    }

    /* Memeriksa apakah bahan dapat diletakkan di piring */
    @Override
    public boolean canBePlacedOnPlate() {
        return true;
    }

    /* Mengubah status bahan menjadi terpotong */
    @Override
    public void chop() {
        if (canBeChopped()) {
            state = IngredientState.CHOPPED;
            updateImage();
        }
    }

    /* Mengubah status bahan menjadi matang */
    @Override
    public void cook() {
        if(canBeCooked()){
            state = IngredientState.COOKED;
            updateImage();
        }
    }

    /* Mengubah status bahan menjadi gosong */
    @Override
    public void burn() {
        state = IngredientState.BURNED;
        updateImage();
    }
}