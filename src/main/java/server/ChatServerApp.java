package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.service.ChatServerManager;

import java.util.Scanner;

public class ChatServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerApp.class);

    public static void main(String[] args) {

        ChatServerManager chatServerManager = new ChatServerManager(Integer.parseInt(args[0]));
        Scanner reader = new Scanner(System.in);

        logger.info("Enter anything to stop server: ");
        String input = reader.nextLine();
        reader.close();

        chatServerManager.stop();
    }
}
