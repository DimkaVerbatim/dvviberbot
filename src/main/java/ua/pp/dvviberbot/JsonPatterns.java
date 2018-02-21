package ua.pp.dvviberbot;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonPatterns {
    private JSONObject jsonPattern = new JSONObject();

    public JSONObject getJsonPatternBtnChoseServices() {
        JSONArray jsonArrayButtons = new JSONArray();
        JSONObject firstBtnGVP = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"ГВП\", \"Text\": \"ГВП\", \"TextSize\": \"regular\"}");
        JSONObject firstBtnCO = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"ЦО\", \"Text\": \"ЦО\", \"TextSize\": \"regular\"}");
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", true);

        jsonArrayButtons.put(firstBtnGVP);
        jsonArrayButtons.put(firstBtnCO);

        jsonPattern.put("Buttons", jsonArrayButtons);

        return jsonPattern;
    }
    public JSONObject getJsonPatternBtnStart() {
        JSONArray jsonArrayButtons = new JSONArray();
        JSONObject firstBtnStart = new JSONObject("{\"ActionType\": \"reply\", \"ActionBody\": \"start\", \"Text\": \"Спочатку\", \"TextSize\": \"regular\"}");
        jsonPattern.put("Type", "keyboard");
        jsonPattern.put("DefaultHeight", true);
        jsonArrayButtons.put(firstBtnStart);

        jsonPattern.put("Buttons", jsonArrayButtons);

        return jsonPattern;
    }
    public JSONObject getJsonPatternStartConversation(String senderId, String senderName) {
        jsonPattern.put("receiver", senderId);
        jsonPattern.put("text", "Привіт, "+ senderName + "! Вас вітає бот DimkaVerbatim! Для передачі показань оберіть послугу." );
        jsonPattern.put("type", "text");
        jsonPattern.put("tracking_data","start conversation");
        jsonPattern.put("keyboard",this.getJsonPatternBtnChoseServices());

        return jsonPattern;
    }
}
