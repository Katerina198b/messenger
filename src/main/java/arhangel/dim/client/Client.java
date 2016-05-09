package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
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

    /**
     * Механизм логирования позволяет более гибко управлять записью данных в лог (консоль, файл и тд)
     */
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

    public void initSocket() throws IOException {
        Socket socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        /**
         * Инициализируем поток-слушатель. Синтаксис лямбды скрывает создание анонимного класса Runnable
         */
        socketThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 64];
            log.info("Starting listener thread...");
            /*
             * Класс thread содержит метод урпавлеиня потоками. interrupt - прерванный
             */
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Здесь поток блокируется на ожидании данных

                    //if (in.available() > 0)
                    int read = in.read(buf);
                    if (read > 0) {

                        // По сети передается поток байт, его нужно раскодировать с помощью протокола
                        Message msg = protocol.decode(Arrays.copyOf(buf, read));
                        onMessage(msg);
                    }
                } catch (SocketException e) {
                    return;
                } catch (IOException | ProtocolException e) {
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
        log.info("Message received: {}", msg);
        msg.toString();

    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        log.info("Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {

            case "/login":
                if (tokens.length == 3) {
                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.setType(Type.MSG_LOGIN);
                    loginMessage.setLogin(tokens[1]);
                    loginMessage.setPassword(tokens[2]);
                    send(loginMessage);
                } else {
                    log.error("incorrect input: you enter {} words, but had 3 words", tokens.length);
                }
                break;

            case "/help":
                HelpMessage helpMessage = new HelpMessage();
                helpMessage.setType(Type.MSG_INFO);
                send(helpMessage);
                break;

            case "/text":
                if (tokens.length == 3) {
                    TextMessage textMessage = new TextMessage();
                    textMessage.setType(Type.MSG_TEXT);
                    textMessage.setChatId(tokens[1]);
                    textMessage.setText(tokens[2]);
                    send(textMessage);
                } else {
                    log.error("incorrect input: you enter {} words, but had 3 words", tokens.length);
                }
                break;

            case "/info":
                switch (tokens.length) {
                    case 1:
                        InfoMessage infoMessage = new InfoMessage();
                        // число -1 будет обозначать запрос о себе
                        Integer userId = -1;
                        infoMessage.setType(Type.MSG_INFO);
                        infoMessage.setUserId(userId.toString());
                        send(infoMessage);
                        break;
                    case 2:
                        InfoMessage message = new InfoMessage();
                        Integer id = Integer.valueOf(tokens[1]);
                        message.setType(Type.MSG_INFO);
                        message.setUserId(id.toString());
                        send(message);
                        break;
                    default:
                        log.error("incorrect input: you enter {} " +
                                "words, but had 3 or 2 words", tokens.length);
                }
                break;

            case "/chat_list":
                if (tokens.length == 1) {
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setType(Type.MSG_CHAT_LIST);
                    send(chatListMessage);
                } else {
                    log.error("incorrect input: you enter {} words, but had 1 word", tokens.length);
                }
                break;

            case "/chat_create":
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setType(Type.MSG_CHAT_CREATE);
                for (int i = 1; i < tokens.length + 1; i++) {
                    chatCreateMessage.addId(tokens[i]);
                }
                send(chatCreateMessage);
                break;

            case "/chat_history":
                if (tokens.length == 2) {
                    ChatHistMessage chatHistMessage = new ChatHistMessage();
                    chatHistMessage.setType(Type.MSG_CHAT_HIST);
                    chatHistMessage.setChatId(tokens[1]);
                } else {
                    log.error("incorrect input: you enter {} words, but had 2 words", tokens.length);
                }
                break;

            default:
                log.error("Invalid input: " + line);
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        // flush очищает любые выходные буферы, завершая операцию вывода.
        out.flush();
    }


    /**
     * Молча (без проброса ошибок) закрываем соединение и освобождаем ресурсы
     */
    @Override
    public void close() {
        // TODO: написать реализацию. Закройте ресурсы и остановите поток-слушатель
        socketThread.interrupt();
        try {
            in.close();
            out.close();
        } catch (SocketException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        Client client = null;
        // Пользуемся механизмом контейнера
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create client", e);
            return;
        }
        try {
            client.initSocket();

            // Цикл чтения с консоли
            /*
             * В классе есть методы для чтения очередного символа заданного
             * типа со стандартного потока ввода, а также для проверки
             * существования такого символа.
             */
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
                    log.error("Failed to process user input", e);
                }
            }
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
