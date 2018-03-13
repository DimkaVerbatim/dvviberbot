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
    private ExternalDataService extDataServ = new ExternalDataService();

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

            if (!jsonRequst.isNull("event") && bCorrectSignature){
                /* when a user message is received */
                response.setHeader("X-Viber-Auth-Token", viberCom.getViberToken());
                response.setHeader("Content-Type", "application/json");

                String eventParam = jsonRequst.getString("event");

                String msgSenderId = "";
                String msgSenderName = "клієнт";

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
                    ViberMessage viberMsg = new ViberMessage(jsonRequst);
                    Sender sender = new Sender(viberMsg);
                    // here goes the data to send message back to the user
                    jsonResponse.put("receiver", viberMsg.getSenderId());
                    jsonResponse.put("type", "text");

                    listServices.add("ГВП");
                    listServices.add("ЦО");
                    // Вибрали послугу
                    if (listServices.contains(viberMsg.getMessageText())) {
                        sender.insertIntoDB();
                        jsonResponse.put("text", "Оберіть або додайте № О/Р по послузі " + viberMsg.getMessageText() + ":");
                        jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
                        jsonResponse.put("tracking_data", "choose or;" + viberMsg.getMessageText());

                    }

                    else if (viberMsg.getMessageTrackingData().startsWith("choose or;")) {
                        //Take param from previous message
                        String [] strParam;
                        strParam = viberMsg.getMessageTrackingData().split(";");
                        if (viberMsg.getMessageText().equals("add_or")){
                            jsonResponse.put("text", "Введіть, будь ласка, № О/Р по послузі " + strParam[1] + ":");
                            jsonResponse.put("tracking_data", "send or;" + strParam[1]);

                        }
                        else if (viberMsg.getMessageText().equals("choose_or")){
                            JSONObject jsonAccs = sender.getAccounts(strParam[1]);
                            if (jsonAccs.getJSONArray("Accounts").length() > 0) {
                                jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnChooseOR(jsonAccs));
                                jsonResponse.put("text", "Оберіть, будь ласка, О/Р по послузі " + strParam[1] + ":");
                                jsonResponse.put("tracking_data", "send or;" + strParam[1]);
                            }
                            else{
                                jsonResponse.put("text", "Відсудсутні збережені О/Р, додайте О/Р по послузі " + strParam[1] + ":");
                                jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
                                jsonResponse.put("tracking_data", "choose or;" + strParam[1]);
                            }
                        }
                        else if (viberMsg.getMessageText().equals("back")){
                            jsonResponse = jsonPatterns.getJsonPatternStartConversation(viberMsg.getSenderId(), viberMsg.getSenderName());
                        }

                    }
                    else if (viberMsg.getMessageTrackingData().startsWith("send or;")) {
                        //Take param from previous message
                        String [] strParam;
                        strParam = viberMsg.getMessageTrackingData().split(";");
                        // CHOOSE COUNTER OR INPUT COUNTER DATA
                        if (viberMsg.getMessageText().equals("back")){
                            jsonResponse.put("text", "Оберіть або додайте № О/Р по послузі " + viberMsg.getMessageText() + ":");
                            jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
                            jsonResponse.put("tracking_data", "choose or;" + strParam[1]);
                        }
                        else {
                            if (viberMsg.getMessageText().matches("^[0-9]{15}$")) {

                                String resultServ = extDataServ.getLCCounterInfoBitek(viberMsg.getMessageText(), strParam[1]);
                                if (!resultServ.equals("{\"data\":}")) {
                                    JSONObject jsonResultServ = new JSONObject(resultServ);
                                    int counterCount = jsonResultServ.getJSONArray("data").length();
                                    if (counterCount == 1) {
                                        sender.addAccount(strParam[1], viberMsg.getMessageText());
                                        String msgForAnswer = "Адреса: "
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("FULLADRESS")
                                                + "\nОстані передані показники: "
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getDouble("LASTPOKAZ")
                                                + " на " + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("LASTPOKAZDATE")
                                                + "\nВведіть поточні показання:";
                                        jsonResponse.put("text", msgForAnswer);
                                        jsonResponse.put("tracking_data", "send cnt;"
                                                + strParam[1] + ";"
                                                + viberMsg.getMessageText() + ";"
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getInt("COUNTERID") + ";"
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getDouble("LASTPOKAZ") + ";"
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getDouble("KOEFPOKAZ"));
                                    } else if (counterCount > 1) {
                                        sender.addAccount(strParam[1], viberMsg.getMessageText());
                                        String msgForAnswer = "Адреса: "
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("FULLADRESS")
                                                + "\nВиберіть номер лічильника згідно квитанції:";
                                        jsonResponse.put("text", msgForAnswer);
                                        jsonResponse.put("tracking_data", "choose cnt;" + strParam[1] + ";" + viberMsg.getMessageText());
                                        jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnChooseCounter(jsonResultServ));

                                    } else {
                                        jsonResponse.put("text", "Шановний(a) " + viberMsg.getSenderName() + ". Інформація по О/Р " + viberMsg.getMessageText()
                                                + ", на даний момент не доступна, або відсутня інформація по лічильниках! :-(");
                                        jsonResponse.put("tracking_data", "bad or");
                                        jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());

                                    }
                                } else {
                                    jsonResponse.put("text", "Шановний(a) " + viberMsg.getSenderName() + ". Відсутній зв'язок з сервером, скориcтайтесь сервісом пізніше!");
                                    jsonResponse.put("tracking_data", "crash server");
                                    jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
                                }

                            } else {
                                jsonResponse.put("text", "Ви вказали О/Р не вірного формату! Формат О/Р 15 цифр. Введіть коректний ОР повторно.");
                                jsonResponse.put("tracking_data", viberMsg.getMessageTrackingData());
                            }
                        }
                    }
                    else if (viberMsg.getMessageTrackingData().startsWith("choose cnt;")) {
                        // CHOOSE COUNTER
                        String [] strParam;
                        String [] strParamTracking;
                        strParamTracking = viberMsg.getMessageTrackingData().split(";");
                        strParam = viberMsg.getMessageText().split(";");
                        String msgForAnswer = "Остані передані показники: "
                                + strParam[2]
                                + " на " + strParam[3]
                                + "\nВведіть поточні показання:";
                        jsonResponse.put("text", msgForAnswer);
                        jsonResponse.put("tracking_data", "send cnt;" + strParamTracking[1] + ";" + strParamTracking[2] + ";" + strParam[1] + ";" + strParam[2] + ";" + strParam[4]);

                    }
                    else if (viberMsg.getMessageTrackingData().startsWith("send cnt;")) {
                        // SEND COUNTER DATA
                        if (viberMsg.getMessageText().matches("^[0-9]*[.]?[0-9]+$")) {
                            String [] strParamTracking;
                            strParamTracking = viberMsg.getMessageTrackingData().split(";");
                            if (Double.valueOf(viberMsg.getMessageText())<=0){
                                jsonResponse.put("text", "Вказано показник не коректного значення. Внесіть коректні дані:");
                                jsonResponse.put("tracking_data", viberMsg.getMessageTrackingData());
                            }
                            else {
                                if ((strParamTracking[1].equals("ГВП")  && (
                                                (Double.valueOf(viberMsg.getMessageText()) - Double.valueOf(strParamTracking[4])) < 100))
                                        || (strParamTracking[1].equals("ЦО") && (
                                                (Double.valueOf(viberMsg.getMessageText()) - Double.valueOf(strParamTracking[4])) * Double.valueOf(strParamTracking[5]) < 10))
                                        ){
                                    String resultServ = extDataServ.setCounterDataBitek (
                                            strParamTracking[2],
                                            strParamTracking[1],
                                            Integer.valueOf(strParamTracking[3]),
                                            Double.valueOf(viberMsg.getMessageText()),
                                            Double.valueOf(strParamTracking[4]));

                                    if (!resultServ.equals("{\"data\":}")) {
                                        JSONObject jsonResultServ = new JSONObject(resultServ);
                                        jsonResponse.put("text", "Результат: \n"
                                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("msg"));
                                        jsonResponse.put("tracking_data", "send result");
                                        jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
                                    }
                                    else {
                                        jsonResponse.put("text", "Результат передачі невдалий!");
                                        jsonResponse.put("tracking_data", "send result");
                                        jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
                                    }

                                }
                                else {
                                    jsonResponse.put("text", "Вказано показник занадто великого значення. Внесіть менший показник:");
                                    jsonResponse.put("tracking_data", viberMsg.getMessageTrackingData());
                                }

                            }
                        }
                        else
                        {
                            jsonResponse.put("text", "Вказано показник не коректного значення. Внесіть коректні дані:");
                            jsonResponse.put("tracking_data", viberMsg.getMessageTrackingData());

                        }

                    }
                    else if (viberMsg.getMessageText().toLowerCase().equals("start")){
                        jsonResponse = jsonPatterns.getJsonPatternStartConversation(viberMsg.getSenderId(), viberMsg.getSenderName());

                    }
                    else {
                        jsonResponse.put("text", "Шановний(a) " + viberMsg.getSenderName() + ". Робота з іншими командами в розробці! Ви нам надіслали: " + viberMsg.getMessageText());
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
            System.out.println(e.getMessage()+jb.toString());
            jsonResponse.put("text", "Щось пішло не так, повторіть все спочатку!");
            jsonResponse.put("tracking_data", "crash");
            jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnStart());
            response.getOutputStream().print(jsonResponse.toString());
            //throw new IOException("Error parsing JSON request string");
        }

    }

}
