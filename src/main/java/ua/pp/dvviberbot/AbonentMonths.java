package ua.pp.dvviberbot;

import org.json.JSONObject;

public class AbonentMonths {
    private JSONObject jsonDataLocal;
    private int cntCounters;
    AbonentMonths (String jsonData){
        jsonDataLocal = new JSONObject(jsonData);
        initParamFromJson();

    }
    private void initParamFromJson(){
        cntCounters = jsonDataLocal.getJSONArray("data").length();
        if (cntCounters > 0){
            lc = jsonDataLocal.getJSONArray("data").getJSONObject(0).getString("LC");
            fio = jsonDataLocal.getJSONArray("data").getJSONObject(0).getString("FIO");
            adres = jsonDataLocal.getJSONArray("data").getJSONObject(0).getString("ADRES");
            nlivers = jsonDataLocal.getJSONArray("data").getJSONObject(0).getInt("NLIVERS");
        }
    }
    public JSONObject getJsonData(){
        return jsonDataLocal;
    }
    public String getLc() {
        return lc;
    }

    public void setLc(String lc) {
        this.lc = lc;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public int getNlivers() {
        return nlivers;
    }

    public void setNlivers(int nlivers) {
        this.nlivers = nlivers;
    }

    private String lc;
    private String fio;
    private String adres;
    private int nlivers;


}
