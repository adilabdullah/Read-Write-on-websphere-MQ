/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ActiveMqReadWrite;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
 
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author pc
 */
public class ReadWriteActivemq {
        private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "Q1"; // Queue Name.You can create any/many queue
 //    private static String subject = "Q1";
    public static void main(String[] args)
    {
            try {
             //   writeMq();
                readMq();
            } catch (JMSException ex) {
                ex.printStackTrace();
                System.out.println(" "+ex.getMessage());
            }
            catch (Exception ee) {
                ee.printStackTrace();
                System.out.println(" "+ee.getMessage());
            }
    }
    
    public static void writeMq() throws JMSException
    {  // Getting JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
         
        //Creating a non transactional session to send/receive JMS message.
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);  
         
        //Destination represents here our queue 'JCG_QUEUE' on the JMS server. 
        //The queue will be created automatically on the server.
        Destination destination = session.createQueue(subject); 
         
        // MessageProducer is used for sending messages to the queue.
        MessageProducer producer = session.createProducer(destination);
         
        // We will send a small text message saying 'Hello World!!!' 
        TextMessage message = session
                .createTextMessage("This is Adil Abdullah.");
         
        // Here we are sending our message!
        producer.send(message);
         
        System.out.println("JCG printing@@ '" + message.getText() + "'");
        connection.close();}
    
    public static void readMq() throws JMSException
    { // Getting JMS connection from the server
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
 
        // Creating session for seding messages
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
 
        // Getting the queue 'JCG_QUEUE'
        Destination destination = session.createQueue(subject);
 
        // MessageConsumer is used for receiving (consuming) messages
        MessageConsumer consumer = session.createConsumer(destination);
 
        // Here we receive the message.
        Message message = consumer.receive();
 
        // We will be using TestMessage in our example. MessageProducer sent us a TextMessage
        // so we must cast to it to get access to its .getText() method.
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("Received message '" + textMessage.getText() + "'");
        }
        connection.close();}
    
}
