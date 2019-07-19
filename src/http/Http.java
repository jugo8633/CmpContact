package http;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import common.EventListener;
import common.Logs;

/**
 * Created by Jugo on 2018/3/28
 */
abstract class Http
{
    private static EventListener.Callback eventListener = null;
    
    static void setResponseListener(EventListener.Callback listener)
    {
        eventListener = listener;
    }
    
    static void POST(final String httpsURL, final Config.HTTP_DATA_TYPE http_data_type, final
    HashMap<String, String> parameters, Response response)
    {
        JSONObject jsonResponse = new JSONObject();
        
        try
        {
            jsonResponse.put("id", response.Id);
            jsonResponse.put("code", -1);
            String strParameter = getPostDataString(parameters);
            Logs.showTrace("[Http] POST : URL=" + httpsURL + " Data Type=" + http_data_type
                    .toString() + " Parameter:" + strParameter);
            URL url = new URL(httpsURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(Config.TIME_OUT_CONNECT);
            con.setReadTimeout(Config.TIME_OUT_READ);
            con.setRequestProperty("Content-length", String.valueOf(strParameter.length()));
            con.setRequestProperty("Content-Type", http_data_type.toString());
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setDoOutput(true);
            con.setDoInput(true);
            
            DataOutputStream output = new DataOutputStream(con.getOutputStream());
            output.writeBytes(strParameter);
            output.close();
            
            response.Code = con.getResponseCode();
            if (response.Code == HttpURLConnection.HTTP_OK)
            {
                response.Data = "";
                String line;
                StringBuilder stringBuffer = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line = br.readLine()) != null)
                {
                    stringBuffer.append(line);
                }
                response.Data = stringBuffer.toString();
            }
            else
            {
                Logs.showTrace("[Http] ERROR HTTP Response Code:" + response.Code);
            }
            jsonResponse.put("code", response.Code);
            jsonResponse.put("data", response.Data);
        }
        catch (Exception e)
        {
            Logs.showError("[Http] POST Exception: " + e.getMessage());
        }
        
        if (null != eventListener)
        {
            eventListener.onEvent(jsonResponse);
        }
        
        Logs.showTrace("[Http] POST Response: " + jsonResponse.toString());
    }
    
    private static String getPostDataString(HashMap<String, String> params)
            throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (HashMap.Entry<String, String> entry : params.entrySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                result.append('&');
            }
            
            result.append(URLEncoder.encode(entry.getKey(), Config.ENCODING));
            result.append('=');
            result.append(URLEncoder.encode(entry.getValue(), Config.ENCODING));
        }
        
        return result.toString();
    }
}
