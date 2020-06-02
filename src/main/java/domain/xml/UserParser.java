package domain.xml;


import domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class UserParser extends DefaultHandler {
    final static  Logger LOGGER = LogManager.getLogger(UserParser.class);

    private User user;
    private List<User> users;
    private String thisElement;

    public UserParser(List<User> users) {
        this.users = users;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        LOGGER.debug("Start Documebt");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        thisElement = qName;
        LOGGER.debug("Start Element");
        LOGGER.trace("<" + qName);
        if ("user".equals(qName)) {
            this.user = new User();
            for (int i=0;i<attributes.getLength();i++){
                String attrName = attributes.getLocalName(i);
                String attrValue = attributes.getValue(i);
                LOGGER.trace(attrName+" = "+attrValue);
                switch (attrName){
                    case "name":
                        this.user.setName(attrValue);
                        break;
                    case "login":
                        this.user.setLogin(attrValue);
                        break;
                    case"password":
                        this.user.setPassword(attrValue);
                        break;
                    case "status":
                        this.user.setStatus(attrValue.equals("true"));
                }
            }
            LOGGER.trace(">");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if("user".equals(qName)){
            users.add(user);
        }
        thisElement="";
        LOGGER.debug("End element");
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LOGGER.debug("End document");
    }

    public List<User> getUsers(String login){
        return users;
    }

    static public User getUser(String login, List<User> users){
        for (User user:users) {
            if(user.getLogin().equals(login)){
                return user;
            }
        }
        return null;
    }
}
