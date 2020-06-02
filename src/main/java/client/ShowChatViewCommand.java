package client;

import java.util.Timer;

public class ShowChatViewCommand implements Command {

    private MessengerApl apl;
    private LoginPanelView view;

    public ShowChatViewCommand(MessengerApl parent, LoginPanelView view) {

        this.apl = parent;
        this.view = view;
    }

    @Override
    public void execute() {
        view.clearFields();
        view.setVisible(false);
        apl.getChatPanelView().getTextMessageField().setEnabled(false);
        apl.setTimer(new Timer());
        apl.getTimer().scheduleAtFixedRate(new UpdateMessageTask(apl), MessengerApl.DELAY_MESSAGES, MessengerApl.PERIOD_MESSAGES);
        apl.getTimer().scheduleAtFixedRate(new UpdateUsersTask(apl), MessengerApl.DELAY_USERS, MessengerApl.PERIOD_USERS);
        apl.showChatPanelView();
    }
}
