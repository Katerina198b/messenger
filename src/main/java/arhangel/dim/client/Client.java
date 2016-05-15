package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import arhangel.dim.core.messages.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

/**
 * Клиент для тестирования серверного приложения
 */
public class Client implements ConnectionHandler {

    static Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * Протокол, хост и порт инициализируются из конфига
     */
    private Protocol protocol;
    private int port;
    private String host;

    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    private long senderId;

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getSenderId() {
        return senderId;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Инициализируем сокет и слушаем входной поток от сервера
     */
    public void initSocket() throws IOException {

        Socket socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        socketThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 64];
            log.info("Starting listener thread...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //if (in.available() > 0)
                    int read = in.read(buf);
                    if (read > 0) {
                        Message msg = protocol.decode(Arrays.copyOf(buf, read));
                        onMessage(msg);
                    }
                } catch (SocketException e) {
                    return;

                } catch (IOException | ProtocolException e) {
                    log.error("Failed into initSocket");
                    e.printStackTrace();
                }
            }
        });

        socketThread.start();
    }

    /**
     * Реагируем на входящее сообщение
     */
    @Override
    public void onMessage(Message msg) {

        if (msg.getType() == Type.MSG_INFO_RESULT) {
            InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
            if (infoResultMessage.getNewSession()) {
                this.setSenderId(infoResultMessage.getUserId());
                System.out.println("Login is successful");
            }
        }
        System.out.println(msg.toString());
        System.out.println("$");

    }

    public void invalidInput() {

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setText("Invalid input. For more information please enter \"/help\"");
        log.error("processInput : Invalid input");
        this.onMessage(errorMessage);

    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     *
     * @throws IOException, ProtocolException
     */
    public void processInput(String line) throws IOException, ProtocolException {

        String[] tokens = line.split(" ");
        log.info("processInput: Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {

            case "/login":
                if (tokens.length == 3) {
                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.setSenderId(this.getSenderId());
                    loginMessage.setLogin(tokens[1]);
                    loginMessage.setPassword(tokens[2]);
                    send(loginMessage);
                } else {
                    this.invalidInput();
                }
                break;

            /**
             * Cообщение, которое не отправляется серверу
             */
            case "/help":
                HelpMessage helpMessage = new HelpMessage();
                helpMessage.setSenderId(this.getSenderId());
                this.onMessage(helpMessage);
                break;

            case "/text":
                if (tokens.length > 2) {
                    TextMessage textMessage = new TextMessage();
                    textMessage.setSenderId(this.getSenderId());
                    textMessage.setChatId(tokens[1]);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < tokens.length; i ++) {
                        builder.append(tokens[i]).append(" ");
                    }
                    textMessage.setText(builder.toString());
                    send(textMessage);
                } else {
                    this.invalidInput();
                }
                break;

            case "/info":
                switch (tokens.length) {

                    case 1:
                        InfoMessage infoMessage = new InfoMessage();
                        // число -1 будет обозначать запрос о себе
                        infoMessage.setSenderId(this.getSenderId());
                        infoMessage.setUserId(-1L);
                        send(infoMessage);
                        break;

                    case 2:
                        InfoMessage message = new InfoMessage();
                        message.setSenderId(this.getSenderId());
                        message.setUserId(tokens[1]);
                        send(message);
                        break;

                    default:
                        this.invalidInput();
                }
                break;

            case "/chat_list":
                if (tokens.length == 1) {
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setSenderId(this.getSenderId());
                    send(chatListMessage);
                } else {
                    this.invalidInput();
                }
                break;

            case "/chat_hist":
                if (tokens.length == 2) {
                    ChatHistMessage chatHistMessage = new ChatHistMessage();
                    chatHistMessage.setSenderId(this.getSenderId());
                    chatHistMessage.setChatId(tokens[1]);
                    send(chatHistMessage);
                } else {
                    this.invalidInput();
                }
                break;


            case "/chat_create":
                if (tokens.length > 1) {
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                    chatCreateMessage.setSenderId(this.getSenderId());
                    for (int i = 1; i < tokens.length; i++) {
                        chatCreateMessage.addId(tokens[i]);
                    }
                    send(chatCreateMessage);
                } else {
                    this.invalidInput();
                }
                break;

            case "/chat_history":
                if (tokens.length == 2) {
                    ChatHistMessage chatHistMessage = new ChatHistMessage();
                    chatHistMessage.setSenderId(this.getSenderId());
                    chatHistMessage.setChatId(tokens[1]);
                } else {
                    this.invalidInput();
                }
                break;

            default:
                this.invalidInput();
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {

        out.write(protocol.encode(msg));
        log.info("send: message sent {}", msg.toString());
        // flush очищает любые выходные буферы, завершая операцию вывода.
        out.flush();
    }


    /**
     * Молча (без проброса ошибок) закрываем соединение и освобождаем ресурсы
     */
    @Override
    public void close() {

        socketThread.interrupt();
        try {
            in.close();
            out.close();
        } catch (SocketException e) {
            return;
        } catch (IOException e) {
            log.error("Failed into close.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Client client = (Client) other;
        return Objects.equals(port, client.port) &&
                Objects.equals(protocol, client.protocol) &&
                Objects.equals(host, client.host);
    }

    public static void main(String[] args) throws Exception {

        Client client = null;
        try {

            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");

            client.initSocket();

            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    client.processInput(input);
                } catch (ProtocolException | IOException e) {
                    log.error("Client: main: Failed to process user input.", e);
                }
            }
        } catch (InvalidConfigurationException e) {
            log.error("Client: main: Failed to create client.", e);
        } catch (Exception e) {
            log.error("Client: main: Application failed.", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
