package client.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import client.FxChatClient;
import client.SocketNotifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ChatMetadata;
import model.Message;
import model.Type;

public class ChatWindowController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button disconnectButton;

    @FXML
    private TextField portTextField;

    @FXML
    private Label connectedLabel;

    @FXML
    private ChoiceBox<Type> actionChoiceField;

    @FXML
    private ChoiceBox<String> roomChoiceField;

    @FXML
    private ListView<String> rcvdMsgsListView;

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField sendTextField;

    @FXML
    private Button sendButton;

    @FXML
    private Button connectButton;

    @FXML
    private Button joinRoomButton;

    @FXML
    private Button closeRoomButton;

    @FXML
    private Button logoutButton;

    public enum ConnectionState {
        CONNECTED, DISCONNECTED, CONNECTING
    }

    private boolean connected;
    private FxChatClient fxChatClient;
    private ObservableList<String> messages;
    private ObservableList<String> rooms;
    private static final ObjectMapper mapper = new ObjectMapper();
    private ChatMetadata chatMetadata;

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void displayConnectionState(ConnectionState connectionState) {
        switch(connectionState) {
            case CONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                sendButton.setDisable(false);
                rcvdMsgsListView.setDisable(false);
                connectedLabel.setText("Connected");
                break;
            case DISCONNECTED:
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
                sendButton.setDisable(true);
                rcvdMsgsListView.setDisable(true);
                connectedLabel.setText("Disconnected");
                break;
            case CONNECTING:
                connectButton.setDisable(true);
                disconnectButton.setDisable(true);
                sendButton.setDisable(true);
                rcvdMsgsListView.setDisable(true);
                connectedLabel.setText("Connecting...");
                break;
        }
    }

    public Message generateMessage(Type type) {
        Message message = null;
        switch (type) {
            case NEW_USER:
                chatMetadata.setUserName(sendTextField.getText());
                message = new Message.MessageBuilder()
                        .username(sendTextField.getText())
                        .type(Type.NEW_USER).build();
                break;
            case BROADCAST:
                message = new Message.MessageBuilder()
                        .message(sendTextField.getText())
                        .roomName(roomChoiceField.getValue())
                        .username(chatMetadata.getUserName())
                        .type(Type.BROADCAST).build();
                break;
            case ADD_ROOM:
                chatMetadata.getRooms().add(sendTextField.getText());
                message = new Message.MessageBuilder()
                        .roomName(sendTextField.getText())
                        .username(chatMetadata.getUserName())
                        .type(Type.ADD_ROOM).build();
                roomChoiceField.setValue(sendTextField.getText());
                break;
            case CLOSE_ROOM:
                message = new Message.MessageBuilder()
                        .message(sendTextField.getText())
                        .roomName(roomChoiceField.getValue())
                        .username(chatMetadata.getUserName())
                        .type(Type.CLOSE_ROOM).build();
                break;
            case JOIN:
                message = new Message.MessageBuilder()
                        .roomName(sendTextField.getText())
                        .username(chatMetadata.getUserName())
                        .type(Type.JOIN).build();
                roomChoiceField.setValue(sendTextField.getText());
                break;
            case LOGOUT:
                message = new Message.MessageBuilder()
                        .message(sendTextField.getText())
                        .roomName(roomChoiceField.getValue())
                        .username(chatMetadata.getUserName())
                        .type(Type.LOGOUT).build();
                break;
            case ROOM_LIST:
                message = new Message.MessageBuilder()
                        .roomName(roomChoiceField.getValue())
                        .username(chatMetadata.getUserName())
                        .type(Type.ROOM_LIST).build();
                break;
            case ROOM_MEMBERS:
                message = new Message.MessageBuilder()
                        .roomName(roomChoiceField.getValue())
                        .username(chatMetadata.getUserName())
                        .type(Type.ROOM_MEMBERS).build();
                break;
        }

        return message;
    }

    @FXML
    void handleClearRcvdMsgsButton(ActionEvent event) {
        messages.clear();
    }

    @FXML
    void handleSendMessageButton(ActionEvent event) {
        Message message = generateMessage(actionChoiceField.getValue());
        if (!sendTextField.getText().equals("") && message != null) {
            sendMessage(message);
        }

    }

    void sendMessage(Message message) {
        try {
            fxChatClient.send(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleConnectButton(ActionEvent event) {
        displayConnectionState(ConnectionState.CONNECTING);
        connectClient();
    }

    @FXML
    void handleDisconnectButton(ActionEvent event) {
        fxChatClient.close();
    }

    @FXML
    void initialize() {
        setConnected(false);
        displayConnectionState(ConnectionState.DISCONNECTED);

        messages = FXCollections.observableArrayList();
        rcvdMsgsListView.setItems(messages);
        rcvdMsgsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        rooms = FXCollections.observableArrayList();
        roomChoiceField.setItems(rooms);

        actionChoiceField.getItems().setAll(Type.values());
        actionChoiceField.setValue(Type.BROADCAST);
        this.chatMetadata = new ChatMetadata();
        chatMetadata.setRooms(new ArrayList<>());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {if (fxChatClient != null) {
            Message message = generateMessage(Type.LOGOUT);
            sendMessage(message);
            fxChatClient.close();
        }}));
    }

    private synchronized void notifyShutdown() {
        this.connected = false;
        notifyAll();
    }

    private void connectClient() {
        try {
            fxChatClient = new FxChatClient(new FxListener(),
                    hostTextField.getText(),
                    Integer.parseInt(portTextField.getText()));
            fxChatClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            displayConnectionState(ConnectionState.DISCONNECTED);
        }
    }

    private void refreshRoomList() {
        Message message = new Message.MessageBuilder()
                .username(chatMetadata.getUserName())
                .type(Type.ROOM_LIST).build();

        sendMessage(message);
    }

    public void handleRcvdBoxMessage(Message message) {
        if (message.getRoomName() == null ||
                (message.getRoomName() != null && roomChoiceField.getValue().equals(message.getRoomName()))) {
            messages.add(message.getResponse());
        }
    }

    public void handleJoinRoomButton(ActionEvent actionEvent) {
    }

    public void handleCloseRoom(ActionEvent actionEvent) {
    }

    public void handleLogoutButton(ActionEvent actionEvent) {
    }

    private synchronized void handleResponse(Message message) {
        switch (message.getType()) {
            case BROADCAST:
            case NEW_USER:
            case ADD_ROOM:
            case CLOSE_ROOM:
            case JOIN:
            case ROOM_MEMBERS:
            case LOGOUT:
                handleRcvdBoxMessage(message);
                refreshRoomList();
                break;
            case ROOM_LIST:
                String roomValue = roomChoiceField.getValue();
                rooms.setAll(message.getResponse().split(" "));
                roomChoiceField.setValue(roomValue);
                break;
        }
    }

    class FxListener implements SocketNotifier {

        @Override
        public void notifySocketStatus(boolean isOpen) {
            if (isOpen) {
                setConnected(true);
                displayConnectionState(ConnectionState.CONNECTED);
            } else {
                notifyShutdown();
                displayConnectionState(ConnectionState.DISCONNECTED);
            }

        }

        @Override
        public void onMessage(String line) {
            Message message = null;
            if (line != null && !line.equals("")) {
                try {
                    message = mapper.readValue(line, Message.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handleResponse(Objects.requireNonNull(message));
            }
        }
    }
}
