package tile;

import environment.entity.Entity;
import environment.item.Plate;
import main.util.GamePanel;
import main.util.ItemContainer;

import javax.imageio.ImageIO;
import java.awt.*;

public class PlateStorage extends Tile {

    public ItemContainer<Plate> plateOnStorage = new ItemContainer<>();

    private final int STARTING_PLATES = 4;

    /* Konstruktor untuk inisialisasi penyimpanan piring dan memuat piring awal */
    public PlateStorage(GamePanel gp) {
        super(gp);
        this.collision = true;

        loadStorageImage();
        loadPlates();
    }

    /* Memuat gambar visual untuk rak penyimpanan piring */
    private void loadStorageImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/stations/plate-storage.png"));
        } catch (Exception e) {

        }
    }

    /* Mengisi penyimpanan dengan jumlah piring bersih awal saat permainan dimulai */
    private void loadPlates() {
        for (int i = 0; i < STARTING_PLATES; i++) {
            plateOnStorage.addItem(new Plate(gp));
        }
    }

    /* Menyimpan piring (biasanya piring kotor yang dikembalikan) ke dalam rak */
    public void storePlate(Plate plate) {
        plateOnStorage.addItem(plate);
    }

    /* Menangani interaksi pemain mengambil piring dari rak jika tangan kosong */
    @Override
    public void interact(Entity player) {
        if (player.inventory == null) {
            if (!plateOnStorage.isEmpty()) {
                player.inventory = plateOnStorage.takeItem();
            }
        }
    }

    /* Menggambar rak penyimpanan dan jumlah piring yang tersedia */
    public void draw(Graphics2D g2, int x, int y) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        if (!plateOnStorage.isEmpty()) {
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