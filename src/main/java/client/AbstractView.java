package client;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractView extends JPanel {
    protected static MessengerApl parent;

    public static void setParent(MessengerApl parent) {
        AbstractView.parent = parent;
    }


    public AbstractView(){
        super();
    }

    public abstract void initialise();
    public abstract void clearFields();

    protected void addLabelField(JPanel panel, String labelText, Component field){
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.white);
        panel.add(label);
        panel.add(field);
    }
    protected void addLabelField(JPanel panel, String labelText, Component field, Dimension labelSize, int labelTextAlign){
        JLabel label = new JLabel(labelText, labelTextAlign);
        label.setPreferredSize(labelSize);
        label.setForeground(Color.white);
        panel.add(label);
        panel.add(field);
    }
    protected void addLabelField(JPanel panel, String labelText, Component field, Dimension labelSize){
        addLabelField(panel,labelText,field,labelSize, SwingConstants.LEFT);
    }
    public void initModel(){

    }
}
