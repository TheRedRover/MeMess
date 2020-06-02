package server;

import domain.Message;
import domain.TaskUserStatusUpdate;
import domain.User;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import domain.xml.UserBuilder;
import domain.xml.UserParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MessagesServerThread extends Thread {

    final static Logger LOGGER = LogManager.getLogger(MessagesServerThread.class);

    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String END_LINE_MESSAGE = "END";
    public static final String MESSAGES = "M";
    public static final String USERS = "U";

    private final Socket socket;
    private final AtomicInteger messageId;
    private final Map<Long, Message> messagesList;
    private  final List<User> usersList;
    private final BufferedReader in;
    private final PrintWriter out;


    public AtomicInteger getMessageId() {
        return messageId;
    }

    public Map<Long, Message> getMessagesList() {
        return messagesList;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public MessagesServerThread(Socket socket, AtomicInteger messageId, Map<Long, Message> messagesList, List<User> usersList) throws IOException {
        this.socket = socket;
        this.messageId = messageId;
        this.messagesList = messagesList;
        this.usersList = usersList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        start();
    }

    @Override
    public void run() {
        try{
            LOGGER.debug("New message socket thread started");
            String requestLine = in.readLine();
            LOGGER.debug(requestLine);
            switch (requestLine) {
                case MESSAGES: {
                    requestLine = in.readLine();
                    switch (requestLine){
                        case METHOD_GET:

                            LOGGER.debug("get");
                            Long lastId = Long.valueOf(in.readLine());
                            String login = in.readLine();
                            LOGGER.debug(lastId);
                            List<Message> newMessages = messagesList
                                    .entrySet().stream().filter(message -> message.getKey()
                                            .compareTo(lastId) > 0)
                                    .filter(message->message.getValue().getUser().getLogin().equals(login)||
                                            message.getValue().getTo_user().getLogin().equals(login))
                                    .map(Map.Entry::getValue).collect(Collectors.toList());
                            LOGGER.debug(newMessages);
                            User loggedUser = UserParser.getUser(login,usersList);
                            assert loggedUser != null;
                            loggedUser.setStatus(true);
                            if(loggedUser.getTimer()!=null)
                                loggedUser.getTimer().cancel();
                            loggedUser.setTimer(new Timer());
                            loggedUser.getTimer().schedule(new TaskUserStatusUpdate(loggedUser),2000);
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document document = builder.newDocument();

                            String xmlContent = MessageBuilder.buildDocument(document, newMessages);
                            LOGGER.trace("Echoing:" + xmlContent);
                            out.println(xmlContent);
                            out.println(END_LINE_MESSAGE);
                            out.flush();
                            break;
                        case METHOD_PUT: {
                            LOGGER.debug("put");
                            requestLine = in.readLine();
                            StringBuilder messStr = new StringBuilder();
                            while (!END_LINE_MESSAGE.equals(requestLine)) {
                                messStr.append(requestLine);
                                requestLine = in.readLine();
                            }
                            LOGGER.debug(messStr);
                            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                            SAXParser parserMes = saxParserFactory.newSAXParser();
                            List<Message> messages = new ArrayList<>();
                            MessageParser saxpMess = new MessageParser(messageId, messages, usersList);
                            InputStream isM = new ByteArrayInputStream(messStr.toString().getBytes());
                            parserMes.parse(isM, saxpMess);
                            for (Message message : messages) {
                                messagesList.put(message.getId(), message);
                            }
                            isM.close();
                            LOGGER.trace("Echoing" + messages);
                            out.println("Ok");
                            out.flush();
                            out.close();
                            break;
                        }
                        default:{
                            LOGGER.info("Unknow request");
                            out.println("BAD REQUEST");
                            out.flush();
                            break;
                        }
                    }
                }
                case USERS:{
                    requestLine = in.readLine();
                    switch (requestLine){
                        case METHOD_GET:
                            LOGGER.debug("get");
                            List<User> newUsers = usersList;
                            LOGGER.debug(usersList);
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document document = builder.newDocument();
                            String xmlContent = UserBuilder.buildDocument(document, usersList);
                            LOGGER.trace("Echoing:");
                            out.println(xmlContent);
                            out.println(END_LINE_MESSAGE);
                            out.flush();
                            break;
                        case METHOD_PUT:
                            LOGGER.debug(METHOD_PUT);
                            requestLine = in.readLine();
                            StringBuilder userStr = new StringBuilder();
                            while (! END_LINE_MESSAGE.equals(requestLine)){
                                userStr.append(requestLine);
                                requestLine = in.readLine();
                            }
                            LOGGER.debug(userStr);
                            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                            SAXParser parserUsers = saxParserFactory.newSAXParser();
                            List<User> users = new ArrayList<>();
                            UserParser saxpUser = new UserParser( users);
                            InputStream isM = new ByteArrayInputStream(userStr.toString().getBytes());
                            parserUsers.parse(isM,saxpUser);
                            usersList.addAll(users);
                            isM.close();
                            LOGGER.trace("Echoing"+users);
                            out.println("Ok");
                            out.flush();
                            out.close();
                            break;
                        default:
                            LOGGER.info("Unknow request");
                            out.println("BAD REQUEST");
                            out.flush();

                            break;
                    }

                }
            }

        }
        catch(Exception e){LOGGER.error(e.getMessage());
        out.println("ERROR");
        out.flush();
        }
        finally {
            try {
                LOGGER.debug("Socket closing...");
                LOGGER.debug("Strams closing...");
                in.close();
                out.close();
                socket.close();
            }
            catch (IOException e) {
                LOGGER.error("Socket not closed");
            }
        }
    }
}
