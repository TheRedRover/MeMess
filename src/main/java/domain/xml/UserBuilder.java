package domain.xml;

import domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

public class UserBuilder {
    final static Logger LOGGER = LogManager.getLogger(UserBuilder.class);

    public static String buildDocument(Document document, Collection<User> userList){
        LOGGER.debug("Creation of document start");
        //create DOM of XML doc
        Element rootElement = document.createElement("users");
        document.appendChild(rootElement);
        for (User user:userList) {
            Element userElement = document.createElement("user");
            rootElement.appendChild(userElement);
            userElement.setAttribute("name", user.getName());
            userElement.setAttribute("login",user.getLogin());
            userElement.setAttribute("password", user.getPassword());
            userElement.setAttribute("status",user.getStatus().toString());
            LOGGER.trace("Created user element"+userElement.toString());
        }

        //XML doc to string
        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLS =(DOMImplementationLS) impl.getFeature("LS","3.0");

        LSSerializer ser = implLS.createLSSerializer();
        ser.getDomConfig().setParameter("format-pretty-print",true);

        LSOutput lsOutput=implLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");

        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);

        ser.write(document,lsOutput);
        String result = stringWriter.toString();
        LOGGER.debug("Creation of document ended");
        LOGGER.trace(result);
        return result;
    }
}
