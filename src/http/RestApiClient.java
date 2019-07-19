package http;


import org.json.JSONObject;

import java.util.HashMap;

import common.EventListener;

public class RestApiClient
{
    private static int msnSerialNUm = 0;
    private ResponseListener responseListener = null;
    
    public static interface ResponseListener
    {
        public void onResponse(JSONObject jsonObject);
    }
    
    public void setResponseListener(ResponseListener listener)
    {
        responseListener = listener;
    }
    
    public String toString()
    {
        return "RestApiClient";
    }
    
    public int HttpsPost(final String httpsURL, final Config.HTTP_DATA_TYPE http_data_type,
            final HashMap<String, String> parameters, Response response)
    {
        response.Id = ++msnSerialNUm;
        Thread thread = new Thread(new HttpsPostRunnable(httpsURL, http_data_type, parameters,
                response));
        thread.start();
        return response.Id;
    }
    
    public int HttpPost(final String httpsURL, final Config.HTTP_DATA_TYPE http_data_type,
            final HashMap<String, String> parameters, Response response)
    {
        response.Id = ++msnSerialNUm;
        Thread thread = new Thread(new HttpPostRunnable(httpsURL, http_data_type, parameters,
                response));
        thread.start();
        return response.Id;
    }
    
    
    private class HttpsPostRunnable implements Runnable
    {
        private String mstrHttpsURL;
        private Config.HTTP_DATA_TYPE mHttp_data_type;
        private HashMap<String, String> mParameters;
        private Response mResponse;
        
        HttpsPostRunnable(final String httpsURL, final Config.HTTP_DATA_TYPE http_data_type,
                final HashMap<String, String> parameters, Response response)
        {
            mstrHttpsURL = httpsURL;
            mHttp_data_type = http_data_type;
            mParameters = parameters;
            mResponse = response;
        }
        
        @Override
        public void run()
        {
            Https.setResponseListener(callback);
            Https.POST(mstrHttpsURL, mHttp_data_type, mParameters, mResponse);
        }
    }
    
    private class HttpPostRunnable implements Runnable
    {
        private String mstrHttpsURL;
        private Config.HTTP_DATA_TYPE mHttp_data_type;
        private HashMap<String, String> mParameters;
        private Response mResponse;
        
        HttpPostRunnable(final String httpsURL, final Config.HTTP_DATA_TYPE http_data_type,
                final HashMap<String, String> parameters, Response response)
        {
            mstrHttpsURL = httpsURL;
            mHttp_data_type = http_data_type;
            mParameters = parameters;
            mResponse = response;
        }
        
        @Override
        public void run()
        {
            Http.setResponseListener(callback);
            Http.POST(mstrHttpsURL, mHttp_data_type, mParameters, mResponse);
        }
    }
    
    private EventListener.Callback callback = new EventListener.Callback()
    {
        @Override
        public void onEvent(JSONObject jsonObject)
        {
            System.out.println("[RestApiClient] EventListener.Callback: " + jsonObject.toString());
            if (null != responseListener)
            {
                responseListener.onResponse(jsonObject);
            }
        }
    };
}
