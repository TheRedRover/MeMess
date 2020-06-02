package client;

public class ConnectionErrorCommand implements Command {
    LoginPanelView view;
    public ConnectionErrorCommand(LoginPanelView view) {
        this.view = view;
    }

    @Override
    public void execute() {
        view.setVisible(false);
        view.getErrorLabel().setText("Connection error");
        view.getMainPanel().add(view.getErrorLabel());
        view.getErrorLabel().setVisible(true);
        view.setVisible(true);
        view.repaint();
    }
}
