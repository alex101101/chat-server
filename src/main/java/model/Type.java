package model;

public enum Type {
    // Depending on message type: broadcast, add room, close room, logout, get room member list, get all rooms
    NEW_USER("New User"),
    BROADCAST("Send"),
    ADD_ROOM("Add Room"),
    CLOSE_ROOM("Close Room"),
    JOIN("Join Room"),
    LOGOUT("Logout"),
    ROOM_LIST("Room List"),
    ROOM_MEMBERS("Room Members");

    private String label;

    Type(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}