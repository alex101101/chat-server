package server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BasicClient implements ClientConnection, ClientHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private String id;
    private ObjectMapper mapper;
    protected BlockingQueue<Message> messageProcessQueue;

    public BasicClient(Socket socket) {
        this.socket = socket;
        this.id = UUID.randomUUID().toString();
        mapper = new ObjectMapper();
        this.messageProcessQueue = new LinkedBlockingQueue<>();
    }

    public void readSocket() {
        logger.info("Chat server thread: " + Thread.currentThread().getName() + " started. Id: " + id);
            try {
                while (true) {
                    String message = dataIn.readUTF();
                    // Deserialise to Message
                    Message msg = mapper.readValue(message, Message.class);
                    handleMessage(msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                // Do nothing
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Client threads will be interrupted by exceptions
                logger.info("Finished chat service thread " + Thread.currentThread().getName());
                close();
            }
    }

    public void writeToSocket() {
        try {
            while (true) {
                Message message = messageProcessQueue.take();
                logger.debug("Found outgoing message of type: " + message.getType()
                        + ", Body: " + mapper.writeValueAsString(message));
                dataOut.writeUTF(mapper.writeValueAsString(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Client threads will be interrupted by exceptions
            logger.info("Finished chat service thread " + Thread.currentThread().getName());
            close();
        }
    }

    public void open() throws IOException {
        this.dataIn = new DataInputStream(socket.getInputStream());
        this.dataOut = new DataOutputStream(socket.getOutputStream());
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
        } catch (IOException e) { e.printStackTrace(); }

        logger.info("Socket is CLOSED");
    }
}
