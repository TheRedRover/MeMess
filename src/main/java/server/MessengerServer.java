package server;

import domain.Message;
import domain.User;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import domain.xml.UserBuilder;
import domain.xml.UserParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MessengerServer {

    public static final int MAIN_PORT = 7070;
    public static final String ADDRESS = "127.0.0.1";
    final static Logger LOGGER = LogManager.getLogger(MessengerServer.class);
    private static final int SERVER_TIMEOUT = 500 ;
    private static final String XML_FILE_NAME_USERS = "users.xml";
    private static final String XML_FILE_NAME_MESSAGES = "messages.xml";
    private static volatile boolean stop = false;
    private static AtomicInteger id = new AtomicInteger(0);
    private static Map<Long, Message> messagesList = Collections.synchronizedSortedMap(new TreeMap<>());
    private  static List<User> usersList = Collections.synchronizedList(new LinkedList<>());

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        //Load messages from XML file
        LoadUserXMLFile();
        LoadMessagesXMLFile(usersList);

        //Run thread for quit command
        quitCommandThread();
        ServerSocket serverSocket =  new ServerSocket(MAIN_PORT);
//        ServerSocketChannel serverSocketUsers = new ServerSocket(ADDITIONAL_PORT);
        //(SSLServerSocketFactory.getDefault()).createServerSocket
        LOGGER.info("Server messages started on port: " + MAIN_PORT);

        //loop request is socket with timeout
        while (!stop){
            serverSocket.setSoTimeout(SERVER_TIMEOUT);
//            serverSocketUsers.setSoTimeout(SERVER_TIMEOUT);
            Socket socket;

            try {
                socket = serverSocket.accept();
                try {
                    new MessagesServerThread(socket, id, messagesList, usersList);
                }
                catch (IOException e){
                    LOGGER.error("IO error");
                    socket.close();
                }
            }
            catch (SocketTimeoutException e){
            }
        }

        //write message into xml file
        saveMessagesXMLFile();
        saveUsersXMLFile();
        LOGGER.info("Server stopped ");
        serverSocket.close();
    }

    private static void saveUsersXMLFile() throws ParserConfigurationException, IOException {
        for (User user:
             usersList) {
            if(user.getTimer()!=null)
                user.getTimer().cancel();
            user.setStatus(false);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        String xmlContent = UserBuilder.buildDocument(document,usersList);
        OutputStream stream = new FileOutputStream(new File(XML_FILE_NAME_USERS));
        OutputStreamWriter out = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        out.write(xmlContent+"\n");
        out.flush();
        out.close();
    }

    private static void saveMessagesXMLFile() throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        String xmlContent = MessageBuilder.buildDocument(document,messagesList.values());
        OutputStream stream = new FileOutputStream(new File(XML_FILE_NAME_MESSAGES));
        OutputStreamWriter out = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        out.write(xmlContent+"\n");
        out.flush();
        out.close();
    }
    private static void LoadMessagesXMLFile(List<User> users) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parserMes = factory.newSAXParser();
        List<Message> messages = new ArrayList<>();
        MessageParser saxpMess = new MessageParser(id,messages,users);
        InputStream isM = new ByteArrayInputStream(Files.readAllBytes((Paths.get(XML_FILE_NAME_MESSAGES))));
        parserMes.parse(isM,saxpMess);
        for(Message message:messages){
            messagesList.put(message.getId(),message);
        }
        id.incrementAndGet();
        isM.close();
    }

    private static void LoadUserXMLFile() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parserUser= factory.newSAXParser();
        List<User> users = new ArrayList<>();
        UserParser saxpUser = new UserParser(users);
        InputStream isU = new ByteArrayInputStream(Files.readAllBytes((Paths.get(XML_FILE_NAME_USERS))));
        parserUser.parse(isU,saxpUser);
        usersList.addAll(users);
        isU.close();
    }

    private static void quitCommandThread() {
        new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String buf;
                try {
                    buf = br.readLine();
                    if("quit".equals(buf)){
                        stop = true;
                        break;
                    } else {
                        LOGGER.warn("Type 'quit' for server termination");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
