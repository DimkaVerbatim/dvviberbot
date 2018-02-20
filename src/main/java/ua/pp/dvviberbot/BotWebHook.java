package ua.pp.dvviberbot;

//import com.sun.org.apache.xpath.internal.operations.String;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class BotWebHook extends HttpServlet {

    private final String secretKey = "47659e83e627d6c7-131d26b96d02cf8d-df57301eeea40afb";
    private boolean bCorrectSignature = false;
    private JsonPatterns jsonPatterns = new JsonPatterns();

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        final String signature = request.getHeader("X-Viber-Content-Signature");

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        try {
            response.setContentType("application/json");
            /*convert charset*/
            String strInput = jb.toString();
            String s = new String(strInput.getBytes("ISO-8859-1"), "UTF-8");
            JSONObject jsonRequst = new JSONObject(s);
            JSONObject jsonResponse = new JSONObject();

            ViberSignatureValidator viberSignatureValidator = new ViberSignatureValidator(secretKey);
            if (viberSignatureValidator.isSignatureValid(signature, jb.toString())) {
                bCorrectSignature = true;
            }

            if (!jsonRequst.isNull("event") /*&& bCorrectSignature*/){

                response.setHeader("X-Viber-Auth-Token", secretKey);
                response.setHeader("Content-Type", "application/json");
                String eventParam = jsonRequst.getString("event");

                if (eventParam.equals("webhook")) {
                    /* read param webhook*/
                    jsonResponse.put("event_types","delivered");
                    jsonResponse.put("status_message","ok");
                    jsonResponse.put("status",0);
                }
                else if (eventParam.equals("message")){

                    /* when a user message is received */
                    String msgType = jsonRequst.getJSONObject("message").getString("type");
                    String msgText = jsonRequst.getJSONObject("message").getString("text");
                    String msgSenderId = jsonRequst.getJSONObject("sender").getString("id");
                    String msgSenderName = jsonRequst.getJSONObject("sender").getString("name");
                    String msgTrackingData= jsonRequst.getJSONObject("message").getString("tracking_data");
                    System.out.println(msgTrackingData);

                    // here goes the data to send message back to the user
                    jsonResponse.put("receiver", msgSenderId);
                    jsonResponse.put("text", "Привіт, "+ msgSenderName + "! Це бот DimkaVerbatim! Поки я можу повторювати ваші повідомлення. Ви надіслали мені наступне : " + msgText);
                    jsonResponse.put("type", "text");
                    jsonResponse.put("tracking_data","tracking data");
                    jsonResponse.put("keyboard",jsonPatterns.getJsonPatternChoseServices());

                    /* here need send answer for viber */

                    String strRusult = sendMessage(jsonResponse.toString());

                    /* send answer*/
                    jsonResponse = new JSONObject(strRusult);
                }
                else {
                    jsonResponse.put("result", "this event not implemented");
                    jsonResponse.put("event",eventParam);
                }
            }
            else {
                jsonResponse.put("result", "this json data not implemented");
            }

            /* send answer*/
            response.getOutputStream().print(jsonResponse.toString());

        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

    }
    private String sendMessage(String textMessage) throws IOException {
        final String resourceURL = "https://chatapi.viber.com/pa/send_message";
        // init connection
        URL url = new URL(resourceURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        //  set request method
        con.setRequestMethod("POST");

        // CURLOPT_FOLLOWLOCATION
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("Content-length", String.valueOf(textMessage.length()));
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-Viber-Auth-Token", secretKey);
        con.setDoOutput(true);
        con.setDoInput(true);

        DataOutputStream output = new DataOutputStream(con.getOutputStream());
        /*change charset*/
        String s = new String(textMessage.getBytes("UTF-8"), "ISO-8859-1");
        output.writeBytes(s);
        output.close();

        // "Post data send ... waiting for reply");
        int code = con.getResponseCode(); // 200 = HTTP_OK

        // read the response
        DataInputStream input = new DataInputStream(con.getInputStream());
        int c;
        StringBuilder resultBuf = new StringBuilder();
        while ( (c = input.read()) != -1) {
            resultBuf.append((char) c);
        }
        input.close();

        return resultBuf.toString();
    }

}
