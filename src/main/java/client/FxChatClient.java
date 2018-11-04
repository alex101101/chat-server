package client;

public class FxChatClient extends BasicChatClient implements SocketNotifier {
    private SocketNotifier fxListener;

    public FxChatClient(SocketNotifier fxListener, String host, int port) {
        super(host, port);
        this.fxListener = fxListener;
    }

    @Override
    public void notifySocketStatus(boolean isOpen) {
        javafx.application.Platform.runLater(() -> fxListener.notifySocketStatus(isOpen));
    }

    @Override
    public void onMessage(String line) {
        javafx.application.Platform.runLater(() -> fxListener.onMessage(line));
    }
}
