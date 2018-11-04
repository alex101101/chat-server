package model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class Room {
    private String roomName;
    private Set<BlockingQueue<Message>> userQueues;
    private volatile Set<String> members;

    public Room(String roomName) {
        this.roomName = roomName;
        this.userQueues = new HashSet<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public void addUser(String userName, BlockingQueue<Message> messageProcessQueue) {
        // Must lock the room to prevent concurrent access during room operations.
        // Does not affect other rooms from processing messages
        synchronized (this) {
            userQueues.add(messageProcessQueue);
            members.add(userName);
        }
    }

    public void removeUser(String userName, BlockingQueue<Message> messageProcessQueue) {
        synchronized (this) {
            userQueues.remove(messageProcessQueue);
            members.remove(userName);
        }
    }

    public void broadcast(Message message) throws InterruptedException {
        synchronized (this) {
            for (BlockingQueue<Message> userQueue : userQueues) {
                userQueue.put(message);
            }
        }
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        synchronized (this) {
            this.members = members;
        }
    }
}
