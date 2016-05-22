package arhangel.dim.core.messages;

public class HelpMessage extends Message {

    @Override
    public String toString() {
        return "HELP:\n" +
                "/help  - помощь\n" +
                "/login <логин_пользователя> <пароль> - авторизироваться, если  пользователь" +
                " с таким логином существует, иначе - зарегистрироваться\n " +
                " авторизация если пользователь с аким логином сществует, иначе - регистрация\n" +
                "/info <id> - инфа о пользователе\n" +
                "/info - инфа о себе\n" +
                "/chat_history <chat_id> - список сообщений из указанного чата\n" +
                "/chat_list - получить список чатов пользователей\n" +
                "/chat_create <user_id list> - создать чат с людьми из user_id list\n" +
                "/text <id> <message> - отправить message в диалог id\n";
    }

}
