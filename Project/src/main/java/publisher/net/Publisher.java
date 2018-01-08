package publisher.net;

import common.CommunicationMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class Publisher
{
    public static final String TOPIC01="jms/Topic01";
    private static TopicConnection topicConnection;
    private static TopicPublisher topicPublisher;
    private static TopicSession publishSession;

    public static void startPublisher(Publisher publisher) throws JMSException, NamingException {
        //Publisher publisher = new Publisher();
        Context initialContext = publisher.getInitialContext();

        // Get the connection factory I defined
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)initialContext.lookup("GFConnectionFactory" );

        Topic topic01 = (Topic)initialContext.lookup(Publisher.TOPIC01);
        //With the connection factory instance we create a topic connection
        topicConnection = topicConnectionFactory.createTopicConnection();
        //start the connection
        topicConnection.start();

        //Now I am going to publish in topic 01
        publisher.publish(topicConnection, topic01);
    }

    public void publish(TopicConnection topicConnection, Topic topic) throws JMSException {
        // Obtain the publish session
        publishSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        //To create the topic publisher
         topicPublisher = publishSession.createPublisher(topic);
    }

    public void disconnect() throws JMSException {
        topicConnection.close();
    }

    public void sendMessage(String username, String message) throws JMSException {
        //Publish the message
        ObjectMessage objectMessage =publishSession.createObjectMessage();
        objectMessage.setObject(new CommunicationMessage( username, message));
        topicPublisher.publish(objectMessage);
    }

    public static Context getInitialContext() throws JMSException, NamingException {
        Properties properties =new Properties();
        properties.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        properties.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        properties.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
        properties.setProperty("java.naming.provider.url", "iiop://localhost:3700");
        return new InitialContext(properties);
    }

}
