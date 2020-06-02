package client;

import domain.Message;
import domain.User;
import domain.xml.MessageParser;
import domain.xml.UserParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import server.MessengerServer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static client.MessengerApl.*;
import static server.MessagesServerThread.*;


public class Utility {
    private static Logger LOGGER = LogManager.getLogger(Utility.class);

    public static <T extends Container> T findParent(Component component, Class<T> clazz) {
        if (component == null) {
            return null;
        }
        if (clazz.isInstance(component)) {
            return clazz.cast(component);
        } else {
            return findParent(component.getParent(), clazz);
        }
    }

    public static boolean usersGetAll(Model model) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(model.getServerAddress());
            try (Socket socket = new Socket(addr, MessengerServer.MAIN_PORT);
                 PrintWriter out = new PrintWriter(new BufferedWriter(
                         new OutputStreamWriter(socket.getOutputStream())), true);
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()))) {
                out.println(USERS);
                out.println(METHOD_GET);
                out.flush();
                String responseLine = in.readLine();
                StringBuilder userStr = new StringBuilder();
                while (!END_LINE_MESSAGE.equals(responseLine)) {
                    userStr.append(responseLine);
                    responseLine = in.readLine();
                }
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser parser = parserFactory.newSAXParser();
                List<User> users = new ArrayList();
                UserParser saxp = new UserParser(users);
                parser.parse(new ByteArrayInputStream(userStr.toString().getBytes()),saxp);
                if(model.getUsers()==null){
                    model.setUsers(users);
                    for (User user: users) {
                        model.getParent().getChatPanelView().usersListUpdate(user);
                    }
                }
                else {
                    for (User user:users) {
                        if(!model.getUsers().contains(user)){
                            model.getUsers().add(user);
                            model.getParent().getChatPanelView().usersListUpdate(user);
                        }
                        UserParser.getUser(user.getLogin(),model.getUsers()).setStatus(user.getStatus());
                    }
                }
                return true;
            } catch (IOException e) {
                LOGGER.error("socket error" + e.getMessage());
                return false;
            }
            catch (ParserConfigurationException | SAXException e){
                LOGGER.error(e.getMessage());
                return false;
            }
        } catch (UnknownHostException e) {
            LOGGER.error("none host address" + e.getMessage());
            return false;
        }

    }

    public static void messagesUpdate(MessengerApl apl) {
        //ИМЕННО ЗДЕСЬ ПЕРЕДАВАТЬ ДРУГОЙ ЗАПРОС
        InetAddress addr;
        try {
            addr = InetAddress.getByName(getModel().getServerAddress());
            try (Socket socket = new Socket(addr, MessengerServer.MAIN_PORT);
                 PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Model model = getModel();
                out.println(MESSAGES);
                out.println(METHOD_GET);
                long lastID = model.getLastMessageID();
                out.println(lastID);
                out.println(getModel().getLoggedUser().getLogin());
                out.flush();
                String responseLine = in.readLine();
                StringBuilder messStr = new StringBuilder();
                while (!END_LINE_MESSAGE.equals(responseLine)){
                    messStr.append(responseLine);
                    responseLine = in.readLine();
                }
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser parser = parserFactory.newSAXParser();
                List<Message> messages = new ArrayList<Message>(){
                    @Override
                    public String toString() {
                        return this.stream().map(Message::toString).collect(Collectors.joining("\n"));
                    }};
                AtomicInteger id = new AtomicInteger(0);
                MessageParser saxp = new MessageParser(id, messages,model.getUsers());
                parser.parse(new ByteArrayInputStream(messStr.toString().getBytes()),saxp);
                if(messages.size()>0){
                    if(model.getLoggedUser()!=null) {
                        model.addMessages(messages);
                        model.setLastMessageID(id.longValue());
                        LOGGER.trace("List of new messages" + messages.toString());
                    }
            }
            }
            catch (IOException e) {
                LOGGER.error("Socket error"+e.getMessage());
            }
            catch (ParserConfigurationException | SAXException e) {
                LOGGER.error("Parse exception"+e.getMessage());
            }
        }
        catch (UnknownHostException e) {
            LOGGER.error("Unknown host"+e.getMessage());
        }
        catch (Exception e){
            usersGetAll(getModel());
            usersGetAll(getModel());
            LOGGER.error("Some troubles" + e.getMessage());
        }
    }
}
