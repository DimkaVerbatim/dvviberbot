package ua.pp.dvviberbot;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonPatterns {
    private JSONObject jsonPattern = new JSONObject();

    public JSONObject getJsonPatternChoseServices() {
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
}
