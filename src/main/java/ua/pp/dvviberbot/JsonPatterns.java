package ua.pp.dvviberbot;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonPatterns {
    private JSONObject jsonPattern = new JSONObject();

    private JSONObject getJsonPatternBtnChooseServices() {
        JSONArray jsonArrayButtons = new JSONArray();
        JSONObject firstBtnGVP = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"ГВП\", \"Text\": \"ГВП\", \"TextSize\": \"regular\"}");
        JSONObject firstBtnCO = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"ЦО\", \"Text\": \"ЦО\", \"TextSize\": \"regular\"}");
        /*JSONObject testBtn = new JSONObject("{\n" +
                "            \"Columns\":2,\n" +
                "            \"BgColor\":\"#e2e7e7\",\n" +
                "            \"Rows\":1,\n" +
                "            \"ActionType\":\"reply\",\n" +
                "            \"ActionBody\":\"https://www.google.com\",\n" +
                "            \"Text\":\"<font color=#8367db>MORE DETAILS</font>\",\n" +
                "            \"TextSize\":\"small\",\n" +
                "            \"TextVAlign\":\"middle\",\n" +
                "            \"TextHAlign\":\"middle\"\n" +
                "         }");*/
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", false);

        jsonArrayButtons.put(firstBtnGVP);
        jsonArrayButtons.put(firstBtnCO);
        //jsonArrayButtons.put(testBtn);

        jsonPattern.put("Buttons", jsonArrayButtons);

        return jsonPattern;
    }
    public JSONObject getJsonPatternBtnChooseCounter(JSONObject jsonCounters) {
        // add btn counters if we have more than one counter
        JSONArray jsonArrayButtons = new JSONArray();
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", false);
        int cntCounters = jsonCounters.getJSONArray("data").length();
        for (int i=0; i<cntCounters; i++){
            JSONObject firstBtnCnt = new JSONObject("{"
                    + "\"ActionType\": \"reply\","
                    + " \"ActionBody\": \""
                        + "cnt;"
                        + jsonCounters.getJSONArray("data").getJSONObject(i).getInt("COUNTERID")
                        + ";"
                        + jsonCounters.getJSONArray("data").getJSONObject(i).getDouble("LASTPOKAZ")
                        + ";"
                        + jsonCounters.getJSONArray("data").getJSONObject(i).getString("LASTPOKAZDATE")
                        + ";"
                        + jsonCounters.getJSONArray("data").getJSONObject(i).getDouble("KOEFPOKAZ")
                        + "\","
                    + " \"Text\": \""+jsonCounters.getJSONArray("data").getJSONObject(i).getString("ABCCNT")+"\","
                    + " \"TextSize\": \"regular\"}");
            jsonArrayButtons.put(firstBtnCnt);
        }
        jsonPattern.put("Buttons", jsonArrayButtons);

        return jsonPattern;
    }
    public JSONObject getJsonPatternBtnStart() {
        return getJsonPatternBtnStart("");
    }
    public JSONObject getJsonPatternBtnStart(String addBtnBackActionBody) {
        JSONArray jsonArrayButtons = new JSONArray();
        JSONObject firstBtnStart = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"start\", \"Text\": \"Спочатку\", \"TextSize\": \"regular\"}");
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", false);
        jsonArrayButtons.put(firstBtnStart);
        if (!addBtnBackActionBody.equals("")){
            jsonArrayButtons.put(getJsonPatternBtnBack(addBtnBackActionBody));
        }

        jsonPattern.put("Buttons", jsonArrayButtons);

        return jsonPattern;
    }
    public JSONObject getJsonPatternBtnOR() {
        JSONArray jsonArrayButtons = new JSONArray();
        JSONObject firstBtnAddOR = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"add_or\", \"Text\": \"Додати О/Р\", \"TextSize\": \"regular\"}");
        JSONObject firstBtnChooseOR = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"choose_or\", \"Text\": \"Мої О/Р\", \"TextSize\": \"regular\"}");
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", false);

        jsonArrayButtons.put(firstBtnAddOR);
        jsonArrayButtons.put(firstBtnChooseOR);
        jsonArrayButtons.put(getJsonPatternBtnBack());
        jsonPattern.put("Buttons", jsonArrayButtons);
        return jsonPattern;
    }
    public JSONObject getJsonPatternBtnChooseOR(JSONObject jsonListOR){
        // add btn OR
        JSONArray jsonArrayButtons = new JSONArray();
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", false);
        int cntOR = jsonListOR.getJSONArray("Accounts").length();
        for (int i=0; i<cntOR; i++){
            JSONObject firstBtnOR = new JSONObject("{"
                    + "\"ActionType\": \"reply\","
                    + " \"ActionBody\": \""
                    + jsonListOR.getJSONArray("Accounts").getString(i)
                    + "\","
                    + " \"Text\": \""+jsonListOR.getJSONArray("Accounts").getString(i)+"\","
                    + " \"TextSize\": \"regular\"}");
            jsonArrayButtons.put(firstBtnOR);
        }
        jsonArrayButtons.put(getJsonPatternBtnBack());
        jsonPattern.put("Buttons", jsonArrayButtons);
        return jsonPattern;
    }
    public JSONObject getJsonPatternStartConversation(String senderId, String senderName) {
        jsonPattern.put("receiver", senderId);
        jsonPattern.put("text", "Привіт "+ senderName + ", Вас вітає бот DimkaVerbatim! Для передачі показань оберіть послугу." );
        jsonPattern.put("type", "text");
        jsonPattern.put("tracking_data","start conversation");
        jsonPattern.put("keyboard",new JsonPatterns().getJsonPatternBtnChooseServices());

        return jsonPattern;
    }
    private JSONObject getJsonPatternBtnBack(String actionBoby){
        return new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \""+actionBoby+"\", \"Text\": \"Назад\", \"TextSize\": \"regular\"}");
    }
    private JSONObject getJsonPatternBtnBack(){
        return getJsonPatternBtnBack("back");
    }
}
