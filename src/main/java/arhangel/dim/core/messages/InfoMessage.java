package arhangel.dim.core.messages;

import java.util.Objects;

public class InfoMessage extends Message {
    private int userId;

    public void setUserId (int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        /*
         * Ключевое слово super можно использовать для вызова конструктора суперкласса
         * и для обращения к члену суперкласса, скрытому членом подкласса.
         */
        if (!super.equals(other)) {
            return false;
        }
        InfoMessage message = (InfoMessage) other;
        return Objects.equals(userId, message.userId);
    }

    @Override
    // Зачем элементу hashcode?
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }
}