package environment;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Ingredient extends Item implements Preparable {

    public IngredientState state;

    private BufferedImage imgRaw, imgChopped, imgCooked, imgBurned;

    public Ingredient(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.state = IngredientState.RAW; // Default state

        loadImages();
        updateImage();

        // Sesuaikan ukuran hitbox
        solidArea = new Rectangle(0, 0, 24, 24);
    }

    private void loadImages() {
        // 1. Tentukan Path Default (Pola Lama)
        String rawPath = "/ingredients/" + name + "_raw.png";
        String choppedPath = "/ingredients/" + name + "_chopped.png";
        String cookedPath = "/ingredients/" + name + "_cooked.png";
        String burnedPath = "/ingredients/" + name + "_burned.png";

        // 2. Override Path untuk Bahan Khusus (Sesuai Resource Anda)
        if (name.equalsIgnoreCase("fish")) {
            rawPath = "/Sprites_Overcooked/Sprites_Ingredients/fish.png";
            choppedPath = "/Sprites_Overcooked/Sprites_Ingredients/chopped_fish.png";
            // Jika tidak ada gambar cooked, pakai raw/chopped sementara
            cookedPath = "/Sprites_Overcooked/Sprites_Ingredients/chopped_fish.png";
        }
        else if (name.equalsIgnoreCase("shrimp")) {
            rawPath = "/Sprites_Overcooked/Sprites_Ingredients/shrimp.png";
            choppedPath = "/Sprites_Overcooked/Sprites_Ingredients/chopped_shrimp.png";
            // Udang biasanya berubah warna jadi oranye saat masak,
            // jika belum ada gambarnya, bisa pakai chopped dulu.
            cookedPath = "/Sprites_Overcooked/Sprites_Ingredients/chopped_shrimp.png";
        }
        else if (name.equalsIgnoreCase("rice")) {
            // Beras mentah biasanya butiran (grains)
            rawPath = "/Sprites_Overcooked/Sprites_Ingredients/rice_grains.png";
            // Beras matang jadi nasi di mangkuk/tumpukan
            cookedPath = "/Sprites_Overcooked/Sprites_Ingredients/rice.png";
        }
        else if (name.equalsIgnoreCase("nori")) {
            rawPath = "/Sprites_Overcooked/Sprites_Ingredients/nori.png";
        }

        // 3. Load Gambar dengan Error Handling (Agar tidak invisible jika gagal)
        try {
            // Load RAW (Wajib ada)
            imgRaw = loadImageSafe(rawPath);
            if (imgRaw == null) {
                System.out.println("CRITICAL: Gagal load gambar utama untuk " + name + " di path: " + rawPath);
                // Fallback ke gambar error atau kotak kosong jika perlu
            }

            // Load State Lain (Fallback ke imgRaw jika tidak ada)
            imgChopped = loadImageSafe(choppedPath);
            if (imgChopped == null) imgChopped = imgRaw;

            imgCooked = loadImageSafe(cookedPath);
            if (imgCooked == null) imgCooked = imgRaw;

            imgBurned = loadImageSafe(burnedPath);
            if (imgBurned == null) imgBurned = imgRaw;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method untuk load gambar tanpa crash
    private BufferedImage loadImageSafe(String path) {
        try {
            if (getClass().getResource(path) != null) {
                return ImageIO.read(getClass().getResourceAsStream(path));
            }
        } catch (IOException e) {
            // Ignore error, return null
        }
        return null;
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
        // Logic khusus: Nori & Rice biasanya tidak dipotong
        if (name.equalsIgnoreCase("nori") || name.equalsIgnoreCase("rice")) {
            return false;
        }
        return state == IngredientState.RAW;
    }

    @Override
    public boolean canBeCooked() {
        // Logic khusus: Nori biasanya tidak dimasak
        if (name.equalsIgnoreCase("nori")) return false;

        // Rice harus dimasak dari mentah (RAW), bahan lain biasanya dari potong (CHOPPED)
        if (name.equalsIgnoreCase("rice")) return state == IngredientState.RAW;

        return state == IngredientState.CHOPPED || state == IngredientState.RAW;
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
            System.out.println(name + " berhasil dipotong!");
        } else {
            System.out.println(name + " tidak bisa dipotong!");
        }
    }

    @Override
    public void cook() {
        if(canBeCooked()){
            state = IngredientState.COOKED;
            updateImage();
            System.out.println(name + " matang!");
        }
    }

    @Override
    public void burn() {
        state = IngredientState.BURNED;
        updateImage();
        System.out.println(name + " gosong!");
    }
}