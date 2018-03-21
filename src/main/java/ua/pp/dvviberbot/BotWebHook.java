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

    private JsonPatterns jsonPatterns = new JsonPatterns();
    private List<String> listServices = new ArrayList<>();
    private ViberComunicator viberCom = new ViberComunicator();
    private ViberResponse viberResponse = new ViberResponse();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        StringBuilder jb = new StringBuilder();
        String line;
        JSONObject jsonResponse = new JSONObject();
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
            boolean bCorrectSignature = viberCom.isValidateSinature(signature, jb.toString());

            if (!jsonRequst.isNull("event") && bCorrectSignature) {
                /* when a user message is received */
                response.setHeader("X-Viber-Auth-Token", viberCom.getViberToken());
                response.setHeader("Content-Type", "application/json");

                String eventParam = jsonRequst.getString("event");

                String msgSenderId = "";
                String msgSenderName = "клієнт";

                /*check what event come from viber*/
                switch (eventParam) {
                    case "webhook":
                        /* read param webhook*/
                        jsonResponse.put("event_types", "delivered");
                        jsonResponse.put("status_message", "ok");
                        jsonResponse.put("status", 0);
                        break;
                    case "conversation_started":
                        if (!jsonRequst.getJSONObject("user").isNull("name")) {
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
                        break;
                    case "message":
                        ViberMessage viberMsg = new ViberMessage(jsonRequst);
                        // here goes the data to send message back to the user
                        jsonResponse.put("receiver", viberMsg.getSenderId());
                        jsonResponse.put("type", "text");

                        listServices.add("ГВП");
                        listServices.add("ЦО");
                        // Вибрали послугу
                        if (listServices.contains(viberMsg.getMessageText())) {
                            jsonResponse = viberResponse.onChooseService(viberMsg);
                        } else if (viberMsg.getMessageText().toLowerCase().equals("start")) {
                            jsonResponse = jsonPatterns.getJsonPatternStartConversation(viberMsg.getSenderId(), viberMsg.getSenderName());
                        } else if (viberMsg.getMessageTrackingData().startsWith("choose or;")) {
                            jsonResponse = viberResponse.onChooseLC(viberMsg);
                        } else if (viberMsg.getMessageTrackingData().startsWith("send or;")) {
                            jsonResponse = viberResponse.onSendLc(viberMsg);
                        } else if (viberMsg.getMessageTrackingData().startsWith("choose cmd btn;")) {
                            jsonResponse = viberResponse.onChooseCmdBtn(viberMsg);
                        } else if (viberMsg.getMessageTrackingData().startsWith("choose cnt;")) {
                            jsonResponse = viberResponse.onChooseCounter(viberMsg);
                        } else if (viberMsg.getMessageTrackingData().startsWith("send cntmult")) {
                            jsonResponse = viberResponse.onSendMultiCounterData(viberMsg);
                        } else if (viberMsg.getMessageTrackingData().startsWith("send cnt;")) {
                            jsonResponse = viberResponse.onSendCounterData(viberMsg);
                        } else {
                            jsonResponse.put("text", "Шановний(a) " + viberMsg.getSenderName() + ". Робота з іншими командами в розробці! Ви нам надіслали: " + viberMsg.getMessageText());
                            jsonResponse.put("tracking_data", "other command");
                            jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
                        }

                        /* here need send answer for viber */
                        String strResult = viberCom.sendMessage(jsonResponse.toString());

                        /* send answer*/
                        jsonResponse = new JSONObject(strResult);
                        break;
                    default:
                        jsonResponse.put("result", "this event not implemented");
                        jsonResponse.put("event", eventParam);
                        break;
                }
            }
            else {
                jsonResponse.put("result", "this json data not implemented");
            }

            /* send answer*/
            response.getOutputStream().print(jsonResponse.toString());

        } catch (JSONException e) {
            // crash and burn
            System.out.println(e.getMessage()+jb.toString());
            jsonResponse.put("text", "Щось пішло не так, повторіть все спочатку!");
            jsonResponse.put("tracking_data", "crash");
            jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnStart());
            /* here need send answer for viber */
            String strResult = viberCom.sendMessage(jsonResponse.toString());
            /* send answer*/
            jsonResponse = new JSONObject(strResult);
            response.getOutputStream().print(jsonResponse.toString());
            //throw new IOException("Error parsing JSON request string");
        }

    }

}
