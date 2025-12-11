package environment;

import main.GamePanel;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;

public class KitchenUtensil extends Item {

    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    public int cookingProgress = 0;
    
    // Waktu masak (sesuaikan jika terlalu cepat/lambat)
    public final int TIME_TO_COOK = 300; 
    public final int TIME_TO_BURN = 600; 
    
    public boolean isCooked = false;
    public boolean isBurned = false;

    public KitchenUtensil(GamePanel gp, String name) {
        super(gp);
        this.name = name;
        this.collision = true;
        loadUtensilImage();
    }

    private void loadUtensilImage() {
        try {
            // LOGIKA BARU: Load gambar sesuai nama (Pot atau Pan)
            // Pastikan path ini sesuai dengan struktur folder 'res' kamu
            if (name.equalsIgnoreCase("Pan")) {
                image = ImageIO.read(getClass().getResourceAsStream("/Sprites_Overcooked/Sprites_Utensils/Frying_Pan.png"));
            } else {
                // Default ke Pot
                image = ImageIO.read(getClass().getResourceAsStream("/Sprites_Overcooked/Sprites_Utensils/Boiling_Pot.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ... (Sisa method addIngredient, cook, serveToPlate, draw biarkan sama seperti sebelumnya)
    // Pastikan copy method-method logic tersebut dari jawaban saya sebelumnya jika belum ada.
}
