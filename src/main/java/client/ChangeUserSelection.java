package client;

import domain.User;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChangeUserSelection implements ListSelectionListener {

    private ChatPanelView view;

    ChangeUserSelection(ChatPanelView view){
        this.view = view;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        User curUser = view.getUserList().getSelectedValue();
        view.getTextMessageField().setEnabled(true);
        view.getSendMessageButton().setEnabled(true);
        MessengerApl.getModel().setCurrentUser(curUser);
        Utility.messagesUpdate(AbstractView.parent);
        view.getUserCompanion().setText(view.getUserList().getSelectedValue().toString());
        view.clearMessageTextPane();
        MessengerApl.getModel().getWithCurrentUserMessages(MessengerApl.getModel().getMessages());
    }
}
