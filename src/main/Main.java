package main;

import main.util.GamePanel;
import net.GameServer;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // --- MODE SERVER ---
        // Jika dijalankan dengan: java -jar Game.jar server
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            System.out.println("=== MODE SERVER AKTIF ===");
            GameServer.main(args);
            return; // STOP! Jangan lanjut ke kode Client di bawah
        }

        // --- MODE CLIENT ---
        System.out.println("=== MODE CLIENT AKTIF ===");
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Nimon's Cooked - Multiplayer");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Auto-connect jika ada argumen IP (java -jar Game.jar 192.168.x.x)
        if (args.length >= 1) {
            String ip = args[0];
            int port = 6741;
            if (args.length >= 2) {
                try { port = Integer.parseInt(args[1]); } catch (Exception e){}
            }
            System.out.println("Auto-connect ke: " + ip + ":" + port);
            gamePanel.netClient.connect(ip, port);
        }

        gamePanel.startGameThread();
    }
}