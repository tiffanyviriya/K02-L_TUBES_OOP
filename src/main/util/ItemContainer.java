package main.util;

import environment.item.Item;
import java.util.Stack;

/**
 * Kelas Generic kustom untuk menyimpan tumpukan item.
 * Menggunakan parameter <T> yang dibatasi (bounded) hanya untuk turunan Item.
 * Ini memenuhi syarat implementasi Custom Generics.
 *
 * @param <T> Tipe data item yang akan disimpan (misal: Plate, Ingredient, atau Item).
 */
public class ItemContainer<T extends Item> {

    // Menggunakan Stack internal untuk penyimpanan
    private Stack<T> containerStack;
    private int maxCapacity;

    public ItemContainer(int capacity) {
        this.containerStack = new Stack<>();
        this.maxCapacity = capacity;
    }

    // Constructor default
    public ItemContainer() {
        this(50); // Default kapasitas 50
    }

    /**
     * Menambahkan item ke dalam container (Generic Method).
     * @param item Objek bertipe T.
     * @return true jika berhasil, false jika penuh.
     */
    public boolean addItem(T item) {
        if (containerStack.size() < maxCapacity) {
            containerStack.push(item);
            return true;
        }
        return false;
    }

    /**
     * Mengambil item teratas dari container.
     * @return Objek bertipe T atau null jika kosong.
     */
    public T takeItem() {
        if (!isEmpty()) {
            return containerStack.pop();
        }
        return null;
    }

    /**
     * Melihat item teratas tanpa mengambilnya.
     */
    public T peekItem() {
        if (!isEmpty()) {
            return containerStack.peek();
        }
        return null;
    }

    public boolean isEmpty() {
        return containerStack.isEmpty();
    }

    public int size() {
        return containerStack.size();
    }

    public void clear() {
        containerStack.clear();
    }
}