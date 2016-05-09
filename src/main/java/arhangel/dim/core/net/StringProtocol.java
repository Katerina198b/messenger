package arhangel.dim.core.net;

import arhangel.dim.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Простейший протокол передачи данных
 */
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
                loginMessage.setSenderId(tokens[1]);
                loginMessage.setLogin(tokens[2]);
                loginMessage.setPassword(tokens[3]);
                loginMessage.setType(Type.MSG_LOGIN);
                return loginMessage;

            case MSG_STATUS:
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setSenderId(tokens[1]);
                statusMessage.setStatus(Status.valueOf(tokens[2]));
                return statusMessage;

            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setSenderId(tokens[1]);
                for (int i = 2; i < tokens.length; i ++) {
                    chatCreateMessage.addId(tokens[i]);
                }
                return chatCreateMessage;

            case MSG_CHAT_HIST:
                ChatHistMessage chatHistMessage = new ChatHistMessage();
                chatHistMessage.setSenderId(tokens[1]);
                chatHistMessage.setChatId(tokens[2]);
                return chatHistMessage;

            case MSG_CHAT_HIST_RESULT:
                ChatHistResultMessage chatHistResultMessage = new ChatHistResultMessage();
                chatHistResultMessage.setSenderId(tokens[1]);
                for (int i = 2; i < tokens.length; i++) {
                    chatHistResultMessage.setMessage(tokens[i]);
                }
                return chatHistResultMessage;

            case MSG_CHAT_LIST:
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setSenderId(tokens[1]);
                return chatListMessage;

            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
                chatListResultMessage.setSenderId(tokens[1]);
                for (int i = 2; i < tokens.length; i++) {
                    chatListResultMessage.setChat(tokens[i]);
                }
                return chatListResultMessage;

            case MSG_INFO:
                InfoMessage infoMessage = new InfoMessage();
                infoMessage.setSenderId(tokens[1]);
                infoMessage.setUserId(tokens[2]);
                return infoMessage;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = new InfoResultMessage();
                infoResultMessage.setSenderId(tokens[1]);
                infoResultMessage.setLogin(tokens[2]);
                return infoResultMessage;

            default:
                throw new ProtocolException("Invalid type: " + type);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        StringBuilder builder = new StringBuilder();
        Type type = msg.getType();
        builder.append(type).append(DELIMITER);
        switch (type) {

            case MSG_TEXT:
                TextMessage textMessage = (TextMessage) msg;
                builder.append(String.valueOf(textMessage.getSenderId())).append(DELIMITER);
                builder.append(textMessage.getChatId()).append(DELIMITER);
                builder.append(textMessage.getText()).append(DELIMITER);
                break;

            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(String.valueOf(loginMessage.getSenderId())).append(DELIMITER);
                builder.append(loginMessage.getLogin()).append(DELIMITER);
                builder.append(String.valueOf(loginMessage.getPassword())).append(DELIMITER);
                break;

            case MSG_INFO:
                InfoMessage infoMessage = (InfoMessage) msg;
                builder.append(String.valueOf(infoMessage.getSenderId())).append(DELIMITER);
                builder.append(String.valueOf(infoMessage.getUserId())).append(DELIMITER);
                break;

            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMessage = (ChatCreateMessage) msg;
                builder.append(String.valueOf(chatCreateMessage.getSenderId())).append(DELIMITER);
                for (int i = 0; i < chatCreateMessage.getIdsCount(); i++) {
                    builder.append(String.valueOf(chatCreateMessage.getId(i))).append(DELIMITER);
                }
                break;

            case MSG_CHAT_HIST:
                ChatHistMessage chatHistMessage = (ChatHistMessage) msg;
                builder.append(String.valueOf(chatHistMessage.getSenderId())).append(DELIMITER);
                builder.append(String.valueOf(chatHistMessage.getChatId())).append(DELIMITER);
                break;
            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                builder.append(String.valueOf(statusMessage.getSenderId())).append(DELIMITER);
                builder.append(String.valueOf(statusMessage.getStatus())).append(DELIMITER);
                break;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
                builder.append(String.valueOf(infoResultMessage.getSenderId())).append(DELIMITER);
                // в ответ MSG_CHAT_LIST_RESULT, MSG_CHAT_HIST,
                break;
            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage chatListResultMessage = (ChatListResultMessage) msg;
                builder.append(String.valueOf(chatListResultMessage.getSenderId())).append(DELIMITER);
                for (int i = 0; i < chatListResultMessage.length(); i++) {
                    builder.append(String.valueOf(chatListResultMessage.getChat(i))).append(DELIMITER);
                }
                break;

            case MSG_CHAT_HIST_RESULT:
                ChatHistResultMessage chatHistResultMessage = (ChatHistResultMessage) msg;
                builder.append(String.valueOf(chatHistResultMessage.getSenderId())).append(DELIMITER);
                for (int i = 0; i < chatHistResultMessage.length(); i++) {
                    builder.append(String.valueOf(chatHistResultMessage.getMessage(i))).append(DELIMITER);
                }
                break;

            default:
                throw new ProtocolException("Invalid type: " + type);

        }
        log.info("encoded: {}", builder.toString());
        return builder.toString().getBytes();
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            //
        }
        return null;
    }
}
