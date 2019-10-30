import org.json.JSONObject;

import cmp.server.ContactServer;

public class Main
{
    public static void main(String[] args)
    {
        // socket server test
        ContactServer contactServer = ContactServer.getInstance();
        if (-1 != contactServer.start(1414))
        {
            contactServer.setReceiveListener(receiveListener);
            contactServer.setStatusListener(statusListener);
            contactServer.setOptionListener(optionListener);
            System.out.println("Socket Server Start!!");
        }
        
    }
    
    private static ContactServer.ReceiveListener receiveListener =
            new ContactServer.ReceiveListener()
    {
        @Override
        public void onReceive(String strData)
        {
            System.out.println("去識別: " + strData);
        }
    };
    
    private static ContactServer.StatusListener statusListener = new ContactServer.StatusListener()
    {
        @Override
        public String onStatus(String strData)
        {
            System.out.println("問狀態: " + strData);
            JSONObject jstatus = new JSONObject();
            jstatus.put("status","running");
            jstatus.put("time","666");
            return jstatus.toString();
        }
    };
    
    private static ContactServer.OptionListener optionListener = new ContactServer.OptionListener()
    {
        @Override
        public void onOption(String strData)
        {
            System.out.println("操作:" + strData);
        }
    };
}
