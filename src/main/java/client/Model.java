package client;

import domain.Message;
import domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Model {
    private MessengerApl parent;
    private User loggedUser;
    private User currentUser;
    private String lastMessageText;
    private List<Message> messages;
    private List<User> users;
    private long lastMessageID;
    private String serverAddress ="127.0.0.1";

    private String realNameLoggedUser ="";

    public String getRealNameLoggedUser() {
        return realNameLoggedUser;
    }

    public void setRealNameLoggedUser(String realNameLoggedUser) {
        this.realNameLoggedUser = realNameLoggedUser;
    }

    //singleton pattern
    private Model(){}

    public static Model getInstance() {
        return ModelHolder.INSTANCE;

    }

    public String messagesToString() {
       return messages.toString();
    }

    public void addMessages(List<Message> newMessages) {
        messages.addAll(newMessages);
        Collections.sort(messages);
        Collections.sort(newMessages);
        getWithCurrentUserMessages(newMessages);
    }

    public void getWithCurrentUserMessages(List<Message> messages){
        if(currentUser!=null){
            List<Message> newMessages = new ArrayList<Message>(){
                @Override
                public String toString() {
                        StringBuilder result = new StringBuilder();
                    for (Message message : this) {
                        result.append(message.toString(loggedUser));
                    }
                        return result.toString();
                }
            };
            if(!getLoggedUser().equals(getCurrentUser()))
                newMessages.addAll(messages.stream().filter(mes->mes.getUser().equals(getCurrentUser())||
                    mes.getTo_user().equals(getCurrentUser())).collect(Collectors.toList()));
            else newMessages.addAll(messages.stream().filter(mes->mes.getUser().equals(getCurrentUser())&&
                    mes.getTo_user().equals(getCurrentUser())).collect(Collectors.toList()));
            Collections.sort(newMessages);
            parent.getChatPanelView().updateMessageTextPane(newMessages.toString());
        }
    }

    private static class ModelHolder{
        private static final Model INSTANCE = new Model();
    }

    public void initialise(){
        setMessages(new ArrayList<Message>(){
            public String toString() {
                StringBuilder result = new StringBuilder();
                for (Message message : this) {
                    result.append(message.toString(loggedUser)).append("<hr>");
                }
                return result.toString();
            }
        });
        lastMessageID=0L;
        currentUser = new User();
        loggedUser= new User();
        lastMessageText="";
    }



//getters and setters
    public MessengerApl getParent() {
        return parent;
    }

    public void setParent(MessengerApl parent) {
        this.parent = parent;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public long getLastMessageID() {
        return lastMessageID;
    }

    public void setLastMessageID(long lastMessageID) {
        this.lastMessageID = lastMessageID;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
