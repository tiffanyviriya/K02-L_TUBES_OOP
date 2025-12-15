package net;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 6741;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static int playerCount = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== GAME SERVER STARTED ON PORT " + PORT + " ===");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            while (playerCount < 2) {
                System.out.println("Menunggu pemain...");
                Socket socket = listener.accept();

                System.out.println("Player " + (playerCount + 1) + " terhubung dari " + socket.getInetAddress());

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);

                // Kirim ID ke pemain (0 = Host/P1, 1 = Joiner/P2)
                out.println("ASSIGN_ID:" + playerCount);

                new Handler(socket).start();
                playerCount++;

                // Jika sudah 2 pemain, beri tahu host
                if (playerCount == 2) {
                    broadcast("LOBBY_FULL");
                }
            }
            System.out.println("Lobby Penuh. Menunggu Host memulai game.");
        } finally {
            listener.close();
        }
    }

    // Broadcast pesan ke SEMUA client
    private static void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader in;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input;
                while ((input = in.readLine()) != null) {
                    // Debug print (opsional)
                    // System.out.println("Received: " + input);

                    // Broadcast raw message ke semua client
                    broadcast(input);
                }
            } catch (IOException e) {
                System.out.println("Pemain terputus: " + socket.getInetAddress());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}