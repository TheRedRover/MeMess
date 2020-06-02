package domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import domain.xml.UserBuilder;

import java.util.Objects;
import java.util.Timer;

public class User {
    private String name;
    private String login;
    private String password;
    private Boolean status = false;
    private Timer timer;

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public  User(){
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getLogin().equals(user.getLogin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogin());
    }

    public User(User user) {
        this.name = user.getName();
        this.login=user.getLogin();
        this.password=user.getPassword();
        this.status = user.getStatus();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean connected) {
        this.status = connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean compareTo(User user){
        return getLogin().equals(user.getLogin());
    }

    @Override
    public String toString() {
        return "<html><font color='white' size = '5'>" + name +
                "   <font size='2', color='aqua'>" +
                (this.getStatus() ? "online" : "") + "</font>" +
                "</font><html>";
    }

    public static String userToJsonString(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static User jsonToUser(String jsonString){
        Gson json = new Gson();
        return json.fromJson(jsonString, User.class);
    }
}
