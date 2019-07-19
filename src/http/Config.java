package http;

/**
 * Created by Jugo on 2018/1/25
 */

abstract class Config
{
    static final String ENCODING = "UTF-8";
    static final int TIME_OUT_CONNECT = 15000;
    static final int TIME_OUT_READ = 15000;
    
    public static enum HTTP_DATA_TYPE
    {
        FORM_DATA("multipart/form-data"),
        X_WWW_FORM("application/x-www-form-urlencoded"),
        RAW("application/x-www-form-urlencoded"),
        JSON("application/json"),
        XML("application/xml");
        
        private final String name;
        
        HTTP_DATA_TYPE(String s)
        {
            name = s;
        }
        
        public boolean equalsName(String otherName)
        {
            return name.equals(otherName);
        }
        
        public String toString()
        {
            return this.name;
        }
    }
    
}
