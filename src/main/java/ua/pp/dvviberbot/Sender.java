package ua.pp.dvviberbot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sender {
    public Sender(String cName, String id) {
        this.cName = cName;
        this.id = id;
    }
    public Sender(String cName, String id, String avatar, String country, String language, int api_version) {
        this(cName, id);
        setAvatar(avatar);
        setCountry(country);
        setLanguage(language);
        setApi_version(api_version);

    }
    public Sender (ViberMessage viberMsg){
        this(viberMsg.getSenderName(),viberMsg.getSenderId(),viberMsg.getSenderAvatar(),viberMsg.getSenderCountry(),viberMsg.getSenderLanduege(),viberMsg.getSenderApiVersion());
    }
    public String getcName() {
        return cName;
    }

    public String getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCountry() {
        return country;

    }

    public String getLanguage() {
        return language;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setApi_version(int api_version) {
        this.api_version = api_version;
    }

    public int getApi_version() {
        return api_version;

    }
    public void insertIntoDB() {
        String SQL = "INSERT INTO senders(id,name,avatar,country,language,\tapi_version) "
                + "VALUES(?,?,?,?,?,?) ON CONFLICT (id) " +
                "DO NOTHING";
        DbConection dbConection = new DbConection();
        Connection conn = dbConection.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, this.getId());
            pstmt.setString(2, this.getcName());
            pstmt.setString(3, this.getAvatar());
            pstmt.setString(4, this.getCountry());
            pstmt.setString(5, this.getLanguage());
            pstmt.setInt(6, this.getApi_version());

            int affectedRows = pstmt.executeUpdate();
            pstmt.close();
            conn.close();

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            if (!(conn == null)){
                try {
                    conn.close();
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        }


    }
    public JSONObject getAccounts(String uslName){
        String SQL = "SELECT account from sendersservicesaccounts where senderid=? and \"cName\"=?";
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        DbConection dbConection = new DbConection();
        Connection conn = dbConection.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, this.getId());
            pstmt.setString(2, uslName);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                jsonArray.put(rs.getString(1));
            }
            json.put("Accounts",jsonArray);
            rs.close();
            pstmt.close();
            conn.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            if (!(conn == null)){
                try {
                    conn.close();
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        }
        return json;
    }
    public void addAccount(String uslName,String account){
        String SQL = "select * from addsenderaccount(?,?,?)";
        DbConection dbConection = new DbConection();
        Connection conn = dbConection.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, this.getId());
            pstmt.setString(2, account);
            pstmt.setString(3, uslName);

            ResultSet rs = pstmt.executeQuery();
            rs.close();
            pstmt.close();
            conn.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            if (!(conn == null)){
                try {
                    conn.close();
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private String cName;
    private String id;
    private String avatar;
    private String country;
    private String language;
    private int api_version;
}
