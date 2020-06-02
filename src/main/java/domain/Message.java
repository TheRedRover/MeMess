package domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Message implements Serializable, Comparable<Message> {
    private Long id;
    private String text;
    private User user;
    private Calendar moment;
    private User toUser;
    public final static String MESSAGE_LEFT_ALIGN="left";
    public final static String MESSAGE_RIGHT_ALIGN="right";

    public static Builder newMessage() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return getId().equals(message.getId()) &&
                getText().equals(message.getText()) &&
                getUser().toString().equals(message.getUser().toString()) &&
                getMoment().equals(message.getMoment()) &&
                getTo_user().toString().equals(message.getTo_user().toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, user, moment, toUser);
    }

    @Override
    public String toString() {
        return "<p><b>" + user.getName() + "->" +
                toUser.getName() + ":</b><br/><message>" + text +
                "</message><br/><div style='align = right;font-size:small;'>" +
                (new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy"))
                        .format(moment.getTime().getTime()) +
                "</div<br/></p>";
    }

    public String toString(User loggedUser) {
        String align = MESSAGE_LEFT_ALIGN;
        if(this.getUser().equals(loggedUser))
            align=MESSAGE_RIGHT_ALIGN;
        return String.format("<p style = 'text-align : %s;'><message style = 'word-wrap: break-word'>", align) +
                text +
                "</message><br/><font size = '1'>" +
                (new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy"))
                        .format(moment.getTime().getTime()) +
                "</font></p><br/>";
    }


    public User getTo_user() {
        return toUser;
    }

    public void setTo_user(User toUser) {
        this.toUser = toUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Calendar getMoment() {
        return moment;
    }

    public void setMoment(Calendar moment) {
        this.moment = moment;
    }

    private Message() {}

    private  Message(Builder builder){
        setId(builder.id);
        setText(builder.text);
        setUser(builder.user);
        setTo_user(builder.toUser);
        setMoment(builder.moment);
    }

    @Override
    public int compareTo(Message o) {
        if(getMoment().equals(o.getMoment()))
            return getId().compareTo(o.getId());
        else return getMoment().compareTo(o.getMoment());
    }

    public static class Builder {
        private Long id;
        private String text;
        private User user,toUser;
        private Calendar moment;

        private Builder() {}

        public  Builder id(Long id)
        {
            this.id = id;
            return this;
        }

        public Builder text(String text){
            this.text=text;
            return  this;
        }

        public Builder user(User user){
            this.user=user;
            return this;
        }

        public  Builder to_user(User toUser){
            this.toUser=toUser;
            return this;
        }

        public Builder moment(Calendar moment){
            this.moment = moment;
            return  this;
        }

        public Message build(){
            return new Message(this);
        }
    }
}
