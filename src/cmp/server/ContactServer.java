package cmp.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cmp.client.ContactClient;
import cmp.client.Controller;
import common.Logs;

/**
 * Created by Jugo on 2019/7/24
 */

public class ContactServer
{
    private volatile boolean mbRun = false;
    private static ContactServer instance = null;
    private ServerSocket serverSocket = null;
    
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
                        Logs.showTrace("Socket Receive:" + receivePacket.cmpBody);
                        Controller.cmpSend(receivePacket.cmpHeader.command_id)
                    }
                    else
                    {
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
