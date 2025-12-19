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

    // Port default 6741 disesuaikan dengan Main.java di Canvas
    private String serverIp = "localhost";
    private int port = 6741;

    public int myPlayerId = -1;
    public boolean isConnected = false;

    public NetworkClient(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Connect default (Localhost:6741).
     * Digunakan oleh DifficultyScene saat memulai mode multiplayer lokal.
     */
    public void connect() {
        connect(this.serverIp, this.port);
    }

    /**
     * Connect dengan Parameter IP dan Port.
     * Digunakan oleh Main.java saat menerima argumen dari terminal.
     */
    public void connect(String ip, int port) {
        if (isConnected) return;
        this.serverIp = ip;
        this.port = port;

        new Thread(() -> {
            try {
                System.out.println("Mencoba terhubung ke " + serverIp + ":" + port + "...");
                socket = new Socket(serverIp, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;

                System.out.println("Terhubung ke server!");
                run();

            } catch (IOException e) {
                System.err.println("Gagal connect ke server. Pastikan GameServer sudah aktif.");
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
        } finally {
            closeConnection();
        }
    }

    private void processMessage(String msg) {
        // Protokol menggunakan delimiter "|" agar konsisten: COMMAND|DATA1|DATA2...
        String[] parts = msg.split("\\|");
        String header = parts[0];

        switch (header) {
            case "ASSIGN_ID":
                myPlayerId = Integer.parseInt(parts[1]);
                System.out.println("ID Saya: " + myPlayerId);
                break;

            case "START_GAME":
                System.out.println("Server memberikan sinyal START. Memasuki arena...");
                gp.changeGameState(GameState.PLAYING);
                break;

            case "ORDER_SPAWN":
                // Format: ORDER_SPAWN|RecipeName|Duration
                if (gp.orderM != null) {
                    gp.orderM.applyOrderSpawn(parts[1], Integer.parseInt(parts[2]));
                }
                break;

            case "ORDER_REMOVE":
                // Format: ORDER_REMOVE|Index
                if (gp.orderM != null) {
                    gp.orderM.applyOrderRemove(Integer.parseInt(parts[1]));
                }
                break;

            case "INPUT":
                // Format: INPUT|SENDER_ID|KEY|PRESSED
                int senderId = Integer.parseInt(parts[1]);
                if (senderId != myPlayerId) {
                    String key = parts[2];
                    boolean isPressed = Boolean.parseBoolean(parts[3]);
                    updateRemoteBuffer(key, isPressed);
                }
                break;

            case "LEAVE_ACK":
                System.out.println("Keluar dari Lobby dikonfirmasi.");
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

    public void sendCommand(String cmd) {
        if (isConnected && out != null) {
            out.println(cmd);
        }
    }

    public void closeConnection() {
        try {
            isConnected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}