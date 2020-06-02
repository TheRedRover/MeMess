package client;

public class ShowLoginViewCommand implements Command {
    private MessengerApl apl;
    private ChatPanelView panel;
    public ShowLoginViewCommand(MessengerApl parent)
    {
        apl = parent;
    }

    @Override
    public void execute() {
        apl.getRegistrationPanelView().clearFields();
        apl.getChatPanelView().clearFields();
        apl.getChatPanelView().setVisible(false);
        apl.getRegistrationPanelView().setVisible(false);
        if(apl.getTimer()!=null)
            apl.getTimer().cancel();
        apl.getLoginPanelView();
        apl.showLoginPanelView();
    }
}
