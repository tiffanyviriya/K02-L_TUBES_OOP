package net;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private int port;
    private List<ClientHandler> handlers = new ArrayList<>();
    private int connectedPlayers = 0;
    private ServerOrderManager orderManager;
    private boolean gameStarted = false;

    public GameServer(int port) {
        this.port = port;
        this.orderManager = new ServerOrderManager(this);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            // Thread khusus untuk mengupdate state server (seperti Order)
            new Thread(() -> {
                while (true) {
//                        System.out.println("Masuk1");
                    if (gameStarted) {
                        orderManager.update();
                        System.out.println("Masuk2");
                    }
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
            }).start();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Masuk");
                if (connectedPlayers < 2) {
                    ClientHandler handler = new ClientHandler(socket, connectedPlayers);
                    connectedPlayers++;
                    handlers.add(handler);
                    new Thread(handler).start();
                    System.out.println("Player " + connectedPlayers + " connected.");
                } else {
                    socket.close();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void broadcast(String message) {
        for (ClientHandler h : handlers) {
            h.sendMessage(message);
        }
    }

    // Reset lobby saat player keluar
    public void resetLobby() {
        this.gameStarted = false;
        this.connectedPlayers = 0;
        this.handlers.clear();
        this.orderManager.reset();
        System.out.println("Lobby has been reset.");
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int playerId;

        public ClientHandler(Socket s, int id) {
            this.socket = s;
            this.playerId = id;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("ASSIGN_ID|" + playerId);

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equals("REQUEST_LEAVE_LOBBY")) {
                        sendMessage("LEAVE_ACK");
                        resetLobby();
                        break;
                    } else if (input.equals("START_GAME")) {
                        gameStarted = true;
                        broadcast("START_GAME");
                    } else {
                        // Relay input seperti biasa
                        broadcast(input);
                    }
                }
            } catch (IOException e) {
                System.out.println("Player disconnected.");
                resetLobby();
            }
        }

        public void sendMessage(String msg) { out.println(msg); }
    }

    public static void main(String[] args) {
        new GameServer(6741).start();
    }
}