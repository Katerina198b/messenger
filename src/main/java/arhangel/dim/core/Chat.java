package arhangel.dim.core;

import java.util.ArrayList;
import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private User admin;
    private List<Long> participants = new ArrayList<>();
    private List<Long> messages = new ArrayList<>();

    public void setId(Long id) {

        this.id = id;
    }

    public Long getId() {

        return id;
    }

    public void setAdmin(User admin) {

        this.admin = admin;
    }

    public User getAdmin() {

        return admin;
    }

    public void addMessage(long message) {

        messages.add(message);
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
