package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * отправить сообщение в заданный чат, чат должен быть в списке чатов пользователя
 * (только для залогиненных пользователей)
 */

public class TextMessage extends Message {

    private long chatId;
    private String text;

    public TextMessage(){
        this.setType(Type.MSG_TEXT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = Long.valueOf(chatId);
    }

    public long getChatId() {
        return chatId;
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
        TextMessage message = (TextMessage) other;
        return Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "chat id = " + chatId + " , " +
                "text = '" + text  + "\' }";
    }
}
