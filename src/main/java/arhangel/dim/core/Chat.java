package arhangel.dim.core;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private long id;
    private long admin;
    private List<Long> participants = new ArrayList<>();
    private List<Long> messages = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setAdmin(long admin) {
        this.admin = admin;
    }

    public long getAdmin() {
        return admin;
    }

    public void addMessage(long message) {
        messages.add(message);
    }

    public void setMessages(List<Long> messages) {
        this.messages.addAll(messages);
    }

    public void addParticipant(Long userId) {
        participants.add(userId);
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void removeParticipant(Long userId) {
        participants.remove(userId);
    }

    public List<Long> getMessages() {
        return messages;
    }

}
