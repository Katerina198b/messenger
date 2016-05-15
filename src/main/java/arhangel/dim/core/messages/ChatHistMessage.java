package arhangel.dim.core.messages;

/**
 * список сообщений из указанного чата (только для залогиненных пользователей)
 */
import java.util.Objects;

public class ChatHistMessage extends Message {

    private long chatId;

    public ChatHistMessage() {
        this.setType(Type.MSG_CHAT_HIST);
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(String id) {
        this.chatId = Long.valueOf(id);
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
        ChatHistMessage message = (ChatHistMessage) other;
        return Objects.equals(chatId, message.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId);
    }

    @Override
    public String toString() {
        return "ChatHistMessage{" +
                "chat=" +
                chatId +
                '}';
    }
}
