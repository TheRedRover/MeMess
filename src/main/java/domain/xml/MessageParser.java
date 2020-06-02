package domain.xml;

import domain.Message;
import domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageParser extends DefaultHandler {
    final static Logger LOGGER = LogManager.getLogger(MessageParser.class);

    private Message message;
    private List<Message> messages;
    private AtomicInteger id;
    private String thisElement;
    private List<User> users;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        LOGGER.debug("Start Documebt");
    }
    public MessageParser(AtomicInteger id, List<Message> messages, List<User> users){
        this.id = id;
        this.messages = messages;
        this.users = users;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        thisElement = qName;
        LOGGER.debug("Start Element");
        LOGGER.trace("<" + qName);
        if ("message".equals(qName)){
            Message.Builder builder = Message.newMessage();
            for (int i=0;i<attributes.getLength();i++){
                String attrName = attributes.getLocalName(i);
                String attrValue = attributes.getValue(i);
                LOGGER.trace(attrName+" = "+attrValue);
                switch (attrName) {
                    case "reciver_login":
                        try {
                            builder.to_user(UserParser.getUser(attrValue, users));
                        } catch (NullPointerException e) {
                            LOGGER.error(e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case "sender_login":
                        try {
                            builder.user(UserParser.getUser(attrValue, users));
                        } catch (NullPointerException e) {
                            LOGGER.error(e.getMessage());
                            e.printStackTrace();
                        }
                        break;

                    case "id":
                        builder.id(Long.valueOf(attrValue));
                        break;
                    case "moment":
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                        try {
                            calendar.setTime(format.parse(attrValue));
                        } catch (ParseException e) {
                            LOGGER.error(e.getMessage());
                            e.printStackTrace();
                        }
                        builder.moment(calendar);


                }
            }
            message= builder.build();
        }
        LOGGER.trace(">");
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if("message".equals(qName)){
            Long newId = (long) id.getAndIncrement();
            if(message.getId() == null){
                message.setId(newId);
            }
            else{
                newId = message.getId();
                id.set(newId.intValue());
            }
            LOGGER.debug("id = " + newId);
            messages.add(message);
        }
        thisElement ="";
        LOGGER.debug("End element");
        LOGGER.trace("</" + qName + ">");
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if ("message".equals(thisElement)){
            String messBody = new String(ch, start, length).trim();
            LOGGER.trace(messBody);
            message.setText(messBody);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LOGGER.debug("End document");
    }
}