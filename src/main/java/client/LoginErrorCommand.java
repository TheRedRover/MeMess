package client;

public class LoginErrorCommand implements Command {
    private LoginPanelView view;
    private String text;

    public LoginErrorCommand(LoginPanelView view, String text) {
        this.view = view;
        this.text=text;
    }

    @Override
    public void execute() {
        view.setVisible(false);
        view.getErrorLabel().setText(text);
        view.getMainPanel().add(view.getErrorLabel());
        view.getErrorLabel().setVisible(true);
        view.setVisible(true);
        view.repaint();
    }
}
