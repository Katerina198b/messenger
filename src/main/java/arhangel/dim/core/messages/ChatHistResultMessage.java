package arhangel.dim.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatHistResultMessage extends Message {

    private int chatId;
    private List<String> messages = new ArrayList<String>();

    public int getChatId() {
        return chatId;
    }

    public int length() {
        return messages.size();
    }

    public String getMessage(int id) {
        return messages.get(id);
    }

    public void setMessage(String message) {
        messages.add(message);
    }

    public void setChatId(String id) {
        Integer chatId = new Integer(id);
        this.chatId = chatId;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        ChatHistResultMessage message = (ChatHistResultMessage) other;
        return Objects.equals(chatId, message.chatId) && Objects.equals(messages, message.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId);
    }

    @Override
    public String toString() {
        return "ChatHistResultMessage{" +
                "chat id=" +
                chatId +
                "messages: " +
                messages +
                '}';
    }
}
