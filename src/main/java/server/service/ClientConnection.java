package server.service;

import java.io.IOException;

public interface ClientConnection {
    /**
     * Opens input and output streams to a new Client connection
     * @throws IOException Error with input/output streams
     */
    void open() throws IOException;

    /**
     * Closes client connection socket and input and output streams
     */
    void close();

    /**
     * Runnable for sending responses to the Client
     */
    void writeToSocket();

    /**
     * Runnable for recieving requests from the Client
     */
    void readSocket();
}
