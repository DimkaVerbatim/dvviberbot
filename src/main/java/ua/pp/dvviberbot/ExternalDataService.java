package ua.pp.dvviberbot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ExternalDataService {
    private Properties webServProp = new Properties();
    public ExternalDataService(){
        try {
            webServProp.load(getClass().getResourceAsStream("/CounterWebServicesAPI.properties"));
        }
        catch (IOException ex){
            System.out.println("Error reading properties "+ex.getMessage());
        }
    }
    private String getDataLCFromUrl(String url, String lc, String usl){
        String resultExec = "{\"data\":}";
        url = webServProp.getProperty("URL-CO-GVP-API") + "/" + url;
        try {
            resultExec = "{\"data\":"+getPostWebServise(url, "lc="+lc+"&usl="+usl, webServProp.getProperty("Auth-Token-CO-GVP"),"X-API-Auth-Token")+"}";
            return resultExec;
        }
        catch (IOException ex){
            System.out.println("Error get webservices" + ex.getMessage());
            return resultExec;
        }
    }
    public String getLCCounterInfoBitek (String lc, String usl) {
        return getDataLCFromUrl("getAbonentInfoByLC", lc, usl);
    }
    public String getAbonentAddressInfoBitek (String lc, String usl) {
        return getDataLCFromUrl("getAbonentAddressFromLC", lc, usl);
    }
    public String getLCAbonentMonthsInfoBitek (String lc, String usl) {
        return getDataLCFromUrl("getAbonentInfoMonthsByLCUSL", lc, usl);
    }
    public String setCounterDataBitek (String lc,
                                       String usl,
                                       int counterId,
                                       double currentPokaz1,
                                       double priPokaz1,
                                       double currentPokaz2,
                                       double priPokaz2,
                                       double currentPokaz3,
                                       double priPokaz3,
                                       double currentPokaz4,
                                       double priPokaz4
    ) {
        String url;
        url = webServProp.getProperty("URL-CO-GVP-API") + "/setCountersDataOtherSource";
        String resultExec = "{\"data\":}";
        try {
            resultExec = "{\"data\":"+getPostWebServise(url,
                    "uslName=" + usl
                            + "&lc=" + lc
                            + "&counterId=" + counterId
                            + "&currentPokaz1=" + currentPokaz1
                            +"&priPokaz1=" + priPokaz1
                            + "&currentPokaz2=" + currentPokaz2
                            +"&priPokaz2=" + priPokaz2
                            + "&currentPokaz3=" + currentPokaz3
                            +"&priPokaz3=" + priPokaz3
                            + "&currentPokaz4=" + currentPokaz4
                            +"&priPokaz4=" + priPokaz4,
                    webServProp.getProperty("Auth-Token-CO-GVP"),"X-API-Auth-Token")+"}";
            return resultExec;
        }
        catch (IOException ex){
            System.out.println("Error get webservices" + ex.getMessage());
            return resultExec;
        }
    }
    private String getPostWebServise(String urlService, String urlParameters, String authToken, String authTokenName) throws IOException {

        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int    postDataLength = postData.length;
        String result = "";
        URL    url            = new URL( urlService );
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        String loginPassword = "web_get_data:xqpb3HWz{cI5";
        String encoded = new sun.misc.BASE64Encoder().encode (loginPassword.getBytes());
        conn.setRequestProperty ("Authorization", "Basic " + encoded);
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( authTokenName, authToken);
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        // send post param
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
        }

        int code = conn.getResponseCode();
        // read the response
        if (code == 200) {
            DataInputStream input = new DataInputStream(conn.getInputStream());
            int c;
            StringBuilder resultBuf = new StringBuilder();
            while ((c = input.read()) != -1) {
                resultBuf.append((char) c);
            }
            result =  new String(resultBuf.toString().getBytes("ISO-8859-1"), "UTF-8");
            input.close();
            conn.disconnect();
        }

        return result;

    }
}
