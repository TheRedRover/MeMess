package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class LoginPanelView extends AbstractView{
    final static Logger LOGGER = LogManager.getLogger(LoginPanelView.class);
    public static final String LOGGIN_ACTION_COMMAND = "login";
    public static final String REGISTRATION_ACTION_COMMAND = "registration";

    private JPanel loginPanel;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JButton loginButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JTextField serverIPAddressField;
    private JLabel errorLabel;
    private JButton registrationButton;

    //singleton
    private LoginPanelView(){
        super();
        initialise();
    }

    public static LoginPanelView getInstance(){
        return LoginPanelViewHolder.INSTANCE;
    }

    private static class LoginPanelViewHolder{
        private  static  final LoginPanelView INSTANCE = new LoginPanelView();
    }

    public JPanel getLoginPanel() {
        if(loginPanel==null){
            loginPanel = new JPanel();
            loginPanel.setLayout(new BorderLayout());
            loginPanel.setBackground(new Color(40,64,92));
            loginPanel.add(getMainPanel(), BorderLayout.CENTER);
            addLabelField(getMainPanel(),"ip address of server",getServerIPAddressField());
            addLabelField(getMainPanel(),"login of user",getLoginField());
            addLabelField((getMainPanel()),"password",getPasswordField());
            loginPanel.add(getButtonPanel(), BorderLayout.SOUTH);
            loginPanel.setForeground(Color.white);

        }
        return loginPanel;
    }

    public JPanel getButtonPanel() {
        if(buttonPanel==null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getRegistrationButton());
            buttonPanel.add(getLoginButton());
//            buttonPanel.setBackground(new Color(97,221,240));
            buttonPanel.setBackground(new Color(40,64,92));
        }
        return buttonPanel;
    }

    public JPanel getMainPanel() {
        if (mainPanel==null){
            mainPanel=new JPanel();
            mainPanel.setBorder( BorderFactory.createEmptyBorder(MessengerApl.SCREEN_SIZE.width/17,
                    (int)(MessengerApl.SCREEN_SIZE.width/6.5),
                    MessengerApl.SCREEN_SIZE.width/11,
                    (int)(MessengerApl.SCREEN_SIZE.width/6.5) ));
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(new Color(40,64,92));
            mainPanel.setForeground(Color.white);
        }
        return mainPanel;
    }

    public JButton getLoginButton() {
        if (loginButton==null)
        {
            loginButton = new JButton();
            loginButton.setText("Sign in");
            loginButton.setName("loginButton");
            loginButton.setActionCommand(LOGGIN_ACTION_COMMAND);
            loginButton.addActionListener(MessengerApl.getController());
        }
        return loginButton;
    }

    public JTextField getLoginField() {
        if(loginField==null){
            loginField = new JTextField(16);
            loginField.setName("loginLoginField");
        }
        return loginField;
    }

    public JPasswordField getPasswordField() {
        if(passwordField == null) {
            passwordField = new JPasswordField(16);
            passwordField.setName("passwordLoginField");
        }
        return passwordField;
    }

    public JTextField getServerIPAddressField() {
        if(serverIPAddressField==null){
            serverIPAddressField = new JTextField(12);
            serverIPAddressField.setName("serverLoginIPAddressField");
        }
        return serverIPAddressField;
    }


    public JLabel getErrorLabel() {
        if(errorLabel==null){
            errorLabel = new JLabel("<html><<div style='text-align: center;'>Check parameters</div></html>", SwingConstants.CENTER);
            errorLabel.setName("errorLoginLabel");
            errorLabel.setForeground(new Color(0,255,255));
        }
        return errorLabel;
    }
    private void setErrorLabelText(String errorText){

        getErrorLabel().setText(errorText);
    }


    public JButton getRegistrationButton() {
        if (registrationButton==null)
        {
            registrationButton = new JButton();
            registrationButton.setText("Sign Up");
            registrationButton.setName("registrationButton");
            registrationButton.setActionCommand(REGISTRATION_ACTION_COMMAND);
            registrationButton.addActionListener(MessengerApl.getController());
        }
        return registrationButton;
    }


    @Override
    public void initialise() {
        this.setName("loginPanelView");
        this.setLayout(new BorderLayout());
        this.add(getLoginPanel(), BorderLayout.CENTER);
        clearFields();
        initModel();
        getLoginField().requestFocusInWindow();
        InputMap im = getLoginButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");

    }
    public void initModel(){
        MessengerApl.getModel().setCurrentUser(null);
        MessengerApl.getModel().setLoggedUser(null);
        getLoginField().requestFocusInWindow();
        clearFields();
        parent.getRootPane().setDefaultButton(getLoginButton());
    }

    @Override
    public void clearFields() {
        getErrorLabel().setVisible(false);
        getLoginField().setText("");
        getPasswordField().setText("");
        getServerIPAddressField().setText(MessengerApl.getModel().getServerAddress());
    }
}
