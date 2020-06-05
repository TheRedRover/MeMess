package client;


import domain.User;
import domain.xml.UserParser;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static client.ChatPanelView.LOGOUT_ACTION_COMMAND;
import static client.ChatPanelView.SEND_ACTION_COMMAND;
import static client.LoginPanelView.REGISTRATION_ACTION_COMMAND;
import static client.MessengerApl.*;


public class Controller implements ActionListener {

    private MessengerApl parent;
    private Command command;

    private Controller(){}

    private static class ControllerHolder{
        private static final Controller INSTANCE = new Controller();
    }

    public static Controller getInstance() {
        return ControllerHolder.INSTANCE;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action(e);
        command.execute();
    }

    private void action(ActionEvent e) {
        String commandName = e.getActionCommand();
        switch (commandName){
            case RegistrationPanelView.SIGN_UP_ACTION_COMMAND: {
                RegistrationPanelView view = Utility
                        .findParent((Component) e.getSource(), RegistrationPanelView.class);
                String pass1 = new String(view.getPasswordField().getPassword());
                String pass2=new String(view.getRepeatedPasswordField().getPassword());
                if (!EmailValidator.getInstance()
                        .isValid(view.getLoginField().getText()) ||
                        !InetAddressValidator.getInstance().isValid(view
                                .getServerIPAddressField().getText()) ||
                        !pass1.equals(pass2)){
                    command = new RegistrationErrorCommand(view,
                            "<html><<div style='text-align: center;'>Check entered</div></html>");
                } else {
                    int k=0;
                    boolean c=false;
                    while(!c)
                    {
                        c=Utility.usersGetAll(getModel());
                        k++;
                        if(k>3) {
                            command = new RegistrationErrorCommand(view,
                                    "<html><<div style='text-align: center;'>Connection error</div></html>");
                            c = true;
                        }
                    }
                    if(Utility.usersGetAll(getModel())){
                    if(UserParser.getUser(parent.getRegistrationPanelView().getLoginField().getText()
                            , getModel().getUsers())!=null)
                        command = new RegistrationErrorCommand(view,
                                "<html><<div style='text-align: center;'>Login is used</div></html>");
                    else {
                        command = new SignUpCommand(view);
                    }
                }}
                break;
            }
            case RegistrationPanelView.GO_BACK_COMMAND:
                command = new ShowLoginViewCommand(parent);
                break;

            case LoginPanelView.LOGGIN_ACTION_COMMAND: {
                LoginPanelView view = Utility.findParent((Component) e.getSource(), LoginPanelView.class);
                String pass = new String(view.getPasswordField().getPassword());
                if (!EmailValidator.getInstance().isValid(view.getLoginField().getText()) ||
                        !InetAddressValidator.getInstance()
                                .isValid(view.getServerIPAddressField().getText())||
                        view.getServerIPAddressField().getText().equals("")||pass.equals("")) {
                    command = new LoginErrorCommand(view,
                            "<html><<div style='text-align: center;'>Check entered</div></html>");
                } else
                    {
                    getModel().setServerAddress(view.getServerIPAddressField().getText());
                        int k=0;
                        boolean c=false;
                        while(!c)
                        {
                            c=Utility.usersGetAll(getModel());
                            k++;
                            if(k>3) {
                                command = new ConnectionErrorCommand(view);
                                c = true;
                            }
                        }
                        if(Utility.usersGetAll(getModel())){
                         User user =   UserParser.getUser(view.getLoginField().getText(), getModel().getUsers());
                         if(user!=null) {
                             if (pass.equals(user.getPassword())) {
                                 getModel().setLoggedUser(UserParser.getUser(view.getLoginField().getText(),
                                         getModel().getUsers()));
                                 getModel().setRealNameLoggedUser(getModel().getLoggedUser().getName());
                                 getModel().getLoggedUser().setName("Saved Messages");
                                 command = new ShowChatViewCommand(parent, view);
                             }
                         }
                         else command = new LoginErrorCommand(view,
                                 "<html><<div style='text-align: center;'>Check entered</div></html>");
                    }
                }
                break;
            }
            case SEND_ACTION_COMMAND: {
                ChatPanelView view = Utility.findParent((Component) e.getSource(),ChatPanelView.class);
                getModel().setLastMessageText(view.getTextMessageField().getText());
                command = new SendMessageCommand(parent,view);
                break;
            }
            case LOGOUT_ACTION_COMMAND: {
                getModel().getMessages().clear();
                getModel().setLastMessageID(0);
                getModel().getLoggedUser().setName(getModel().getRealNameLoggedUser());
                getModel().initialise();
                command = new ShowLoginViewCommand(parent);
                break;
            }
            case REGISTRATION_ACTION_COMMAND:{
                command = new ShowRegistrationPanelView(parent);
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + commandName);
            }
        }

    }

    public void setParent(MessengerApl parent) {
        this.parent = parent;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
