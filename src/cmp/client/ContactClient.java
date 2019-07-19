package cmp.client;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;

import common.Logs;

public class ContactClient
{
    private final String IP = "127.0.0.1";
    private final int PORT = 2019;
    private final int SOCKET_CONNECT_SUCCESS = 0;
    
    private Socket socket = null;
    
    public ContactClient()
    {
    }
    
    public void start()
    {
        stop();
        socket = new Socket();
        
        Logs.showTrace("[ContactClient] start Socket Created");
        Thread thread = new Thread(new SocketConnect(socket));
        thread.start();
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
            Thread thread = new Thread(new SocketSend(socket, handler, jsonObject.toString()));
            thread.start();
        }
    }
    
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
            try
            {
                // Socket Connect Timeout
                int nConnectTimeOut = 15000;
                theSocket.connect(new InetSocketAddress(IP, PORT), nConnectTimeOut);
                // Socket Read IO Timeout
                int nReceiveTimeOut = 15000;
                theSocket.setSoTimeout(nReceiveTimeOut);
                Logs.showTrace("[WheelPiesClient] SocketConnect : " + theSocket.isConnected());
            }
            catch (Exception e)
            {
                Logs.showError("SocketConnect Exception: " + e.toString());
            }
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
                    nRespon = Controller.cmpRequest(Controller.semantic_request, theData,
                            respPacket, theSocket);
                    Logs.showTrace("[SemanticClient] SocketSend Response Code: " + nRespon);
                }
                else
                {
                    Logs.showError("[SemanticClient] SocketSend Socket is not connect");
                }
            }
            catch (Exception e)
            {
                Logs.showError("SocketSend Exception: " + e.toString());
            }
        }
    }
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SOCKET_CONNECT_SUCCESS:
                    Logs.showTrace("Socket Connect Success");
                    
                    break;
            }
        }
    };
}
