package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class RegistrationPanelView extends AbstractView{
    final static Logger LOGGER = LogManager.getLogger(RegistrationPanelView.class);
    public static final String SIGN_UP_ACTION_COMMAND="signup";
    public static final String GO_BACK_COMMAND = "back";

    private JPanel registrationPanel;
    private JPanel buttonsPanel;
    private JPanel mainPanel;
    private JButton registrationButton;
    private JButton backButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField repeatedPasswordField;
    private JLabel errorLabel;
    private JTextField serverIPAddressField;
    private JTextField nameTextField;



    RegistrationPanelView(){
    super();
    initialise();
    }

    public JPanel getRegistrationPanel() {
        if(registrationPanel==null) {
            registrationPanel = new JPanel();
            registrationPanel.setLayout(new BorderLayout());
            registrationPanel.setBackground(new Color(40,64,92));
            registrationPanel.add(getMainPanel(), BorderLayout.CENTER);
            addLabelField(getMainPanel(),"ip address of server",getServerIPAddressField());
            addLabelField(getMainPanel(),"Enter your nickname",getNameTextField());
            addLabelField(getMainPanel(),"Enter your e-mail",getLoginField());
            addLabelField(getMainPanel(),"Enter your password",getPasswordField());
            addLabelField(getMainPanel(),"Repeat your password",getRepeatedPasswordField());
            registrationPanel.add(getButtonsPanel(), BorderLayout.SOUTH);

        }
        return registrationPanel;
    }

    public JTextField getNameTextField() {
        if(nameTextField==null){
            nameTextField = new JTextField();
            nameTextField.setName("nameRegistrationTextField");
        }
        return nameTextField;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel==null){
            buttonsPanel = new JPanel();
            buttonsPanel.add(getBackButton());
            buttonsPanel.setBackground(new Color(40,64,92));
            buttonsPanel.add(getRegistrationButton());

        }
        return buttonsPanel;
    }

    public JPanel getMainPanel() {
        if(mainPanel==null){
            mainPanel=new JPanel();
            mainPanel.setBorder( BorderFactory.createEmptyBorder(MessengerApl.SCREEN_SIZE.width/17,
                    (int)(MessengerApl.SCREEN_SIZE.width/6.5),
                    MessengerApl.SCREEN_SIZE.width/11,
                    (int)(MessengerApl.SCREEN_SIZE.width/6.5) ));
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.pink);
            mainPanel.setForeground(Color.orange);
            mainPanel.setBackground(new Color(40,64,92));
        }
        return mainPanel;
    }

    public JButton getRegistrationButton() {
        if(registrationButton==null) {
            registrationButton = new JButton();
            registrationButton.setText("sign up");
            registrationButton.setName("registrationRegistrationButton");
            registrationButton.setActionCommand(SIGN_UP_ACTION_COMMAND);
            registrationButton.addActionListener(MessengerApl.getController());
        }
        return registrationButton;
    }

    public JButton getBackButton() {
        if(backButton==null) {
            backButton = new JButton();
            backButton.setText("back");
            backButton.setName("backRegistrationButton");
            backButton.setActionCommand(GO_BACK_COMMAND);
            backButton.addActionListener(MessengerApl.getController());
        }
        return backButton;
    }

    public JTextField getLoginField() {
        if(loginField==null){
            loginField = new JTextField(16);
            loginField.setName("loginRegistrationField");
        }
        return loginField;
    }

    public JPasswordField getPasswordField() {
        if(passwordField==null){
            passwordField = new JPasswordField(16);
            passwordField.setName("passwordRegistrationField");
        }
        return passwordField;

    }

    public JPasswordField getRepeatedPasswordField() {
        if(repeatedPasswordField==null){
            repeatedPasswordField = new JPasswordField(16);
            repeatedPasswordField.setName("repeatedPasswordRegistrationField");
        }
        return repeatedPasswordField;
    }

    public JLabel getErrorLabel() {
        if(errorLabel==null){
            errorLabel = new JLabel("<html><<div style='text-align: center;'>Check parameters</div></html>", SwingConstants.CENTER);
            errorLabel.setName("errorRegistrationLabel");
            errorLabel.setForeground(new Color(0,255,255));
        }
        return errorLabel;
    }
    private void setErrorLabelText(String errorText){

        getErrorLabel().setText(errorText);
    }

    public JTextField getServerIPAddressField() {
        if(serverIPAddressField==null){
            serverIPAddressField = new JTextField(12);
            serverIPAddressField.setName("serverRegistrationIPAddressField");
        }
        return serverIPAddressField;
    }

    public static RegistrationPanelView getInstance() {
        return RegistrationPanelViewHolder.INSTANCE;
    }

    private static class RegistrationPanelViewHolder{
        private  static  final RegistrationPanelView INSTANCE = new RegistrationPanelView();
    }

    @Override
    public void initialise() {
        this.setName("registrationPanelView");
        this.setLayout(new BorderLayout());
        this.add(getRegistrationPanel(), BorderLayout.CENTER);
        clearFields();
        initModel();
        InputMap im = getRegistrationButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        im.put(KeyStroke.getKeyStroke("ENTER"),"released");
    }

    @Override
    public void clearFields() {
        getLoginField().requestFocusInWindow();
        getErrorLabel().setVisible(false);
        getNameTextField().setText("");
        getLoginField().setText("");
        getPasswordField().setText("");
        getRepeatedPasswordField().setText("");
        getServerIPAddressField().setText(MessengerApl.getModel().getServerAddress());
        parent.getRootPane().setDefaultButton(getRegistrationButton());
    }

    @Override
    public void initModel() {
        getLoginField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getRegistrationButton());
    }


}
