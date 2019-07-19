package cmp.client;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;

import common.Logs;

public class ContactClient
{
    private String IP;
    private int PORT;
    private boolean ASYNC;
    private final int TIME_OUT = 15000;
    private Socket socket = null;
    
    public ContactClient(final String strIP, final int nPort, final boolean bAsync)
    {
        IP = strIP;
        PORT = nPort;
        ASYNC = bAsync;
    }
    
    public int start()
    {
        stop();
        socket = new Socket();
        
        if (ASYNC)
        {
            new Thread(new SocketConnect(socket)).start();
            return 0;
        }
        return socketConnect(socket);
    }
    
    public void stop()
    {
        if (Controller.validSocket(socket))
        {
            try
            {
                socket.close();
                socket = null;
            }
            catch (Exception e)
            {
                Logs.showError("Socket Close Exception: " + e.getMessage());
            }
        }
    }
    
    public void send(JSONObject jsonObject)
    {
        if (Controller.validSocket(socket))
        {
            if (ASYNC)
            {
                Thread thread = new Thread(new SocketSend(socket, jsonObject.toString()));
                thread.start();
                return;
            }
            
            
        }
    }
    
    private int socketConnect(Socket socket)
    {
        int nResult = 0;
        try
        {
            socket.connect(new InetSocketAddress(IP, PORT), TIME_OUT);
            socket.setSoTimeout(TIME_OUT);
            Logs.showTrace("[ContactClient] SocketConnect : " + socket.isConnected());
        }
        catch (Exception e)
        {
            nResult = -1;
            Logs.showError("SocketConnect Exception: " + e.toString());
        }
        return nResult;
    }
    
    private int socketSend(Socket socket)
    {
    
    }
    
    //==================== Thread Runnable ===============================//
    private class SocketConnect implements Runnable
    {
        private Socket theSocket = null;
        
        SocketConnect(Socket socket)
        {
            theSocket = socket;
        }
        
        @Override
        public void run()
        {
            socketConnect(theSocket);
        }
    }
    
    private class SocketSend implements Runnable
    {
        private Socket theSocket = null;
        private String theData = null;
        
        SocketSend(Socket socket, String strData)
        {
            theSocket = socket;
            theData = strData;
        }
        
        @Override
        public void run()
        {
            int nRespon = Controller.STATUS_ROK;
            try
            {
                if (theSocket.isConnected())
                {
                    Controller.CMP_PACKET respPacket = new Controller.CMP_PACKET();
                    nRespon = Controller.cmpRequest(Controller.deidentify_request, theData,
                            respPacket, theSocket);
                    Logs.showTrace("[ContactClient] SocketSend Response Code: " + nRespon);
                }
                else
                {
                    Logs.showError("[ContactClient] SocketSend Socket is not connect");
                }
            }
            catch (Exception e)
            {
                Logs.showError("SocketSend Exception: " + e.toString());
            }
        }
    }
}
