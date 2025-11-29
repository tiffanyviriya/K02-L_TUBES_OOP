package tile;

import environment.Ingredient;

public class IngredientStation {
    String givenIngredient;

    public IngredientStation (String ingredientName){
        this.givenIngredient = ingredientName;
    }

    public void Instantiate(){
        Ingredient ingredient = new Ingredient(givenIngredient);


    }

    public void draw(){}
}
