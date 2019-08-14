import cmp.server.ContactServer;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Hello World!");
        
        // socket server test
        ContactServer contactServer = ContactServer.getInstance();
        if (-1 != contactServer.start(2310))
        {
            contactServer.setReceiveListener(receiveListener);
            System.out.println("Socket Server Start!!");
            
        }
        
    }
    
    private static ContactServer.ReceiveListener receiveListener =
            new ContactServer.ReceiveListener()
    {
        @Override
        public void onReceive(String strData)
        {
            
            System.out.println("Command: " + strData);
        }
    };
}
