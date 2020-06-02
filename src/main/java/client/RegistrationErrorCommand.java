package client;

public class RegistrationErrorCommand implements Command {
    private RegistrationPanelView view;
    private String line;

    public RegistrationErrorCommand(RegistrationPanelView view, String line) {
        this.view = view;
        this.line = line;
    }

    @Override
    public void execute() {
        view.setVisible(false);
        view.getErrorLabel().setText(line);
        view.getMainPanel().add(view.getErrorLabel());
        view.getErrorLabel().setVisible(true);
        view.setVisible(true);
        view.repaint();
    }
}
