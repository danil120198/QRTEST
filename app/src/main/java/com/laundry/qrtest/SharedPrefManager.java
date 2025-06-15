package com.laundry.qrtest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ACER-4741 on 28/11/2017.
 */

public class SharedPrefManager {
    public static final String Travel = "travel";

    public static final String Nama = "spNama";
    public static final String Telepon = "spTelepon";
    public static final String Username = "Username";
    public static final String Email = "email";
    public static final String Status = "spstatus";
    public static final String Image = "spImage";
    public static final String Foto = "spFoto";
    public static final String Adress = "spAdress";
    public static final String LATLNG = "spLatlng";

    public static final String ID_CLIENT = "spId_Client";
    public static final String INCIDENT = "INCIDENT";


    public static final String SP_SUDAH_LOGIN = "spSudahLogin";
    public static final String STATUS_ALARM = "statusalarm";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(Travel, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }


    public String getIdClient(){
        return sp.getString(ID_CLIENT, "1");
    }

    public String getNama(){
        return sp.getString(Nama, "");
    }

    public String getTelepon(){
        return sp.getString(Telepon, "");
    }

    public String getUsername(){
        return sp.getString(Username, "");
    }


    public String getEmail(){
        return sp.getString(Email, "");
    }
    public String getIncident(){
        return sp.getString(INCIDENT, "");
    }



    public String getImage(){
        return sp.getString(Image, "");
    }

    public String getFoto(){
        return sp.getString(Foto, "");
    }

    public String getAdress(){
        return sp.getString(Adress, "");
    }

    public String getLatlng(){
        return sp.getString(LATLNG, "");
    }
    public String getStatus(){
        return sp.getString(Status, "");
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }

    public Boolean getStatusAlarm(){
        return sp.getBoolean(STATUS_ALARM, true);
    }
}
