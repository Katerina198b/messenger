package arhangel.dim.core.messages;

import java.util.Objects;

public class InfoResultMessage extends Message{

    private long userId;
    private String login;
    private boolean newSession;

    public InfoResultMessage() {
        this.setType(Type.MSG_INFO_RESULT);
    }

    public void setUserId(String userId) {
        this.userId = Long.valueOf(userId);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setNewSession(boolean newSession) {
        this.newSession = newSession;
    }

    public boolean getNewSession() {
        return newSession;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }


    @Override
    public String toString() {
        return "InfoResultMessage{" +
                "userId= '" + userId + '\'' +
                "login= '" + login + '\'' +
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
        InfoResultMessage message = (InfoResultMessage) other;
        return Objects.equals(userId, message.userId) && Objects.equals(login, message.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, login);
    }
}
