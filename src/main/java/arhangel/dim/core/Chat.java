package arhangel.dim.core;

import arhangel.dim.core.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private User admin;
    private List<Long> participants = new ArrayList<>();
    private List<Long> messages  = new ArrayList<>();

    private void setId(Long id) {
        this.id = id;
    }

    private Long getId() {
        return id;
    }

    private void setAdmin(User admin) {
        this.admin = admin;
    }

    private User getAdmin() {
        return admin;
    }

    private void addMessage(Message message) {
        messages.add(message.getId());
    }

    private void addParticipant(User user) {
        participants.add(user.getId());
    }

    private  void removeParticipant(User user) {
        participants.remove(user.getId());
    }

}
