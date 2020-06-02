package client;

import java.util.TimerTask;

public class UpdateUsersTask extends TimerTask {

    private MessengerApl apl;
    public UpdateUsersTask(MessengerApl apl) {
        this.apl = apl;
    }

    @Override
    public void run() {
        Utility.usersGetAll(MessengerApl.getModel());
        apl.getChatPanelView().getUserList().repaint();
    }
}
