package ua.pp.dvviberbot;

import org.json.JSONObject;

public class ViberResponse {
    private JsonPatterns jsonPatterns = new JsonPatterns();
    private ExternalDataService extDataServ = new ExternalDataService();
    private Sender sender;

    private void setResponceSender(JSONObject jsonResponse, String senderId, String type){
        jsonResponse.put("receiver", senderId);
        jsonResponse.put("type", type);
    }

    //Response when user choose usl
    public JSONObject onChooseService(ViberMessage viberMessage){
        JSONObject jsonResponse = new JSONObject();
        setResponceSender(jsonResponse, viberMessage.getSenderId(), "text");
        setSender(viberMessage);
        sender.insertIntoDB();
        jsonResponse.put("text", "Оберіть або додайте № О/Р по послузі " + viberMessage.getMessageText() + ":");
        jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
        jsonResponse.put("tracking_data", "choose or;" + viberMessage.getMessageText());
        return jsonResponse;
    }

    //Response when user send pokaz multicounter
    public JSONObject onSendMultiCounterData(ViberMessage viberMessage){
        JSONObject jsonResponse = new JSONObject();
        setResponceSender(jsonResponse, viberMessage.getSenderId(), "text");
        String [] strParamTracking = viberMessage.getMessageTrackingData().split(";");
        if (viberMessage.getMessageText().matches("^[0-9]*[.]?[0-9]+$")) {
            if (Double.valueOf(viberMessage.getMessageText()) <= 0) {
                jsonResponse.put("text", "Вказано показник не коректного значення. Внесіть коректні дані:");
                jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
            } else {
                String strDataNumber = strParamTracking[0];
                String msgForAnswer;
                switch (strDataNumber){
                    case "send cntmult1":
                        if ((Double.valueOf(viberMessage.getMessageText()) - Double.valueOf(strParamTracking[4])) < 100) {
                            msgForAnswer = "Остані передані показники: "
                                    + "по температурі " + strParamTracking[7] + " : "
                                    + strParamTracking[8]
                                    + " на " + strParamTracking[9]
                                    + "\nВведіть поточні показання по температурі "+ strParamTracking[7] +":";
                            jsonResponse.put("text", msgForAnswer);
                            jsonResponse.put("tracking_data",
                                    viberMessage.getMessageTrackingData().replaceAll(
                                            "send cntmult1","send cntmult2")+";"+viberMessage.getMessageText()
                            );
                        }
                        else {
                            jsonResponse.put("text", "Вказано показник занадто великого значення. Внесіть менший показник:");
                            jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
                        }

                        break;
                    case "send cntmult2":
                        if ((Double.valueOf(viberMessage.getMessageText()) - Double.valueOf(strParamTracking[8])) < 100) {
                            msgForAnswer = "Остані передані показники: "
                                    + "по температурі " + strParamTracking[10] + " : "
                                    + strParamTracking[11]
                                    + " на " + strParamTracking[12]
                                    + "\nВведіть поточні показання по температурі "+ strParamTracking[10] +":";
                            jsonResponse.put("text", msgForAnswer);
                            jsonResponse.put("tracking_data",
                                    viberMessage.getMessageTrackingData().replaceAll(
                                            "send cntmult2","send cntmult3")+";"+viberMessage.getMessageText()
                            );
                        }
                        else {
                            jsonResponse.put("text", "Вказано показник занадто великого значення. Внесіть менший показник:");
                            jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
                        }
                        break;
                    case "send cntmult3":
                        if ((Double.valueOf(viberMessage.getMessageText()) - Double.valueOf(strParamTracking[11])) < 100) {
                            msgForAnswer = "Остані передані показники: "
                                    + "по температурі " + strParamTracking[13] + " : "
                                    + strParamTracking[14]
                                    + " на " + strParamTracking[15]
                                    + "\nВведіть поточні показання по температурі "+ strParamTracking[13] +":";
                            jsonResponse.put("text", msgForAnswer);
                            jsonResponse.put("tracking_data",
                                    viberMessage.getMessageTrackingData().replaceAll(
                                            "send cntmult3","send cntmult4")+";"+viberMessage.getMessageText()
                            );
                        }
                        else {
                            jsonResponse.put("text", "Вказано показник занадто великого значення. Внесіть менший показник:");
                            jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
                        }
                        break;
                    case "send cntmult4":
                        if ((Double.valueOf(viberMessage.getMessageText()) - Double.valueOf(strParamTracking[14])) < 100) {
                            try {
                                String resultServ = extDataServ.setCounterDataBitek (
                                        strParamTracking[2],
                                        strParamTracking[1],
                                        Integer.valueOf(strParamTracking[3]),
                                        Double.valueOf(strParamTracking[16]),
                                        Double.valueOf(strParamTracking[4]),
                                        Double.valueOf(strParamTracking[17]),
                                        Double.valueOf(strParamTracking[8]),
                                        Double.valueOf(strParamTracking[18]),
                                        Double.valueOf(strParamTracking[11]),
                                        Double.valueOf(viberMessage.getMessageText()),
                                        Double.valueOf(strParamTracking[14])
                                );
                                analysisSendCounter(jsonResponse, resultServ, strParamTracking);
                            }
                            catch (Exception ex){
                                System.out.println(viberMessage.getMessageTrackingData());
                                ex.printStackTrace();
                                jsonResponse.put("text", "Щось пішло не так, повторіть все спочатку!");
                                jsonResponse.put("tracking_data", "crash");
                                jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnStart());
                            }

                        }
                        else {
                            jsonResponse.put("text", "Вказано показник занадто великого значення. Внесіть менший показник:");
                            jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return jsonResponse;
    }

    //Response when user choose counter
    public JSONObject onChooseCounter (ViberMessage viberMessage){
        JSONObject jsonResponse = new JSONObject();
        setResponceSender(jsonResponse, viberMessage.getSenderId(), "text");
        // CHOOSE COUNTER
        String [] strParam = viberMessage.getMessageText().split(";");
        String [] strParamTracking= viberMessage.getMessageTrackingData().split(";");
        if (strParam[0].equals("cntmulti")) {
            String msgForAnswer = "Остані передані показники: "
                    + "по температурі " + strParam[2] + " : "
                    + strParam[3]
                    + " на " + strParam[4]
                    + "\nВведіть поточні показання по температурі "+ strParam[2] +":";
            jsonResponse.put("text", msgForAnswer);
            jsonResponse.put("tracking_data", "send cntmult1;"
                    + strParamTracking[1] + ";"
                    + strParamTracking[2] + ";"
                    + strParam[1] + ";"
                    + strParam[3] + ";"
                    + 1 + ";"
                    + strParamTracking[3] + ";"
                    + strParam[5] + ";"
                    + strParam[6] + ";"
                    + strParam[7] + ";"
                    + strParam[8] + ";"
                    + strParam[9] + ";"
                    + strParam[10] + ";"
                    + strParam[11] + ";"
                    + strParam[12] + ";"
                    + strParam[13]
            );
        }
        else {
            String msgForAnswer = "Остані передані показники: "
                    + strParam[2]
                    + " на " + strParam[3]
                    + "\nВведіть поточні показання:";
            jsonResponse.put("text", msgForAnswer);
            jsonResponse.put("tracking_data", "send cnt;" + strParamTracking[1] + ";" + strParamTracking[2] + ";" + strParam[1] + ";" + strParam[2] + ";" + strParam[4] + ";" + strParamTracking[3]);
        }
        return jsonResponse;
    }

    //Response when user select "choose or"
    public JSONObject onChooseLC (ViberMessage viberMessage){
        JSONObject jsonResponse = new JSONObject();
        setResponceSender(jsonResponse, viberMessage.getSenderId(), "text");
        //Take param from previous message
        String [] strParam = viberMessage.getMessageTrackingData().split(";");
        if (viberMessage.getMessageText().equals("add_or")){
            jsonResponse.put("text", "Введіть, будь ласка, № О/Р по послузі " + strParam[1] + ":");
            jsonResponse.put("tracking_data", "send or;" + strParam[1]);

        }
        else if (viberMessage.getMessageText().equals("choose_or")){
            JSONObject jsonAccs = sender.getAccounts(strParam[1]);
            if (jsonAccs.getJSONArray("Accounts").length() > 0) {
                jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnChooseOR(jsonAccs));
                jsonResponse.put("text", "Оберіть, будь ласка, О/Р по послузі " + strParam[1] + ":");
                jsonResponse.put("tracking_data", "send or;" + strParam[1]);
            }
            else{
                jsonResponse.put("text", "Відсутні збережені О/Р, натисніть кнопку \"Додати О/Р\" по послузі " + strParam[1] + ":");
                jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
                jsonResponse.put("tracking_data", "choose or;" + strParam[1]);
            }
        }
        else if (viberMessage.getMessageText().equals("back")){
            jsonResponse = jsonPatterns.getJsonPatternStartConversation(viberMessage.getSenderId(), viberMessage.getSenderName());
        }
        return jsonResponse;
    }
    
    //Response when user send counter data
    public JSONObject onSendCounterData(ViberMessage viberMessage){
        JSONObject jsonResponse = new JSONObject();
        setResponceSender(jsonResponse, viberMessage.getSenderId(), "text");
        if (viberMessage.getMessageText().matches("^[0-9]*[.]?[0-9]+$")) {
            String [] strParamTracking = viberMessage.getMessageTrackingData().split(";");
            if (Double.valueOf(viberMessage.getMessageText())<=0){
                jsonResponse.put("text", "Вказано показник не коректного значення. Внесіть коректні дані:");
                jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
            }
            else {
                if ((strParamTracking[1].equals("ГВП")  && (
                        (Double.valueOf(viberMessage.getMessageText()) - Double.valueOf(strParamTracking[4])) < 100))
                        || (strParamTracking[1].equals("ЦО") && (
                        (Double.valueOf(viberMessage.getMessageText()) - Double.valueOf(strParamTracking[4])) * Double.valueOf(strParamTracking[5]) < 10))
                        ){
                    String resultServ = extDataServ.setCounterDataBitek (
                            strParamTracking[2],
                            strParamTracking[1],
                            Integer.valueOf(strParamTracking[3]),
                            Double.valueOf(viberMessage.getMessageText()),
                            Double.valueOf(strParamTracking[4]),
                            0,
                            0,
                            0,
                            0,
                            0,
                            0
                    );

                    analysisSendCounter(jsonResponse, resultServ, strParamTracking);
                }
                else {
                    jsonResponse.put("text", "Вказано показник занадто великого значення. Внесіть менший показник:");
                    jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
                }
            }
        }
        else
        {
            jsonResponse.put("text", "Вказано показник не коректного значення. Внесіть коректні дані:");
            jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());

        }
        return jsonResponse;
    }
    
    // Response when user choose cmd btn
    public JSONObject onChooseCmdBtn (ViberMessage viberMessage){
        JSONObject jsonResponse = new JSONObject();
        //Take param from previous message
        String [] strParam = viberMessage.getMessageTrackingData().split(";");
        setResponceSender(jsonResponse, viberMessage.getSenderId(), "text");
        // CHOOSE COUNTER OR INPUT COUNTER DATA
        switch (viberMessage.getMessageText()) {
            case "back":
                jsonResponse.put("text", "Оберіть або додайте № О/Р по послузі " + strParam[1] + ":");
                jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
                jsonResponse.put("tracking_data", "choose or;" + strParam[1]);
                break;
            case "sendcounter":
                String resultServ = extDataServ.getLCCounterInfoBitek(strParam[2], strParam[1]);
                if (!resultServ.equals("{\"data\":}")) {
                    JSONObject jsonResultServ = new JSONObject(resultServ);
                    int counterCount = jsonResultServ.getJSONArray("data").length();
                    if (counterCount == 1) {
                        String msgForAnswer = "Адреса: "
                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("FULLADRESS")
                                + "\nОстані передані показники: "
                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getDouble("LASTPOKAZ")
                                + " на " + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("LASTPOKAZDATE")
                                + "\nВведіть поточні показання:";
                        jsonResponse.put("text", msgForAnswer);
                        jsonResponse.put("tracking_data", "send cnt;"
                                + strParam[1] + ";"
                                + strParam[2] + ";"
                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getInt("COUNTERID") + ";"
                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getDouble("LASTPOKAZ") + ";"
                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getDouble("KOEFPOKAZ") + ";"
                                + counterCount);
                    } else if (counterCount > 1) {
                        String msgForAnswer = "Адреса: "
                                + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("FULLADRESS")
                                + "\nВиберіть номер лічильника згідно квитанції:";
                        jsonResponse.put("text", msgForAnswer);
                        jsonResponse.put("tracking_data", "choose cnt;" + strParam[1] + ";" + strParam[2] + ";" + counterCount);
                        jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnChooseCounter(jsonResultServ));

                    } else {
                        jsonResponse.put("text", "Шановний(a) " + viberMessage.getSenderName() + ". Інформація по О/Р " + strParam[2]
                                + ", на даний момент не доступна, або відсутня інформація по лічильниках! :-(");
                        jsonResponse.put("tracking_data", "bad or");
                        jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());

                    }
                } else {
                    jsonResponse.put("text", "Шановний(a) " + viberMessage.getSenderName() + ". Відсутній зв'язок з сервером, скориcтайтесь сервісом пізніше!");
                    jsonResponse.put("tracking_data", "crash server");
                    jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
                }
                break;
            case "back choose cmd btn":
                viberMessage.setMessageText(strParam[2]);
                viberMessage.setMessageTrackingData("send or;"+strParam[1]);
                jsonResponse = onSendLc(viberMessage);
                break;
            case "reportofpays":
                jsonResponse.put("type","file");
                jsonResponse.put("media","https://dbviberbot.munis.com.ua/getinvoicepdf/"+ strParam[1] + "/"+strParam[2]+".pdf");
                jsonResponse.put("size","30000");
                jsonResponse.put("file_name","reportofpays.pdf");
                jsonResponse.put("tracking_data", viberMessage.getMessageTrackingData());
                jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnBackMenu("back choose cmd btn"));
                break;
            default: break;
        }
        return jsonResponse;
    }
    
    // Responce when user send LC
    public JSONObject onSendLc(ViberMessage viberMassage){
        setSender(viberMassage);
        JSONObject jsonResponse = new JSONObject();
        //Take param from previous message
        String [] strParam = viberMassage.getMessageTrackingData().split(";");
        setResponceSender(jsonResponse, viberMassage.getSenderId(), "text");
        // CHOOSE COUNTER OR INPUT COUNTER DATA
        if (viberMassage.getMessageText().equals("back")){
            jsonResponse.put("text", "Оберіть або додайте № О/Р по послузі " + strParam[1] + ":");
            jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnOR());
            jsonResponse.put("tracking_data", "choose or;" + strParam[1]);
        }
        else {
            if (viberMassage.getMessageText().matches("^[0-9]{15}$")) {
                String resultServ = extDataServ.getAbonentAddressInfoBitek(viberMassage.getMessageText(), strParam[1]);
                if (!resultServ.equals("{\"data\":}")) {
                    JSONObject jsonResultServ = new JSONObject(resultServ);
                    int resultCode = jsonResultServ.getJSONArray("data").getJSONObject(0).getInt("CODE");
                    if (resultCode == 1){
                        sender.addAccount(strParam[1], viberMassage.getMessageText());
                        String strAddress = jsonResultServ.getJSONArray("data").getJSONObject(0).getString("MSG");
                        String msgForAnswer = "Адреса: "
                                + strAddress
                                + "\nОберіть дію:";
                        jsonResponse.put("text", msgForAnswer);
                        jsonResponse.put("keyboard",jsonPatterns.getJsonPatternBtnReportOrCounter());
                        jsonResponse.put("tracking_data", "choose cmd btn;" + strParam[1] + ";" + viberMassage.getMessageText());
                    }

                } else {
                    jsonResponse.put("text", "Шановний(a) " + viberMassage.getSenderName() + ". Інформація по О/Р " + viberMassage.getMessageText()
                            + ", на даний момент не доступна, або О/Р некоректний! :-(");
                    jsonResponse.put("tracking_data", "bad or");
                    jsonResponse.put("keyboard", jsonPatterns.getJsonPatternBtnStart());

                }

            } else {
                jsonResponse.put("text", "Ви вказали О/Р не вірного формату! Формат О/Р 15 цифр. Введіть коректний ОР повторно.");
                jsonResponse.put("tracking_data", viberMassage.getMessageTrackingData());
            }
        }
        return jsonResponse;
    }
    private void setSender (ViberMessage viberMassage){
        sender = new Sender(viberMassage);
    }
    private void analysisSendCounter(JSONObject jsonLocalResponce, String resultSendCounter, String paramTracking[]){
        if (!resultSendCounter.equals("{\"data\":}")) {
            JSONObject jsonResultServ = new JSONObject(resultSendCounter);
            jsonLocalResponce.put("text", "Результат: \n"
                    + jsonResultServ.getJSONArray("data").getJSONObject(0).getString("msg"));
            if (Integer.valueOf(paramTracking[6])> 1) {
                jsonLocalResponce.put("tracking_data", "send or;"+paramTracking[1]);
                jsonLocalResponce.put("keyboard", jsonPatterns.getJsonPatternBtnStart(paramTracking[2]));
            }
            else {
                jsonLocalResponce.put("tracking_data", "send result");
                jsonLocalResponce.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
            }

        }
        else {
            jsonLocalResponce.put("text", "Результат передачі невдалий!");
            jsonLocalResponce.put("tracking_data", "send result");
            jsonLocalResponce.put("keyboard", jsonPatterns.getJsonPatternBtnStart());
        }
    }
}
