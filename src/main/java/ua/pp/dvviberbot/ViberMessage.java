package ua.pp.dvviberbot;

import org.json.JSONObject;

public class ViberMessage {
    private String messageText = "";
    private String senderName = "клієнт";
    private String event = "";
    private String senderId = "";
    private String senderAvatar = "";
    private String senderCountry = "";
    private String senderLanduege = "";
    private int senderApiVersion = 0;
    private String messageTrackingData = "";
    private JSONObject message;

    public ViberMessage(JSONObject message){
        this.message = message;
        initEvent();
        initEventMessageParam();
    }
    private void initEvent(){
        if (!message.isNull("event")){
            event = message.getString("event");
        }
    }
    private void initEventMessageParam(){
        if (!message.getJSONObject("message").isNull("text")) {
            messageText = message.getJSONObject("message").getString("text");
        }
        if (!message.getJSONObject("sender").isNull("name")){
            senderName = message.getJSONObject("sender").getString("name");
        }
        if (!message.getJSONObject("sender").isNull("id")) {
            senderId = message.getJSONObject("sender").getString("id");
        }
        if (!message.getJSONObject("sender").isNull("avatar")) {
            senderAvatar = message.getJSONObject("sender").getString("avatar");
        }
        if (!message.getJSONObject("sender").isNull("country")) {
            senderCountry = message.getJSONObject("sender").getString("country");
        }
        if (!message.getJSONObject("sender").isNull("language")) {
            senderLanduege = message.getJSONObject("sender").getString("language");
        }
        if (!message.getJSONObject("sender").isNull("api_version")) {
            senderApiVersion = message.getJSONObject("sender").getInt("api_version");
        }
        if (!message.getJSONObject("message").isNull("tracking_data")) {
            messageTrackingData = message.getJSONObject("message").getString("tracking_data");
        }

    }
    public String getMessageText() {
        return messageText;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public String getSenderCountry() {
        return senderCountry;
    }

    public String getSenderLanduege() {
        return senderLanduege;
    }

    public int getSenderApiVersion() {
        return senderApiVersion;
    }

    public String getMessageTrackingData() {
        return messageTrackingData;
    }

    public JSONObject getMessage() {
        return message;
    }
}
