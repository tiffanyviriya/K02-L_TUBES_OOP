package main.util;

import environment.item.Item;
import java.util.Stack;

/* Kelas Generic kustom untuk menyimpan tumpukan item dengan batasan tipe turunan Item */
public class ItemContainer<T extends Item> {

    private Stack<T> containerStack;
    private int maxCapacity;

    /* Menginisialisasi container dengan kapasitas spesifik */
    public ItemContainer(int capacity) {
        this.containerStack = new Stack<>();
        this.maxCapacity = capacity;
    }

    /* Menginisialisasi container dengan kapasitas default 50 */
    public ItemContainer() {
        this(50);
    }

    /* Menambahkan item ke dalam container jika belum penuh dan mengembalikan true jika berhasil */
    public boolean addItem(T item) {
        if (containerStack.size() < maxCapacity) {
            containerStack.push(item);
            return true;
        }
        return false;
    }

    /* Mengambil dan menghapus item teratas dari container atau null jika kosong */
    public T takeItem() {
        if (!isEmpty()) {
            return containerStack.pop();
        }
        return null;
    }

    /* Melihat item teratas tanpa menghapusnya dari container */
    public T peekItem() {
        if (!isEmpty()) {
            return containerStack.peek();
        }
        return null;
    }

    /* Mengecek apakah container kosong */
    public boolean isEmpty() {
        return containerStack.isEmpty();
    }

    /* Mengembalikan jumlah item yang ada di dalam container */
    public int size() {
        return containerStack.size();
    }

    /* Menghapus semua item dari container */
    public void clear() {
        containerStack.clear();
    }
}