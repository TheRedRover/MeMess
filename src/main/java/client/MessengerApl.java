package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;


public class MessengerApl extends JFrame {
    public static final short DELAY_MESSAGES = 100;
    public static final short PERIOD_MESSAGES = 1000;
    public static final short DELAY_USERS = 200;
    public static final int PERIOD_USERS = 2000;

    final static Logger LOGGER = LogManager.getLogger(MessengerApl.class);

    private static final Model MODEL;
    private static final Controller CONTROLLER;
    private static final ViewFactory VIEWS;
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();


    static {
        MODEL = Model.getInstance();
        CONTROLLER = Controller.getInstance();
        VIEWS = ViewFactory.getInstance();
        LOGGER.trace("MVC instanced"+MODEL+CONTROLLER+VIEWS);
    }

    private Timer timer;

    public MessengerApl(){
        super();
        initialise();
    }

    public static void main(String[] args) {
        JFrame frame = new MessengerApl();
        frame.setVisible(true);
        frame.repaint();
    }

    public static Model getModel() {
        return MODEL;
    }

    public static Controller getController() {
        return CONTROLLER;
    }

    public static ViewFactory getViews() {
        return VIEWS;
    }

    public Timer getTimer() {
        return this.timer;
    }

    private void initialise(){
        AbstractView.setParent(this);
        MODEL.setParent(this);
        MODEL.initialise();
        CONTROLLER.setParent(this);
        VIEWS.viewRegister("login",LoginPanelView.getInstance());
        VIEWS.viewRegister("chat", ChatPanelView.getInstance());
        VIEWS.viewRegister("registration", RegistrationPanelView.getInstance());
        //VIEWS.viewRegister("explorer",explorerPanelView().getInstance());
        //VIEWS.viewRegister("registration",registrationPanelView.getInstance());
        timer = new Timer("Server request for update messages");
        this.setBackground(Color.pink);
        this.setForeground(Color.orange);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize((int) (SCREEN_SIZE.width/2.5), (SCREEN_SIZE.height/2));
        this.setLocationRelativeTo(null);
        this.setTitle("MeMess");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(getLoginPanelView(), BorderLayout.CENTER);
        this.setContentPane(contentPanel);
    }

    LoginPanelView getLoginPanelView() {
        LoginPanelView loginPanelView = VIEWS.getView("login");
        loginPanelView.initModel();
        return loginPanelView;
    }

    RegistrationPanelView getRegistrationPanelView(){
        RegistrationPanelView registrationPanelView = VIEWS.getView("registration");
        registrationPanelView.initModel();
        return registrationPanelView;
    }


    ChatPanelView getChatPanelView(){
        ChatPanelView chatPanelView = VIEWS.getView("chat");
        chatPanelView.initModel();
        return chatPanelView;
    }

    public void setTimer(Timer timer) {
        this.timer=timer;
    }

    private void showPanel(JPanel panel){
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        getChatPanelView().getTextMessageField().requestFocusInWindow();
        panel.repaint();
    }

    public void showChatPanelView() {
        showPanel(getChatPanelView());
        InputMap im = getChatPanelView().getSendMessageButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    public void showLoginPanelView() {
        showPanel(getLoginPanelView());
        InputMap im = getLoginPanelView().getLoginButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        getLoginPanelView().getLoginField().requestFocusInWindow();
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    public void showRegistrationPanelView() {
        showPanel(getRegistrationPanelView());
        InputMap im = getRegistrationPanelView().getRegistrationButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        getRegistrationPanelView().getNameTextField().requestFocusInWindow();
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }
}
