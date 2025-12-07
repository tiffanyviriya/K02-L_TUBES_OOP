package environment;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Ingredient extends Item implements Preparable{

    // Menggunakan Enum yang terpisah (IngredientState.java)
    public IngredientState state;

    // Menyimpan referensi gambar untuk tiap state agar performa lebih cepat
    private BufferedImage imgRaw, imgChopped, imgCooked, imgBurned;

    public Ingredient(GamePanel gp, String name) {
        super(gp);
        this.name = name;

        // Default state saat bahan baru dibuat adalah RAW (Mentah)
        this.state = IngredientState.RAW;

        loadImages();
        updateImage();
    }

    private void loadImages() {
        // Memuat gambar berdasarkan nama bahan (misal: "potato")
        // Pastikan file gambar ada di folder: /res/ingredients/
        try {
            imgRaw = ImageIO.read(getClass().getResourceAsStream("/ingredients/" + name + "_raw.png"));

            // Menggunakan try-catch terpisah (fallback) jika gambar variasi belum ada
            // Jadi jika "potato_chopped.png" belum digambar, gamenya tidak crash, tapi pakai gambar raw dulu.
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

    // Mengubah gambar aktif sesuai state saat ini
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

    // --- IMPLEMENTASI INTERFACE PREPARABLE ---

    @Override
    public boolean canBeChopped() {
        // Bahan hanya bisa dipotong jika masih MENTAH
        return state == IngredientState.RAW;
    }

    @Override
    public boolean canBeCooked() {
        // Bahan bisa dimasak jika RAW (misal beras) atau CHOPPED (misal daging potong)
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
            System.out.println(name + " berhasil dipotong!");
        }
    }

    @Override
    public void cook() {
        // Dipanggil saat proses memasak selesai
        state = IngredientState.COOKED;
        updateImage();
        System.out.println(name + " matang!");
    }

    @Override
    public void burn() {
        // Dipanggil saat proses memasak terlalu lama
        state = IngredientState.BURNED;
        updateImage();
        System.out.println(name + " gosong!");
    }
}
