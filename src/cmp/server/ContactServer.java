package cmp.server;

import org.json.JSONObject;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import common.Controller;
import common.Logs;


/**
 * Created by Jugo on 2019/7/24
 */

public class ContactServer
{
    private volatile boolean mbRun = false;
    private static ContactServer instance = null;
    private ServerSocket serverSocket = null;
    private ContactServer.ReceiveListener receiveListener = null; // 去識別
    private ContactServer.StatusListener statusListener = null; // 狀態
    private ContactServer.OptionListener optionListener = null; // 操作
    
    public static interface ReceiveListener
    {
        public void onReceive(String strData);
    }
    
    public static interface StatusListener
    {
        public String onStatus(String strData);
    }
    
    public static interface OptionListener
    {
        public void onOption(String strData);
    }
    
    public void setReceiveListener(ContactServer.ReceiveListener listener)
    {
        receiveListener = listener;
    }
    
    public void setStatusListener(ContactServer.StatusListener listener)
    {
        statusListener = listener;
    }
    
    public void setOptionListener(ContactServer.OptionListener listener)
    {
        optionListener = listener;
    }
    
    
    private ContactServer()
    {
    
    }
    
    public static ContactServer getInstance()
    {
        if (null == instance)
        {
            synchronized (ContactServer.class)
            {
                if (null == instance)
                {
                    instance = new ContactServer();
                }
            }
        }
        return instance;
    }
    
    public int start(int nPort)
    {
        stop();
        try
        {
            serverSocket = new ServerSocket(nPort);
            serverSocket.setReceiveBufferSize(1024 * 1024);
            mbRun = true;
            new Thread(new SocketAccept()).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            serverSocket = null;
            return -1;
        }
        return 0;
    }
    
    public void stop()
    {
        if (null != serverSocket)
        {
            try
            {
                mbRun = false;
                serverSocket.close();
                serverSocket = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void read(Socket socket)
    {
        new Thread(new SocketReceive(socket)).start();
    }
    
    public void send(final int nCommand, final String strBody, Socket msocket, final int nSequence)
    {
        Controller.cmpSend(nCommand, strBody, null, msocket,nSequence);
    }
    
    
    //=============================== Thread Run ==========================================//
    private class SocketAccept implements Runnable
    {
        SocketAccept()
        {
        
        }
        
        @Override
        public void run()
        {
            try
            {
                while (mbRun)
                {
                    Logs.showTrace("Socket Server start Listening");
                    read(serverSocket.accept());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private class SocketReceive implements Runnable
    {
        Socket theSocket;
        
        SocketReceive(Socket socket)
        {
            theSocket = socket;
        }
        
        @Override
        public void run()
        {
            try
            {
                Logs.showTrace("Socket Client Connect");
                while (mbRun)
                {
                    Logs.showTrace("Socket Receive Start");
                    Controller.CMP_PACKET receivePacket = new Controller.CMP_PACKET();
                    if (0 <= Controller.cmpReceive(receivePacket, theSocket, -1))
                    {
                        String strStatus;
                        if(null != receivePacket.cmpBody)
                            receivePacket.cmpBody = receivePacket.cmpBody.trim();
                        switch(receivePacket.cmpHeader.command_id)
                        {
                            case Controller.deidentify_request:
                                Controller.cmpSend(Controller.deidentify_response, null, null, theSocket);
                                if(null != receiveListener)
                                {
                                    receiveListener.onReceive(receivePacket.cmpBody);
                                }
                                break;
                            case Controller.status_request:
                                strStatus = "";
                                if(null != statusListener)
                                {
                                    strStatus = statusListener.onStatus(receivePacket.cmpBody);
                                }
                                Logs.showTrace("send status_response data = " + strStatus);
                                Controller.cmpSend(Controller.status_response, strStatus, null,
                                        theSocket,receivePacket.cmpHeader.sequence_number);
                                break;
                            case Controller.option_request:
                                Controller.cmpSend(Controller.option_response, null, null, theSocket);
                                if(null != optionListener)
                                {
                                    optionListener.onOption(receivePacket.cmpBody);
                                }
                                break;
                        }
                    }
                    else
                    {
                        theSocket.shutdownInput();
                        theSocket.shutdownOutput();
                        theSocket.close();
                        Logs.showTrace("Socket Close");
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
