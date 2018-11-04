package server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServerManager implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerManager.class);

    private RoomManager roomManager;
    private final ExecutorService exService = Executors.newSingleThreadExecutor();
    private final ExecutorService userExService = Executors.newFixedThreadPool(20);
    private ServerSocket serverSocket;

    public ChatServerManager(int port) {
        this.roomManager = RoomManager.getInstance();
        try {
            logger.info("Binding to port " + port);
            this.serverSocket = new ServerSocket(port);
            logger.info("Starting server");
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        exService.submit(this);
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userExService.shutdownNow();
        exService.shutdownNow();
    }

    public RoomManager getRoomManager() {
        return this.roomManager;
    }

    public void setRoomManager(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    private void addThread(Socket socket) {
        logger.info("User accepted " + socket);
        ClientHandler clientHandler = new BasicClientHandler(socket, roomManager);
        try {
            clientHandler.open();
            userExService.submit(clientHandler::readSocket);
            userExService.submit(clientHandler::writeToSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!exService.isShutdown()) {
            try {
                logger.info("Server accepting user connections...");
                addThread(serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("Finished main server thread");
    }
}
