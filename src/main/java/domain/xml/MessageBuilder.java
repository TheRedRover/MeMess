package domain.xml;

import domain.Message;
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
import java.text.SimpleDateFormat;
import java.util.Collection;

public class MessageBuilder {
    final static Logger LOGGER = LogManager.getLogger(MessageBuilder.class);

    public static String buildDocument(Document document, Collection<Message> messagesList){
        LOGGER.debug("Creation of document start");
        //create DOM of XML doc
        Element rootElement = document.createElement("messages");
        document.appendChild(rootElement);
        LOGGER.trace("Create root element"+rootElement.toString());
        for (Message message:messagesList) {
            Element messageElement = document.createElement("message");
            rootElement.appendChild(messageElement);
            if(message.getId()!=null){
                messageElement.setAttribute("id",message.getId().toString());
            }
            messageElement.setAttribute("sender_login", message.getUser().getLogin());
            messageElement.setAttribute("sender_name", message.getUser().getName());
            messageElement.setAttribute("reciver_login", message.getTo_user().getLogin());
            messageElement.setAttribute("reciver_name", message.getTo_user().getLogin());
            messageElement.setAttribute("moment", (new SimpleDateFormat("HH:mm:ss dd-MM-yyyy")
                    .format(message.getMoment().getTime())));
            messageElement.appendChild(document.createTextNode(message.getText()));
            LOGGER.trace("Created message element"+messageElement.toString());
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
