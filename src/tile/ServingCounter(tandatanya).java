// Di method interact() ServingCounter
if (itemOnTop == null && player.inventory != null) {
// Player menaruh piring
boolean success = gp.orderM.checkServing(player.inventory);

    if (success) {
// Makanan diterima: Hapus item dari tangan player
player.inventory = null;
        // Piring kotor nanti akan spawn di Washing Station (Sesuai spec M2)
        } else {
        // Makanan salah: Player tetap pegang (atau bisa dibikin hilang + piring jadi kotor)
        // Sesuai spec: "Jika tidak cocok, order tidak dihapus".
        }
        }