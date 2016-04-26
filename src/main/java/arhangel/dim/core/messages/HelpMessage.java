package arhangel.dim.core.messages;

public class HelpMessage extends Message {

    @Override
    public String toString() {
        return "HELP:\n +" +
                "/help  - помощь +" +
                "/login <логин_пользователя> <пароль> - регистрация\n" +
                "/login <пароль> - авторизация\n" +
                "/info [id] - инфа о пользователе\n" +
                "/info - инфа о себе\n" +
                "/chat_list - получить список чатов пользователей\n" +
                "/chat_create <user_id list> - создать чат с людьми из user_id list\n" +
                "/text <id> <message> - отправить message в диалог id\n";
    }

}
