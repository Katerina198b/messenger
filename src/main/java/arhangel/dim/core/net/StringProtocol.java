package arhangel.dim.core.net;

/**
 * Простейший протокол передачи данных
 */
import arhangel.dim.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class StringProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(StringProtocol.class);

    public static final String DELIMITER = ";";

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {

        String str = new String(bytes);
        log.info("decoded: {}", str);
        String[] tokens = str.split(DELIMITER);
        Type type = Type.valueOf(tokens[0]);
        switch (type) {

            case MSG_TEXT:
                TextMessage textMsg = new TextMessage();
                textMsg.setType(type);
                textMsg.setSenderId(tokens[1]);
                textMsg.setChatId(tokens[2]);
                textMsg.setText(tokens[3]);
                return textMsg;

            case MSG_LOGIN:
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setType(type);
                loginMessage.setSenderId(tokens[1]);
                loginMessage.setLogin(tokens[2]);
                loginMessage.setPassword(tokens[3]);
                return loginMessage;

            case MSG_STATUS:
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setType(type);
                statusMessage.setSenderId(tokens[1]);
                statusMessage.setStatus(Status.valueOf(tokens[2]));
                return statusMessage;

            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setType(type);
                chatCreateMessage.setSenderId(tokens[1]);
                for (int i = 2; i < tokens.length; i++) {
                    chatCreateMessage.addId(tokens[i]);
                }
                return chatCreateMessage;

            case MSG_CHAT_HIST:
                ChatHistMessage chatHistMessage = new ChatHistMessage();
                chatHistMessage.setType(type);
                chatHistMessage.setSenderId(tokens[1]);
                chatHistMessage.setChatId(tokens[2]);
                return chatHistMessage;

            case MSG_CHAT_HIST_RESULT:
                ChatHistResultMessage chatHistResultMessage = new ChatHistResultMessage();
                chatHistResultMessage.setType(type);
                chatHistResultMessage.setSenderId(tokens[1]);
                long chatId = Long.valueOf(tokens[2]);
                for (int i = 3; i < tokens.length; i = i + 2) {
                    //не уверена, где именно надо обьявлять TextMessage
                    TextMessage message = new TextMessage();
                    message.setChatId(chatId);
                    message.setSenderId(tokens[i]);
                    message.setText(tokens[i + 1]);
                    chatHistResultMessage.addMessage(message);
                }
                return chatHistResultMessage;

            case MSG_CHAT_LIST:
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setType(type);
                chatListMessage.setSenderId(tokens[1]);
                return chatListMessage;

            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
                chatListResultMessage.setType(type);
                chatListResultMessage.setSenderId(tokens[1]);
                for (int i = 2; i < tokens.length; i++) {
                    chatListResultMessage.addChat(tokens[i]);
                }
                return chatListResultMessage;

            case MSG_INFO:
                InfoMessage infoMessage = new InfoMessage();
                infoMessage.setType(type);
                infoMessage.setSenderId(tokens[1]);
                infoMessage.setUserId(tokens[2]);
                return infoMessage;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = new InfoResultMessage();
                infoResultMessage.setType(type);
                infoResultMessage.setSenderId(tokens[1]);
                infoResultMessage.setUserId(tokens[2]);
                infoResultMessage.setLogin(tokens[3]);
                infoResultMessage.setNewSession(Boolean.valueOf(tokens[4]));
                return infoResultMessage;

            case MSG_ERROR:
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(type);
                errorMessage.setSenderId(tokens[1]);
                errorMessage.setText(tokens[2]);
                return errorMessage;

            default:
                log.error("decoded: Invalid type: {}", type);
                throw new ProtocolException("decoded: Invalid type: " + type);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {

        StringBuilder builder = new StringBuilder();
        Type type = msg.getType();

        builder.append(type).append(DELIMITER)
                .append(Optional.ofNullable(msg.getSenderId()).orElse(0L))
                .append(DELIMITER);
        switch (type) {

            case MSG_TEXT:
                TextMessage textMessage = (TextMessage) msg;
                builder.append(textMessage.getChatId()).append(DELIMITER);
                builder.append(textMessage.getText());
                break;

            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(loginMessage.getLogin()).append(DELIMITER);
                builder.append(loginMessage.getPassword()).append(DELIMITER);
                break;

            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMessage = (ChatCreateMessage) msg;
                for (int i = 0; i < chatCreateMessage.getIdsCount(); i++) {
                    builder.append(String.valueOf(chatCreateMessage.getId(i)));
                    if (i < (chatCreateMessage.getIdsCount() - 1)) {
                        builder.append(DELIMITER);
                    }
                }
                break;

            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                builder.append(statusMessage.getStatus());
                break;

            case MSG_INFO:
                InfoMessage infoMessage = (InfoMessage) msg;
                builder.append(infoMessage.getUserId()).append(DELIMITER);
                break;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
                builder.append(infoResultMessage.getUserId()).append(DELIMITER);
                builder.append(infoResultMessage.getLogin()).append(DELIMITER);
                builder.append(infoResultMessage.getNewSession()).append(DELIMITER);
                break;

            case MSG_CHAT_HIST:
                ChatHistMessage chatHistMessage = (ChatHistMessage) msg;
                builder.append(chatHistMessage.getChatId());
                break;

            case MSG_CHAT_HIST_RESULT:
                ChatHistResultMessage chatHistResultMessage = (ChatHistResultMessage) msg;
                builder.append(chatHistResultMessage.getChatId()).append(DELIMITER);
                List<TextMessage> messages = chatHistResultMessage.getMessages();
                for (int i = 0; i < messages.size(); i++) {
                    builder.append(messages.get(i).getSenderId()).append(DELIMITER);
                    builder.append(messages.get(i).getText());
                    if (i < (messages.size() - 1)) {
                        builder.append(DELIMITER);
                    }
                }
                break;

            case MSG_CHAT_LIST:
                break;

            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage chatListResultMessage = (ChatListResultMessage) msg;
                for (int i = 0; i < chatListResultMessage.length(); i++) {
                    builder.append(chatListResultMessage.getChat(i));
                    if (i < (chatListResultMessage.length() - 1)) {
                        builder.append(DELIMITER);
                    }
                }
                break;

            case MSG_ERROR:
                ErrorMessage errorMessage = (ErrorMessage) msg;
                builder.append(errorMessage.getText());
                break;

            default:
                log.error("encoded: Invalid type: {}", type);
                throw new ProtocolException("encoded: Invalid type: " + type);

        }
        log.info(" encoded: {}", builder.toString());
        return builder.toString().getBytes();
    }
}

