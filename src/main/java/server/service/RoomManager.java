package server.service;

import model.Message;
import model.Room;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);
    private static RoomManager instance;

    private final Map<String, Room> rooms;

    private RoomManager() {
        // Atomic operations on the map of references to all rooms
        rooms = new ConcurrentHashMap<>();
    }

    static RoomManager getInstance() {
        if (instance == null) {
            synchronized (RoomManager.class) {
                if (instance == null) {
                    instance = new RoomManager();
                }
            }
        }
        return instance;
    }

    public void addRoom(Room room) {
        this.rooms.put(room.getRoomName(), room);
    }

    public void deleteRoom(Room room) {
        this.rooms.remove(room.getRoomName());
    }

    public Room findRoom(String roomName) {
        return this.rooms.get(roomName);
    }

    public Map<String, Room> getRooms() {
        return this.rooms;
    }

    public Room newRoom(String roomName, User user, BlockingQueue<Message> messageProcessQueue) {
        Room room = new Room(roomName);
        room.setMembers(new HashSet<>());
        room.addUser(user.getUsername(), messageProcessQueue);
        addRoom(room);

        return room;
    }

    public List<String> getRoomNames() {
        return new ArrayList<>(rooms.keySet());
    }

    public String getRoomMembers(Room room) {
        StringBuilder memberList = new StringBuilder();
        for (String userName : room.getMembers()) {
            memberList.append(userName).append(" ");
        }
        return memberList.toString();
    }

    public void logout(User user, BlockingQueue<Message> messageProcessQueue) {
        synchronized (rooms) {
            for (Room r : rooms.values()) {
                r.removeUser(user.getUsername(), messageProcessQueue);
            }
        }
    }

    public User newUser(String userName) {
        User user = new User();
        user.setUsername(userName);

        return user;
    }
}
