package client;

import domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;

public class ChatPanelView extends AbstractView {

    final static Logger LOGGER = LogManager.getLogger(ChatPanelView.class);
    private static final String REFRESH_USERS = "refresh";
    private JPanel chatPanel;

    public static final String SEND_ACTION_COMMAND = "send";
    public static final String LOGOUT_ACTION_COMMAND = "logout";

    private JScrollPane messagesListPanel;
    private JTextPane messagesTextPane;
    private JPanel textMessagePanel;
    private JButton sendMessageButton;
    public JScrollPane usersListPanel;
    private JTextField textMessageField;
    private JButton logoutButton;
    private JLabel userCompanion;
    private JList<User> userList;
    private DefaultListModel<User> listModel;


    private ChatPanelView(){
        super();
        initialise();
    }

    public static ChatPanelView getInstance() {
        return ChatPanelViewHolder.INSTANCE;
    }

    public void clearMessageTextPane(){
        getMessagesTextPane().setText("");
    }

    public void updateMessageTextPane(String messages) {
        HTMLDocument document = (HTMLDocument) getMessagesTextPane().getStyledDocument();
        HTMLEditorKit kit = (HTMLEditorKit)getMessagesTextPane().getEditorKit();
        try {
            kit.insertHTML(document,document.getLength(),messages,0,0,null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
        getMessagesTextPane().setCaretPosition(document.getLength());

    }

    private static class ChatPanelViewHolder{
        private  static  final ChatPanelView INSTANCE = new ChatPanelView();
    }


    @Override
    public void initialise() {
        this.setName("chatPanelView");
        this.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.add(getUserCompanion(), BorderLayout.CENTER);
        header.add(getLogoutButton(), BorderLayout.EAST);
        header.setBackground(new Color(40,64,92));
        this.add(header, BorderLayout.NORTH);
        this.add(getMessagesListPanel(), BorderLayout.CENTER);
        this.add(getTextMessagePanel(), BorderLayout.SOUTH);
        this.add(getUsersListPanel(), BorderLayout.WEST);
        this.add(usersListPanel.add(getUserList()), BorderLayout.WEST);
        //JLIST ADD//
        InputMap im = getSendMessageButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"),"released");
    }

    @Override
    public void clearFields() {
        getMessagesTextPane().setText("");
        getTextMessageField().setText("");
        getUserCompanion().setText("");
    }

    public void initModel() {
        MessengerApl.getModel().setLastMessageText("");
        getTextMessageField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getSendMessageButton());
    }

    public JPanel getChatPanel() {
        if(chatPanel==null){
            chatPanel = new JPanel();
        }
        return chatPanel;
    }



    public JScrollPane getMessagesListPanel() {
        if(messagesListPanel==null){
            messagesListPanel = new JScrollPane(getMessagesTextPane());
            messagesListPanel.setSize(getMaximumSize());
            messagesListPanel
                    .setVerticalScrollBarPolicy(ScrollPaneConstants
                            .VERTICAL_SCROLLBAR_AS_NEEDED);
            messagesListPanel.setBackground(new Color(225,243,250));
        }
        return messagesListPanel;
    }

    public JTextPane getMessagesTextPane() {
        if(messagesTextPane==null) {
            messagesTextPane = new JTextPane();
            messagesTextPane.setContentType("text/html");
            messagesTextPane.setEditable(false);
            messagesTextPane.setName("messagesTextArea");
            ((DefaultCaret)messagesTextPane.getCaret())
                    .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            messagesTextPane.setBackground(new Color(225,243,250));
        }
        return messagesTextPane;
    }

    public JButton getSendMessageButton() {
        if(sendMessageButton==null){
            sendMessageButton = new JButton();
            sendMessageButton.setText("Send");
            sendMessageButton.setName("sendMessageButton");
            sendMessageButton.setForeground(Color.white);
            sendMessageButton.setBackground(new Color(40,64,92));
            sendMessageButton.setActionCommand(SEND_ACTION_COMMAND);
            sendMessageButton.addActionListener(MessengerApl.getController());
            sendMessageButton.setEnabled(false);

        }
        return sendMessageButton;
    }



    public JTextField getTextMessageField() {
        if(textMessageField==null){
            textMessageField = new JTextField(20);
            textMessageField.setName("textMessageField");
        }
        return textMessageField;
    }

    public JButton getLogoutButton() {
        if(logoutButton==null){
            logoutButton = new JButton();
            logoutButton.setText("logout");
            logoutButton.setName("logoutButton");
            logoutButton.setForeground(Color.white);
            logoutButton.setBackground(new Color(40,64,92));
            logoutButton.setActionCommand(LOGOUT_ACTION_COMMAND);
            logoutButton.addActionListener(MessengerApl.getController());
        }
        return logoutButton;
    }

    public JLabel getUserCompanion() {
        if(userCompanion==null){
            userCompanion = new JLabel();
            userCompanion.setText("");
            userCompanion.setHorizontalAlignment(0);
        }
        return userCompanion;
    }

    public JPanel getTextMessagePanel() {
        if (textMessagePanel == null) {
            textMessagePanel = new JPanel();
            textMessagePanel.setLayout(
                    new BoxLayout(textMessagePanel, BoxLayout.X_AXIS));
            textMessagePanel.setBackground(new Color(40,64,92));
            addLabelField(textMessagePanel,"Enter your message",getTextMessageField(),
                    new Dimension(MessengerApl.SCREEN_SIZE.width/8,textMessagePanel.getHeight()), JLabel.CENTER);
            textMessagePanel.add(getSendMessageButton());
        }
        return textMessagePanel;
    }

    public JScrollPane getUsersListPanel() {
        if(usersListPanel==null){
            usersListPanel = new JScrollPane();
            usersListPanel.add(getUserList());
        }
        return usersListPanel;
    }

    public DefaultListModel<User> getListModel() {
        if(listModel==null){
            listModel = new DefaultListModel<>();
        }
        return listModel;
    }

    public void usersListUpdate(User user){
        getListModel().addElement(user);
        getUserList().setModel(getListModel());
        getUserList().repaint();
        getUsersListPanel().repaint();
    }

    public JList<User> getUserList() {
        if(userList==null){
            userList = new JList<User>(new DefaultListModel<>()){
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(MessengerApl.SCREEN_SIZE.width/8,super.getPreferredSize().height);
                }
            };
            userList.setBackground(new Color(32,105,137));
            userList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
            userList.setFixedCellHeight(25);
            userList.setFixedCellWidth(MessengerApl.SCREEN_SIZE.width/8);
            userList.setVisible(true);
            userList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
            userList.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
            userList.addListSelectionListener(new ChangeUserSelection(this));
        }
        return userList;
    }
}