/* получить всю информацию о пользователе,
 * без аргументов - о себе (только для залогиненных пользователей)
 */

package arhangel.dim.core.messages;

import java.util.Objects;

public class InfoMessage extends Message {
    private long userId;
    private String login;

    public void setUserId(String userId) {
        this.userId = Long.valueOf(userId);
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "userId='" + userId + '\'' +
                "login='" + login +
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
        return Objects.equals(userId, message.userId) && Objects.equals(login, message.login);
    }

    @Override
    // Зачем элементу hashcode?
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }
}