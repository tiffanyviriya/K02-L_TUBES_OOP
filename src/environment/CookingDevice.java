package environment;

public interface CookingDevice {
    boolean isPortable();
    int capacity();
    boolean canAccept(Ingredient ingredient);
    void addIngredient(Ingredient ingredient);
    void startCooking();
}
