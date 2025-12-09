package environment;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class BoilingPot extends KitchenUtensil {

    public BoilingPot(GamePanel gp) {
        super(gp);
        name = "Boiling Pot";
        try {
            // Pastikan kamu punya gambar ini atau ganti pathnya
            image = ImageIO.read(getClass().getResourceAsStream("/kitchen/pot.png")); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canAccept(Ingredient ingredient) {
        // Panci hanya menerima bahan tertentu (misal: Beras atau Pasta)
        // Dan bahan harus status RAW atau CHOPPED
        boolean validIngredient = ingredient.name.equalsIgnoreCase("Rice") 
                               || ingredient.name.equalsIgnoreCase("Pasta");
        
        return validIngredient && (ingredient.state == IngredientState.RAW || ingredient.state == IngredientState.CHOPPED);
    }
}
