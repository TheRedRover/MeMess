package client;

import java.util.TimerTask;

public class UpdateMessageTask extends TimerTask {

    private MessengerApl apl;
    public UpdateMessageTask(MessengerApl apl) {
        this.apl = apl;
        Utility.messagesUpdate(apl);
    }

    @Override
    public void run() {
        Utility.messagesUpdate(apl);
    }
}
