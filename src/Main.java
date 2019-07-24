import org.json.JSONObject;

import cmp.client.ContactClient;
import cmp.client.Controller;
import cmp.server.ContactServer;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Hello World!");
        
        // socket server test
        ContactServer contactServer = ContactServer.getInstance();
        contactServer.start(2310);
        // socket client test
        /*
        ContactClient contactClient = new ContactClient("127.0.0.1", 2310, false);
        if (-1 != contactClient.start())
        {
            JSONObject jsonWord = new JSONObject();
            jsonWord.put("id", 1);
            jsonWord.put("type", 4);
            jsonWord.put("word", "try it");
            jsonWord.put("total", 0);
            jsonWord.put("number", 0);
            
            Controller.CMP_PACKET respPacket = new Controller.CMP_PACKET();
            contactClient.send(jsonWord, respPacket);
            contactClient.stop();
            System.out.println(respPacket.cmpBody);
        }
         */
    }
}
