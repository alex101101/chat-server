package model;

public class Message {
    private String message;
    private String roomName;
    private String username;
    private String response;

    private Type type;

    public Message() {

    }

    private Message(String message, String roomName, String username, String response, Type type) {
        this.message = message;
        this.roomName = roomName;
        this.username = username;
        this.type = type;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static class MessageBuilder {
        String message;
        String roomName;
        String username;
        String response;

        Type type;

        public MessageBuilder message(String message) {
            this.message = message;
            return this;
        }

        public MessageBuilder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public MessageBuilder username(String username) {
            this.username = username;
            return this;
        }

        public MessageBuilder response(String response) {
            this.response = response;
            return this;
        }

        public MessageBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public Message build() {
            return new Message(message, roomName, username, response, type);
        }

    }
}
