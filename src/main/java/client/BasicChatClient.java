package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public abstract class BasicChatClient implements SocketNotifier {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Socket socket = null;
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;
    private boolean clientRunning = false;
    private Thread readerThread;
    private int port;

    public BasicChatClient(String host, int port) {
        this.port = port;
        this.readerThread = new SocketReader();
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isClientRunning() {
        return clientRunning;
    }

    public synchronized void setClientRunning(boolean clientRunning) {
        this.clientRunning = clientRunning;
    }

    public void start() {
        try {
            dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setClientRunning(true);
        readerThread.start();
    }

    public void close() {
        try {
            setClientRunning(false);
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
            if (socket != null) socket.close();
            notifySocketStatus(false);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            dataOut.writeUTF(message);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SocketReader extends Thread {
        @Override
        public void run() {
            if (socket != null && socket.isConnected()) {
                notifySocketStatus(true);
            }

            try {
                String line;
                while (isClientRunning()) {
                    line = dataIn.readUTF();

                    onMessage(line);
                }
            } catch (SocketException e) {
                logger.info("Read error. Socket is closed");
            } catch (EOFException e) {
                // Do nothing
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }

        }
    }
}
