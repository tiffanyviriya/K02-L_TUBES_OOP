package environment;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class FryingPan extends KitchenUtensil {

    public FryingPan(GamePanel gp) {
        super(gp);
        name = "Frying Pan";
        try {
             // Pastikan kamu punya gambar ini atau ganti pathnya
            image = ImageIO.read(getClass().getResourceAsStream("/kitchen/pan.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canAccept(Ingredient ingredient) {
        // Wajan menerima Daging, Ikan, Ayam, dll.
        // Harus sudah dipotong (CHOPPED) biasanya untuk daging
        boolean validIngredient = ingredient.name.equalsIgnoreCase("Meat") 
                               || ingredient.name.equalsIgnoreCase("Fish")
                               || ingredient.name.equalsIgnoreCase("Shrimp");
                               
        return validIngredient && ingredient.state == IngredientState.CHOPPED;
    }
}
