package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
            // откуда это магическое число?
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
                } catch (Exception e) {
                    // ioexceptions connectionrefused
                    log.error("Failed to process connection: {}", e);
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
                    TextMessage sendMessage = new TextMessage();
                    sendMessage.setType(Type.MSG_TEXT);
                    sendMessage.setText(tokens[1]);
                    send(sendMessage);
                } else {
                    log.error("incorrect input: you enter {} words, but had 3 words", tokens.length);
                }
                break;
            case "/info":
                switch (tokens.length) {
                    case 1:
                        //o себе
                    case 2:
                        InfoMessage sendMessage = new InfoMessage();
                        Integer userId = Integer.valueOf(tokens[1]);
                        sendMessage.setType(Type.MSG_INFO);
                        sendMessage.setUserId(userId);
                        send(sendMessage);
                        break;
                    default:
                        log.error("incorrect input: you enter {} " +
                                "words, but had 3 or 2 words", tokens.length);
                }
                break;
            case "/chat_list":
                // что-нить

            case "/chat_create":


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
        socketThread.currentThread().interrupt();
        try {
            in.close();
            out.close();
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
