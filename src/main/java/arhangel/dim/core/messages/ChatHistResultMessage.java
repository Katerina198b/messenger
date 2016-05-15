package arhangel.dim.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatHistResultMessage extends Message {

    private long chatId;
    private List<TextMessage> messages = new ArrayList<>();

    public ChatHistResultMessage() {
        this.setType(Type.MSG_CHAT_HIST_RESULT);
    }

    public long getChatId() {
        return chatId;
    }

    public TextMessage getMessage(int id) {
        return messages.get(id);
    }

    public void addMessage(TextMessage message) {
        messages.add(message);
    }

    public void setChatId(String id) {
        this.chatId = Long.valueOf(id);
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public List<TextMessage> getMessages() {
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
        StringBuilder stringBuilder = new StringBuilder("ChatHistResultMessage{ chat id= " + chatId + " messages: \n");
        for (int i = 0; i < messages.size(); i++) {
            stringBuilder.append(messages.get(i).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
