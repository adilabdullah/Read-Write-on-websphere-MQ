/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readwritemq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pc
 */
public class ReadWriteMq {

   
private MQQueueManager _queueManager = null;
public int port = 1415;
public String hostname = "";
//public String channel = "SYSTEM.DEF.SVRCONN";
public String channel = "";
public String qManager = "Qmgr";
public String inputQName = "q2";
public String outputQName = "q2";

public ReadWriteMq()
{
// Set up MQ environment
MQEnvironment.hostname = hostname;
MQEnvironment.channel = channel;
MQEnvironment.port = port;
}




public static void main(String[] args) throws IOException
{

ReadWriteMq readQ = new ReadWriteMq();

try
{
//readQ.init(args);
String line;
Scanner in=new Scanner(System.in);
readQ.selectQMgr();
//System.out.println("Init Successfull");
System.out.println("Enter anything to Queue::");
line=in.nextLine();
//readQ.write(line);
readQ.read("q2");
}
catch (IllegalArgumentException e)
{
System.out.println("Usage: java MQRead <-h host> <-p port> <-c channel> <-m QueueManagerName> <-q QueueName>");
System.exit(1);
}
catch (MQException e)
{
System.out.println(e);
System.exit(1);
}
}
public void read(String qname) throws MQException, IOException
{
int openOptions = MQC.MQOO_INQUIRE + MQC.MQOO_FAIL_IF_QUIESCING + MQC.MQOO_INPUT_SHARED;

//MQQueue queue = _queueManager.accessQueue( inputQName,
MQQueue queue = _queueManager.accessQueue(qname,
openOptions,
null, // default q manager
null, // no dynamic q name
null ); // no alternate user id

System.out.println("MQRead v1.0 connected.\n");

int depth = queue.getCurrentDepth();
System.out.println("Current depth: " + depth + "\n");
if (depth == 0)
{
return;
}
MQGetMessageOptions getOptions = new MQGetMessageOptions();
getOptions.options = MQC.MQGMO_NO_WAIT + MQC.MQGMO_FAIL_IF_QUIESCING + MQC.MQGMO_CONVERT;
while(true)
{
MQMessage message = new MQMessage();
try
{
queue.get(message, getOptions);
byte[] b = new byte[message.getMessageLength()];
message.readFully(b);
System.out.println(new String(b));
message.clearMessage();
}
catch (IOException e)
{
System.out.println("IOException during GET: " + e.getMessage());
break;
}
catch (MQException e)
{
if (e.completionCode == 2 && e.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE) {
if (depth > 0)
{
System.out.println("All messages read.");
}
}
else
{
System.out.println("GET Exception: " + e);
}
break;
}
}
queue.close();
_queueManager.disconnect();
}


public void selectQMgr()
{
  /*       Hashtable properties = new Hashtable<String,String>();
     properties.put("hostname","localhost");
     properties.put("port", "1414"); */
 //    properties.put("channel","SYSTEM.DEF.SVRCONN");
    try {
        _queueManager = new MQQueueManager(qManager);
    } catch (MQException ex) {
        ex.printStackTrace();
        System.out.println("Error on selecting queue manager "+ex.getMessage());
    }
}



public void write(String msg) throws MQException
{
int lineNum=0;
int openOptions = MQC.MQOO_OUTPUT + MQC.MQOO_FAIL_IF_QUIESCING;
try
{
MQQueue queue = _queueManager.accessQueue( outputQName,
openOptions,
null, // default q manager
null, // no dynamic q name
null ); // no alternate user id

DataInputStream input = new DataInputStream(System.in);

System.out.println("MQWrite v1.0 connected");
System.out.println("and ready for input, terminate with ^Z\n\n");

// Define a simple MQ message, and write some text in UTF format..
MQMessage sendmsg = new MQMessage();
sendmsg.format = MQC.MQFMT_STRING;
sendmsg.feedback = MQC.MQFB_NONE;
sendmsg.messageType = MQC.MQMT_DATAGRAM;
//sendmsg.replyToQueueName = "ROGER.QUEUE";
sendmsg.replyToQueueManagerName = qManager;

MQPutMessageOptions pmo = new MQPutMessageOptions(); // accept the defaults, same
// as MQPMO_DEFAULT constant

//String line = "test message";
sendmsg.clearMessage();
sendmsg.messageId = MQC.MQMI_NONE;
sendmsg.correlationId = MQC.MQCI_NONE;
sendmsg.writeString(msg);

// put the message on the queue

queue.put(sendmsg, pmo);
System.out.println(++lineNum + ": " + msg);

queue.close();
_queueManager.disconnect();

}
catch (com.ibm.mq.MQException mqex)
{
System.out.println(mqex);
}
catch (java.io.IOException ioex)
{
System.out.println("An MQ IO error occurred : " + ioex);
}

}
    
}
