package server.service;

import model.Message;
import model.Room;
import model.User;

import java.net.Socket;

public class BasicClientHandler extends BasicClient implements ClientConnection, ClientHandler {
    private RoomManager roomManager;
    private User user;

    BasicClientHandler(Socket socket, RoomManager roomManager) {
        super(socket);
        this.roomManager = roomManager;
    }

    @Override
    public void handleMessage(Message msg) throws InterruptedException {
        Room room;
        switch (msg.getType()) {
            case NEW_USER:
                this.user = roomManager.newUser(msg.getUsername());
                room = roomManager.newRoom("PRIVATE" + msg.getUsername(), user, messageProcessQueue);
                msg.setResponse("Hello, Welcome from server");
                room.broadcast(msg);
                logger.info("User {} added", msg.getUsername());
                break;
            case ADD_ROOM:
                room = roomManager.newRoom(msg.getRoomName(), user, messageProcessQueue);
                msg.setResponse("Room added: " + room.getRoomName());
                room.broadcast(msg);
                logger.info("Room {} Added", msg.getRoomName());
                break;
            case BROADCAST:
                room = roomManager.findRoom(msg.getRoomName());
                msg.setResponse(msg.getMessage());
                room.broadcast(msg);
                logger.info("Message Broadcasted");
                break;
            case CLOSE_ROOM:
                room = roomManager.findRoom(msg.getRoomName());
                msg.setResponse("Room closing");
                room.broadcast(msg);
                roomManager.deleteRoom(room);
                logger.info("Room {} closed", msg.getRoomName());
                break;
            case JOIN:
                room = roomManager.findRoom(msg.getRoomName());
                room.addUser(user.getUsername(), messageProcessQueue);
                msg.setResponse("Member added: " + user.getUsername());
                room.broadcast(msg);
                logger.info("Member {} Joined", user.getUsername());
                break;
            case LOGOUT:
                if (user == null) return;
                roomManager.logout(user, messageProcessQueue);
                logger.info("Logged out: {}", user.getUsername());
                break;
            case ROOM_LIST:
                msg.setResponse(String.join(" ", roomManager.getRoomNames()).trim());
                messageProcessQueue.put(msg);
                logger.info("Rooms listed: {}", msg.getResponse());
                break;
            case ROOM_MEMBERS:
                room = roomManager.findRoom(msg.getRoomName());
                msg.setResponse(String.join(" ", roomManager.getRoomMembers(room)).trim());
                room.broadcast(msg);
                logger.info("Members listed for room: {}", room.getRoomName());
                break;
        }
    }
}
