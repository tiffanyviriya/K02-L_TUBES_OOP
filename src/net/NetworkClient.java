package net;

import main.util.GamePanel;
import main.util.GameState;
import java.io.*;
import java.net.*;

/**
 * NetworkClient yang mendukung koneksi default dan kustom.
 * Sinkron dengan GamePanel dan OrderManager terbaru.
 */
public class NetworkClient implements Runnable {
    private GamePanel gp;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Nilai default jika tidak dipassing dari UI
    private String serverIp = "localhost";
    private int port = 6741;

    public int myPlayerId = -1;
    public boolean isConnected = false;

    public NetworkClient(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Method connect tanpa argumen (digunakan oleh DifficultyScene Anda).
     * Menggunakan default localhost:12345
     */
    public void connect() {
        connect(this.serverIp, this.port);
    }

    /**
     * Method connect dengan argumen jika ingin koneksi ke IP spesifik.
     */
    public void connect(String ip, int port) {
        if (isConnected) return;
        this.serverIp = ip;
        this.port = port;

        new Thread(() -> {
            try {
                socket = new Socket(serverIp, this.port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;

                System.out.println("Terhubung ke server " + serverIp + ":" + port);
                run(); // Jalankan loop pendengar

            } catch (IOException e) {
                System.out.println("Gagal connect ke server. Pastikan GameServer sudah dijalankan.");
                // e.printStackTrace();
                isConnected = false;
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                processMessage(msg);
            }
        } catch (IOException e) {
            System.out.println("Koneksi terputus.");
            isConnected = false;
        }
    }

    private void processMessage(String msg) {
        // Format Pesan: COMMAND|DATA1|DATA2...
        String[] parts = msg.split("\\|");
        String header = parts[0];

        switch (header) {
            case "ASSIGN_ID":
                myPlayerId = Integer.parseInt(parts[1]);
                System.out.println("ID Saya dari Server: " + myPlayerId);
                break;

            case "START_GAME":
                System.out.println("Server memulai permainan!");
                gp.changeGameState(GameState.PLAYING);
                break;

            case "ORDER_SPAWN":
                // Sinkronisasi Order: ORDER_SPAWN|RecipeName|Duration
                if (gp.orderM != null) {
                    gp.orderM.applyOrderSpawn(parts[1], Integer.parseInt(parts[2]));
                }
                break;

            case "ORDER_REMOVE":
                // Sinkronisasi Penghapusan: ORDER_REMOVE|Index
                if (gp.orderM != null) {
                    gp.orderM.applyOrderRemove(Integer.parseInt(parts[1]));
                }
                break;

            case "INPUT":
                // Sinkronisasi Pergerakan: INPUT|SENDER_ID|KEY|PRESSED
                int senderId = Integer.parseInt(parts[1]);
                if (senderId != myPlayerId) {
                    String key = parts[2];
                    boolean isPressed = Boolean.parseBoolean(parts[3]);
                    updateRemoteBuffer(key, isPressed);
                }
                break;

            case "LEAVE_ACK":
                System.out.println("Keluar dari Lobby dikonfirmasi server.");
                gp.changeGameState(GameState.MAINMENU);
                closeConnection();
                break;
        }
    }

    private void updateRemoteBuffer(String key, boolean pressed) {
        if (gp.remoteInputBuffer == null) return;

        switch (key) {
            case "W": gp.remoteInputBuffer.up = pressed; break;
            case "S": gp.remoteInputBuffer.down = pressed; break;
            case "A": gp.remoteInputBuffer.left = pressed; break;
            case "D": gp.remoteInputBuffer.right = pressed; break;
            case "V": gp.remoteInputBuffer.action = pressed; break;
            case "C": gp.remoteInputBuffer.interact = pressed; break;
        }
    }

    public void sendInput(String key, boolean pressed) {
        if (isConnected && out != null) {
            out.println("INPUT|" + myPlayerId + "|" + key + "|" + pressed);
        }
    }

    public void sendRequestLeave() {
        if (isConnected && out != null) {
            out.println("REQUEST_LEAVE_LOBBY");
        }
    }

    public void closeConnection() {
        try {
            isConnected = false;
            if (socket != null) socket.close();
        } catch (IOException e) {}
    }
}