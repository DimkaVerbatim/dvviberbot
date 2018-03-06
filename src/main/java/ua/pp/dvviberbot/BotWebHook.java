package ua.pp.dvviberbot;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BotWebHook extends HttpServlet {

    private boolean bCorrectSignature = false;
    private JsonPatterns jsonPatterns = new JsonPatterns();
    private List<String> listServices = new ArrayList<String>();
    private ViberComunicator viberCom = new ViberComunicator();

    @Override
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

            bCorrectSignature = viberCom.isValidateSinature(signature, jb.toString());

            if (!jsonRequst.isNull("event") && bCorrectSignature){

                response.setHeader("X-Viber-Auth-Token", viberCom.getViberToken());
                response.setHeader("Content-Type", "application/json");

                String eventParam = jsonRequst.getString("event");

                String msgSenderId = "";
                String msgSenderName = "клієнте";
                String msgSenderAvatar = null;
                String msgSenderCountry = null;
                String msgSenderLanguage = null;
                int msgSenderApi = 1;

                /*check what event come from viber*/
                if (eventParam.equals("webhook")) {
                    /* read param webhook*/
                    jsonResponse.put("event_types","delivered");
                    jsonResponse.put("status_message","ok");
                    jsonResponse.put("status",0);
                }
                else if (eventParam.equals("conversation_started")){

                    if (!jsonRequst.getJSONObject("user").isNull("name")){
                        msgSenderName = jsonRequst.getJSONObject("user").getString("name");
                    }
                    if (!jsonRequst.getJSONObject("user").isNull("id")) {
                        msgSenderId = jsonRequst.getJSONObject("user").getString("id");
                    }

                    jsonResponse = jsonPatterns.getJsonPatternStartConversation(msgSenderId, msgSenderName);

                    /* here need send answer for viber */
                    String strRusult = viberCom.sendMessage(jsonResponse.toString());

                    /* send answer*/
                    jsonResponse = new JSONObject(strRusult);

                }
                else if (eventParam.equals("message")){

                    /* when a user message is received */
                    String msgType = jsonRequst.getJSONObject("message").getString("type");
                    String msgText = jsonRequst.getJSONObject("message").getString("text");

                    if (!jsonRequst.getJSONObject("sender").isNull("name")){
                        msgSenderName = jsonRequst.getJSONObject("sender").getString("name");
                    }
                    if (!jsonRequst.getJSONObject("sender").isNull("id")) {
                        msgSenderId = jsonRequst.getJSONObject("sender").getString("id");
                    }
                    if (!jsonRequst.getJSONObject("sender").isNull("avatar")) {
                        msgSenderAvatar = jsonRequst.getJSONObject("sender").getString("avatar");
                    }
                    if (!jsonRequst.getJSONObject("sender").isNull("country")) {
                        msgSenderCountry = jsonRequst.getJSONObject("sender").getString("country");
                    }
                    if (!jsonRequst.getJSONObject("sender").isNull("language")) {
                        msgSenderLanguage = jsonRequst.getJSONObject("sender").getString("language");
                    }
                    if (!jsonRequst.getJSONObject("sender").isNull("api_version")) {
                        msgSenderApi = jsonRequst.getJSONObject("sender").getInt("api_version");
                    }

                    String msgTrackingData = "";
                    if (!jsonRequst.getJSONObject("message").isNull("tracking_data")) {
                        msgTrackingData = jsonRequst.getJSONObject("message").getString("tracking_data");
                    }
                    
                    Sender sender = new Sender(msgSenderName, msgSenderId, msgSenderAvatar, msgSenderCountry, msgSenderLanguage, msgSenderApi);
                    sender.insertIntoDB();

                    // here goes the data to send message back to the user
                    jsonResponse.put("receiver", msgSenderId);
                    jsonResponse.put("type", "text");

                    listServices.add("ГВП");
                    listServices.add("ЦО");
                    if (listServices.contains(msgText)) {
                        jsonResponse.put("text", "Введіть, будь ласка, № ОР по послузі " + msgText + ":");
                        jsonResponse.put("tracking_data", "send or <" + msgTrackingData + ">");
                    }
                    else if (msgTrackingData.startsWith("send or <")) {
                        if (msgText.matches("[0-9]{15}")) {
                            jsonResponse.put("text", "Шановний(a) " + msgSenderName + ". Інформація по ОР " + msgText + ", на даний момент не доступна! :-(");
                            jsonResponse.put("tracking_data", " bad or");
                            jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnStart());
                        }
                        else {
                            jsonResponse.put("text", "Ви вказали ОР не вірного формату! Формат ОР 15 цифр. Введіть коректний ОР повторно.");
                            jsonResponse.put("tracking_data", msgTrackingData);
                        }
                    }
                    else if (msgText.toLowerCase().equals("start")){
                        jsonResponse = jsonPatterns.getJsonPatternStartConversation(msgSenderId, msgSenderName);
                    }
                    else {
                        jsonResponse.put("text", "Шановний(a) " + msgSenderName+ ". Робота з іншими командами в розробці! Ви нам надіслали: " + msgText);
                        jsonResponse.put("tracking_data", "other command");
                        jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnStart());
                    }

                    /* here need send answer for viber */
                    String strRusult = viberCom.sendMessage(jsonResponse.toString());

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
            System.out.println(jb.toString());
            throw new IOException("Error parsing JSON request string");
        }

    }

}
