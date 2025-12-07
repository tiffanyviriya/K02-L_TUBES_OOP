package environment;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();

    void chop();
    void cook();
    void burn(); // Tambahan untuk menghandle gosong
}
