package client;

import domain.User;
import domain.xml.UserBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import server.MessagesServerThread;
import server.MessengerServer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SignUpCommand implements Command {
    final Logger LOGGER = LogManager.getLogger(SignUpCommand.class);
    RegistrationPanelView view;
    MessengerApl apl;
    private InetAddress addr;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SignUpCommand(RegistrationPanelView view) {
        try {
            this.view = view;
            addr = InetAddress.getByName(MessengerApl.getModel().getServerAddress());
            socket = new Socket(addr, MessengerServer.MAIN_PORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOGGER.error("Unknown host"+e.getMessage());
        }
    }
    @Override
    public void execute() {
        try {
            addr = InetAddress.getByName(MessengerApl.getModel().getServerAddress());
            socket = new Socket(addr, MessengerServer.MAIN_PORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOGGER.error("Unknown host"+e.getMessage());
        }
        try {
            String result;
            int k=0;
            do {
                out.println(MessagesServerThread.USERS);
                out.println(MessagesServerThread.METHOD_PUT);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();
                List<User> users = new ArrayList<>();
                User user = new User();
                user.setName(view.getNameTextField().getText());
                user.setLogin(view.getLoginField().getText());
                String pass = new String(view.getPasswordField().getPassword());
                user.setStatus(false);
                user.setPassword(pass);
                users.add(user);
                String xmlContent = UserBuilder.buildDocument(document,users);
                out.println(xmlContent);
                out.println(MessagesServerThread.END_LINE_MESSAGE);
                result = in.readLine();
                k++;
            } while ("OK".equals(result));
                Command command = new RegistrationErrorCommand(view,"Succesfully");
                command.execute();
    } catch (ParserConfigurationException | IOException e) {
            LOGGER.error("sign up error"+ e.getMessage());
        }
    }}
