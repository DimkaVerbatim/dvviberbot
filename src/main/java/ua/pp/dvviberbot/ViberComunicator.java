package ua.pp.dvviberbot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class ViberComunicator {
    private Properties viberProp = new Properties();
    public ViberComunicator () {
        try {
            viberProp.load(getClass().getResourceAsStream("/Viber.properties"));
        }
        catch (IOException ex){
            System.out.println("Error reading properties "+ex.getMessage());
        }

    }
    public String getViberToken(){
        return viberProp.getProperty("X-Viber-Auth-Token");
    }
    public boolean isValidateSinature(String signature, String data){
        ViberSignatureValidator viberSignatureValidator = new ViberSignatureValidator(getViberToken());
        return viberSignatureValidator.isSignatureValid(signature, data);
    }
    public String sendMessage(String textMessage) throws IOException {
        final String resourceURL = "https://chatapi.viber.com/pa/send_message";
        String result = null;
        // init connection
        URL url = new URL(resourceURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        //  set request method
        con.setRequestMethod("POST");

        // CURLOPT_FOLLOWLOCATION
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("Content-length", String.valueOf(textMessage.length()));
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-Viber-Auth-Token", getViberToken());
        con.setDoOutput(true);
        con.setDoInput(true);

        DataOutputStream output = new DataOutputStream(con.getOutputStream());
        /*change charset*/
        String s = new String(textMessage.getBytes("UTF-8"), "ISO-8859-1");
        output.writeBytes(s);
        output.close();

        // "Post data send ... waiting for reply");
        int code = con.getResponseCode(); // 200 = HTTP_OK

        if (code == 200) {
            // read the response
            DataInputStream input = new DataInputStream(con.getInputStream());
            int c;
            StringBuilder resultBuf = new StringBuilder();
            while ((c = input.read()) != -1) {
                resultBuf.append((char) c);
            }
            input.close();
            result = resultBuf.toString();
        }

        return result;
    }
}
