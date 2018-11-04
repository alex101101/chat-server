package client;

public interface SocketNotifier {
    void notifySocketStatus(boolean isOpen);
    void onMessage(String line);
}
