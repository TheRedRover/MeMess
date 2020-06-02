package client;

public class ShowRegistrationPanelView implements Command {

    MessengerApl apl;
    RegistrationPanelView view;
    ShowRegistrationPanelView(MessengerApl apl){
        this.apl=apl;
    }
    @Override
    public void execute() {
        apl.getLoginPanelView().setVisible(false);
        apl.getRegistrationPanelView();
        apl.showRegistrationPanelView();
    }
}
