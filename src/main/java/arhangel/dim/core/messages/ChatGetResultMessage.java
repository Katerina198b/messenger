package arhangel.dim.core.messages;

import java.util.Objects;

public class ChatGetResultMessage extends Message {

    private long chatId;

    public ChatGetResultMessage() {
        this.setType(Type.MSG_CHAT_HIST);
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = Long.valueOf(chatId);
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
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
        ChatGetResultMessage message = (ChatGetResultMessage) other;
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
