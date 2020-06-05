package client;

import domain.Message;
import domain.xml.MessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import server.MessagesServerThread;
import server.MessengerServer;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class SendMessageCommand implements Command {
    final Logger LOGGER = LogManager.getLogger(SendMessageCommand.class);
    ChatPanelView panel;
    MessengerApl apl;
    private InetAddress addr;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SendMessageCommand(MessengerApl apl, ChatPanelView view) {
        this.apl=apl;
        this.panel =view;
    }

    @Override
    public void execute() {
        if (!panel.getTextMessageField().getText().equals("")&& !panel.getTextMessageField().getText().matches("[\\s]*")) {
            try {
                addr = InetAddress.getByName(MessengerApl.getModel().getServerAddress());
                socket = new Socket(addr, MessengerServer.MAIN_PORT);
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (NullPointerException|IOException e) {
                JOptionPane.showMessageDialog(panel, "Check your connection");
            }
            try {
                String result;
                int k = 0;
                do {
                    out.println(MessagesServerThread.MESSAGES);
                    out.println(MessagesServerThread.METHOD_PUT);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();
                    List<Message> messages = new ArrayList<>();
                    messages.add(
                            Message.newMessage().text(panel.getTextMessageField()
                                    .getText()).user(MessengerApl.getModel()
                                    .getLoggedUser()).to_user(MessengerApl.getModel().getCurrentUser())
                                    .moment(Calendar.getInstance()).build()
                    );
                    String xmlContent = MessageBuilder.buildDocument(document, messages);
                    out.println(xmlContent);
                    out.println(MessagesServerThread.END_LINE_MESSAGE);
                    result = in.readLine();
                    k++;
                } while ("OK".equals(result)||k>5);
            } catch (IOException | ParserConfigurationException|NullPointerException e) {
                LOGGER.error("Send message error" + e.getMessage());
            }finally {
                try {
                    in.close();
                    out.close();
                    socket.close();

                } catch (IOException|NullPointerException e) {
                    LOGGER.error("Socket close error: " + e.getMessage());
                }
            }
        }
        panel.getTextMessageField().setText("");
        panel.getTextMessageField().requestFocus();
    }
    }

