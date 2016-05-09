package arhangel.dim.core.messages;

import java.util.Objects;

public class LoginMessage extends Message {
    private String login;
    private int password;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return this.login;
    }

    public void setPassword(String password) {
        this.password = password.hashCode();
    }

    public int getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
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
        LoginMessage message = (LoginMessage) other;
        return Objects.equals(login, message.login)
                && Objects.equals(password, message.password);
    }

    @Override
    // Зачем элементу hashcode?
    public int hashCode() {
        return Objects.hash(super.hashCode(), login, password);
    }
}