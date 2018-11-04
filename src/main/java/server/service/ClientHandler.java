package server.service;

import model.Message;

public interface ClientHandler extends ClientConnection {
    /**
     * Handles message recieved, records data to shared model and implements action.
     * @param msg message sent by the client with message type {@link model.Type}
     */
    void handleMessage(Message msg) throws InterruptedException;
}
