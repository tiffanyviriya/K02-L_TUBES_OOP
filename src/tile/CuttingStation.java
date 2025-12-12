package tile;

import environment.entity.Entity;
import environment.food_related.Ingredient;
import environment.food_related.IngredientState;
import environment.item.Item;
import main.util.GamePanel;
import environment.entity.PlayerState;
import environment.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class CuttingStation extends Tile {

    // Item yang ditaruh di atas station
    public Item itemOnTop = null;

    // Progress bar variable
    private int currentProgress = 0;
    private final int TIME_TO_CUT = 180; // 3 detik x 60 FPS

    public CuttingStation(GamePanel gp) {
        super(gp);
        this.collision = true; // Player tidak bisa menembus meja
        loadStationImage();
    }

    private void loadStationImage() {
        try {
            // GANTI baris ini:
            // image = ImageIO.read(getClass().getResourceAsStream("/tiles/OOPtile.png"));

            // MENJADI arah ke file gambar baru:
            image = ImageIO.read(getClass().getResourceAsStream("/stations/cutting_station.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method ini dipanggil terus menerus selama tombol interact ditekan
    public void interact(Entity player) {

        // KASUS 1: Menaruh Item (Syarat: Meja kosong, Tangan player isi)
        if (itemOnTop == null && player.inventory != null) {
            itemOnTop = player.inventory;
            player.inventory = null;
            System.out.println("Menaruh " + itemOnTop.name + " di Cutting Station");
            return;
        }

        // KASUS 2: Interaksi dengan Item di Meja
        if (itemOnTop != null) {

            // Cek apakah item adalah ingredient mentah (RAW)
            boolean isRawIngredient = false;
            if (itemOnTop instanceof Ingredient) {
                if (((Ingredient) itemOnTop).state == IngredientState.RAW) {
                    isRawIngredient = true;
                }
            }

            // A. LOGIKA MEMOTONG (Syarat: Item RAW, Tangan Kosong)
            if (isRawIngredient && player.inventory == null) {
                if (player instanceof Player) {
                    ((Player) player).playerState = PlayerState.BUSY;
                }

                // Tambah progress
                currentProgress++;

                // Debug log (opsional, muncul tiap 1 detik)
                if (currentProgress % 60 == 0) {
                    System.out.println("Memotong... " + (currentProgress/60) + " detik");
                }

                // Cek apakah selesai
                if (currentProgress >= TIME_TO_CUT) {
                    ((Ingredient) itemOnTop).chop();
                    currentProgress = 0;

                    // CASTING LAGI DISINI
                    if (player instanceof Player) {
                        ((Player) player).playerState = PlayerState.IDLE;
                    }
                    System.out.println("Selesai memotong!");
                }
            }

            // B. LOGIKA MENGAMBIL (Syarat: Tangan Kosong, Bukan sedang memotong/Bahan sudah jadi)
            // Kita tambahkan pengecekan: Jika tombol baru saja ditekan (bukan ditahan) atau item sudah jadi
            else if (player.inventory == null) {
                // Jika item sudah CHOPPED atau bukan Ingredient, ambil.
                // Jika item masih RAW tapi player ingin ambil (batal potong),
                // ini agak tricky kalau pakai tombol yang sama.
                // Sesuai spec: Interact untuk memotong.
                // Kita asumsikan kalau item sudah CHOPPED baru bisa diambil,
                // ATAU player harus lepas tombol dulu baru tekan lagi untuk ambil (perlu logika KeyHandler advanced).

                // Simpelnya: Kalau sudah chopped, ambil.
                if (!isRawIngredient) {
                    player.inventory = itemOnTop;
                    itemOnTop = null;
                    currentProgress = 0;
                    System.out.println("Mengambil hasil potongan");
                }
            }
        }
    }

    // Method visualisasi progress bar
    public void draw(Graphics2D g2, int x, int y) {
        // Gambar Meja
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);

        // Gambar Item di atas meja
        if (itemOnTop != null) {
            g2.drawImage(itemOnTop.image, x + 12, y + 12, gp.itemSize, gp.itemSize, null);

            // Gambar Progress Bar (Hanya jika ada progress dan belum selesai)
            if (currentProgress > 0 && currentProgress < TIME_TO_CUT) {
                int barWidth = 32;
                int barHeight = 6;
                int screenX = x + 8;
                int screenY = y - 10;

                // Background Merah
                g2.setColor(Color.RED);
                g2.fillRect(screenX, screenY, barWidth, barHeight);

                // Foreground Hijau (Sesuai persentase)
                int greenBar = (int) (((double)currentProgress / TIME_TO_CUT) * barWidth);
                g2.setColor(Color.GREEN);
                g2.fillRect(screenX, screenY, greenBar, barHeight);

                // Border Hitam
                g2.setColor(Color.BLACK);
                g2.drawRect(screenX, screenY, barWidth, barHeight);
            }
        }
    }
}
