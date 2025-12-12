package tile;

import environment.entity.Entity;
import environment.item.Plate;
import main.util.GamePanel;
import main.util.ItemContainer; // Import kelas generic baru

import javax.imageio.ImageIO;
import java.awt.*;

public class PlateStorage extends Tile {

    // [CUSTOM GENERICS] Mengganti Stack<Plate> dengan ItemContainer<Plate>
    public ItemContainer<Plate> plateOnStorage = new ItemContainer<>();

    private final int STARTING_PLATES = 4; // Jumlah piring awal

    public PlateStorage(GamePanel gp) {
        super(gp);
        this.collision = true;

        loadStorageImage();
        loadPlates();
    }

    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/plate-storage.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlates() {
        // Isi container dengan piring bersih di awal game
        for (int i = 0; i < STARTING_PLATES; i++) {
            // [CUSTOM GENERICS] Menggunakan method addItem
            plateOnStorage.addItem(new Plate(gp));
        }
    }

    public void storePlate(Plate plate) {
        // [CUSTOM GENERICS] Menggunakan method addItem
        plateOnStorage.addItem(plate);
        System.out.println("PlateStorage: Piring diterima. Total sekarang: " + plateOnStorage.size());
    }

    @Override
    public void interact(Entity player) {
        if (player.inventory == null) {
            if (!plateOnStorage.isEmpty()) {

                // [CUSTOM GENERICS] Mengambil item menggunakan method custom takeItem()
                // Tidak perlu casting karena T sudah didefinisikan sebagai Plate
                player.inventory = plateOnStorage.takeItem();

                System.out.println("Mengambil piring. Sisa: " + plateOnStorage.size());
            } else {
                System.out.println("Storage kosong! Tunggu piring kembali.");
            }
        } else {
            System.out.println("Tangan penuh!");
        }
    }

    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        if (!plateOnStorage.isEmpty()) {
            // [CUSTOM GENERICS] Menggunakan peekItem()
            Plate topPlate = plateOnStorage.peekItem();

            int plateX = x + 12;
            int plateY = y + 12;

            if (topPlate != null && topPlate.image != null) {
                g2.drawImage(topPlate.image, plateX, plateY, gp.itemSize, gp.itemSize, null);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(String.valueOf(plateOnStorage.size()), x + 35, y + 45);
        }
    }
}