package subscriber.net;

import common.CommunicationMessage;
import subscriber.controller.SubscriberController;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class Subscriber
{

    public static final String TOPIC01="jms/Topic01";
    private static TopicConnection topicConnection;
    private static TopicSession subscribeSession;


    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage =(ObjectMessage) message;
            CommunicationMessage communicationMessage = (CommunicationMessage)objectMessage.getObject();
            System.out.println("Sender:" + communicationMessage.getName() + " | Message: " + communicationMessage.getMessage());
        }catch (JMSException e) { e.printStackTrace();}
    }

    public static void startSubscriber(Subscriber subscriber, SubscriberController.OutputHandler outputHandler) throws JMSException, NamingException {
        //Subscriber subscriber = new Subscriber();
        Context initialContext = Subscriber.getInitialContext();
        Topic topic01 = (Topic)initialContext.lookup(Subscriber.TOPIC01);
        // Get the connection factory I defined
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)initialContext.lookup("GFConnectionFactory" );
        //With the connection factory instance we create a topic connection
        topicConnection = topicConnectionFactory.createTopicConnection();
        //start the connection
        topicConnection.start();

        //Now I am going to subscribe to topic 01
         subscriber.subscribe(topicConnection,topic01, outputHandler);
    }

    public void subscribe(TopicConnection topicConnection, Topic topic, SubscriberController.OutputHandler outputHandler) throws JMSException{
        //Obtain a Topic Session
        subscribeSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        //Create the subscriber
        TopicSubscriber topicSubscriber =subscribeSession.createSubscriber(topic);
        //Set this class as listener, so messages get printed
        topicSubscriber.setMessageListener(outputHandler);
    }

    public void disconnect() throws JMSException {
        topicConnection.close();
    }

    public static Context getInitialContext() throws NamingException {
        Properties properties =new Properties();
        properties.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        properties.setProperty("java.naming.provider.url", "iiop://localhost:3700");
        return new InitialContext(properties);
    }

}