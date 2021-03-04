package com.pegasusgroup.riarewards.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Kalpen Vaghela on : 28-01-2020.
 */
public class SharedPreference {

    private SharedPreferences sp;
    private String PREF_NAME = "RiaApp";
    private Context con;

    public SharedPreference(Context con) {
        this.con = con;
        sp = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Context getContext() {
        return con;
    }

    public void setValueString(String paramName, String paramValue) {
        Editor et = sp.edit();
        et.putString(paramName, paramValue);
        et.commit();
    }

    public String getValueString(String paramName) {
        return sp.getString(paramName, "");
    }

    public void setValueInt(String paramName, int paramValue) {
        Editor et = sp.edit();
        et.putInt(paramName, paramValue);
        et.commit();
    }

    public int getValueInt(String paramName) {
        return sp.getInt(paramName, 0);
    }

    public void setValueBool(String paramName, boolean paramValue) {
        Editor et = sp.edit();
        et.putBoolean(paramName, paramValue);
        et.commit();
    }

    public boolean getValueBoolean(String paramName) {
        return sp.getBoolean(paramName, false);
    }

    public void setValueDouble(String paramName, Double paramValue) {
        Editor et = sp.edit();
        et.putLong(paramName, Double.doubleToRawLongBits(paramValue));
        et.commit();

    }

    public Double getValueDouble(String paramName) {

        return Double.longBitsToDouble(sp.getLong(paramName, Double.doubleToLongBits(0)));
    }

    public void setValueLong(String paramName, long paramValue) {
        Editor et = sp.edit();
        et.putLong(paramName, paramValue);
        et.commit();

    }

    public long getValueLong(String paramName) {

        return (sp.getLong(paramName, Double.doubleToLongBits(0)));
    }


    public void clearAll() {
        sp.edit().clear().commit();
    }
}
