package net;

import main.util.GamePanel;
import main.util.GameState;
import java.io.*;
import java.net.*;

public class NetworkClient implements Runnable {
    private GamePanel gp;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Default localhost, bisa diganti IP LAN
    private String serverIp = "localhost";
    private int port = 6741;

    public int myPlayerId = -1; // 0 = Player 1, 1 = Player 2
    public boolean isConnected = false;

    public NetworkClient(GamePanel gp) {
        this.gp = gp;
    }

    public void connect() {
        if (isConnected) return; // Jangan connect kalau sudah connect

        try {
            socket = new Socket(serverIp, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            isConnected = true;

            System.out.println("Terhubung ke server!");
            new Thread(this).start(); // Start listening thread

        } catch (IOException e) {
            System.out.println("Gagal connect ke server. Pastikan Server jalan.");
            e.printStackTrace();
        }
    }

    // Kirim input: INPUT:PLAYER_ID:KEY:PRESSED
    public void sendInput(String key, boolean pressed) {
        if (out != null) {
            out.println("INPUT:" + myPlayerId + ":" + key + ":" + pressed);
        }
    }

    // Kirim perintah biasa
    public void sendCommand(String cmd) {
        if (out != null) out.println(cmd);
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
        // Parsing pesan sederhana format "HEADER:DATA:DATA"
        String[] parts = msg.split(":");
        String header = parts[0];

        if (header.equals("ASSIGN_ID")) {
            myPlayerId = Integer.parseInt(parts[1]);
            System.out.println("ID Saya: " + myPlayerId);
        }
        else if (header.equals("LOBBY_FULL")) {
            System.out.println("Pemain kedua masuk.");
        }
        else if (header.equals("START_GAME")) {
            System.out.println("Game Dimulai oleh Host!");
            gp.changeGameState(GameState.PLAYING);
        }
        else if (header.equals("INPUT")) {
            // INPUT:SENDER_ID:KEY:PRESSED
            int senderId = Integer.parseInt(parts[1]);

            // Abaikan input dari diri sendiri (karena server broadcast ke semua)
            if (senderId == myPlayerId) return;

            String key = parts[2];
            boolean isPressed = Boolean.parseBoolean(parts[3]);

            // Masukkan ke buffer remote
            updateRemoteBuffer(key, isPressed);
        }
    }

    private void updateRemoteBuffer(String key, boolean pressed) {
        if (key.equals("W")) gp.remoteInputBuffer.up = pressed;
        if (key.equals("S")) gp.remoteInputBuffer.down = pressed;
        if (key.equals("A")) gp.remoteInputBuffer.left = pressed;
        if (key.equals("D")) gp.remoteInputBuffer.right = pressed;
        if (key.equals("V")) gp.remoteInputBuffer.action = pressed;
        if (key.equals("C")) gp.remoteInputBuffer.interact = pressed;
    }
}