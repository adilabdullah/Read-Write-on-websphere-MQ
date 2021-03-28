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

/**
 *
 * @author pc
 */
public class MQReadWrite {
 //   private MQQueueManager _queueManager = null;
public int port = 1414;
public String hostname = "";
    
String correlId=null;
String msgId=null;
    public static void main(String[] args) throws IOException
    {
      try{
          new MQReadWrite().init("","",1414);
        new MQReadWrite().write("This is test","q1","Qmgr");
        //  new MQReadWrite().read("q1","Qmgr");
          System.out.println("Reat Write Successful");
        
      }
   catch(Exception ex)
   {
  ex.printStackTrace();
       System.out.println(""+ex.getMessage()); 
   }
 }
    
    
    public void read(String qname,String qmanager) throws MQException, IOException
{
              MQQueueManager _queueManager = new MQQueueManager(qmanager);
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
correlId=new String(message.correlationId);
msgId=new String(message.messageId);
    byte[] b = new byte[message.getMessageLength()];
message.readFully(b);
System.out.println(new String(b));
    System.out.println("MsgId::"+msgId+" Correlation Id::"+correlId);
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
//System.out.println("All messages read.");
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
    
    
    
    
    
 public void write(String msg,String qname,String qmanager) throws MQException
{
          MQQueueManager _queueManager = new MQQueueManager(qmanager);

int lineNum=0;
int openOptions = MQC.MQOO_OUTPUT + MQC.MQOO_FAIL_IF_QUIESCING;
try
{
MQQueue queue = _queueManager.accessQueue( qname,
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
sendmsg.replyToQueueManagerName = qmanager;

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
 
 
 
 
 
public void init(String host,String channel,int port)
{
// Set up MQ environment
MQEnvironment.hostname = hostname;
MQEnvironment.channel = channel;
MQEnvironment.port = port;
}
 
}
