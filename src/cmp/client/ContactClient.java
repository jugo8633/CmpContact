package cmp.client;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;

import common.Controller;
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
    
    public int send(JSONObject jsonObject, Controller.CMP_PACKET respPacket)
    {
        if (Controller.validSocket(socket) && null != respPacket)
        {
            if (ASYNC)
            {
                Thread thread = new Thread(new SocketSend(socket, jsonObject.toString(),
                        respPacket));
                thread.start();
                return 0;
            }
            return socketSend(socket, jsonObject.toString(), respPacket);
        }
        return -1;
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
    
    private int socketSend(Socket socket, String strData, Controller.CMP_PACKET respPacket)
    {
        int nRespon = Controller.STATUS_ROK;
        try
        {
            if (socket.isConnected())
            {
                nRespon = Controller.cmpRequest(Controller.deidentify_request, strData,
                        respPacket, socket);
                Logs.showTrace("[ContactClient] socketSend Response Code: " + nRespon + " Data: " + respPacket.cmpBody);
            }
            else
            {
                Logs.showError("[ContactClient] socketSend Socket is not connect");
            }
        }
        catch (Exception e)
        {
            nRespon = -1;
            Logs.showError("SocketSend Exception: " + e.toString());
        }
        return nRespon;
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
        private Controller.CMP_PACKET theRespPacket;
        
        SocketSend(Socket socket, String strData, Controller.CMP_PACKET respPacket)
        {
            theSocket = socket;
            theData = strData;
            theRespPacket = respPacket;
        }
        
        @Override
        public void run()
        {
            socketSend(theSocket, theData, theRespPacket);
        }
    }
}
