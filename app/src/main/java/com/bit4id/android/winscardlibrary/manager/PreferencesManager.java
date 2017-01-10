package com.bit4id.android.winscardlibrary.manager;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;

public class PreferencesManager {
    public static boolean saveConfiguration(Context context, Intent data) {
        boolean res = false;
        SharedPreferences pref = context.getSharedPreferences("deviceconfig", 0);

        SharedPreferences.Editor editor = pref.edit();
        int deviceType = data.getIntExtra("device_type", -1);
        if (1 == deviceType) {
            String deviceName = data.getStringExtra("device_name");
            String deviceAddress = data.getStringExtra("device_address");
            if ((null != deviceName) && (!deviceName.equals("")) && (null != deviceAddress) &&
                    (!deviceAddress.equals(""))) {
                editor.putInt("device_type", 1);
                editor.putString("device_name", deviceName);
                editor.putString("device_address", deviceAddress);
                res = true;
            }
        } else if (0 == deviceType) {
            String deviceName = data.getStringExtra("device_name");
            if ((null != deviceName) && (!deviceName.equals(""))) {
                editor.putInt("device_type", 0);
                editor.putString("device_name", deviceName);
                res = true;
            }
        }
        if (res) {
            editor.commit();
        }
        return res;
    }

    public static void resetConfiguration(Context context) {
        SharedPreferences pref = context.getSharedPreferences("deviceconfig", 0);

        Map<String, ?> prefs = pref.getAll();
        SharedPreferences.Editor editor = pref.edit();
        for (String prefName : prefs.keySet()) {
            editor.remove(prefName);
        }
        editor.commit();
    }

    public static boolean isConfigured(Context context) {
        return
                (-1 != getDeviceType(context)) &&
                        (null != getDeviceName(context)) && (
                        (1 != getDeviceType(context)) || (null != getDeviceAddress(context)));
    }

    public static int getDeviceType(Context context) {
        SharedPreferences pref = context.getSharedPreferences("deviceconfig", 0);

        return pref.getInt("device_type", -1);
    }

    public static String getDeviceName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("deviceconfig", 0);

        return pref.getString("device_name", null);
    }

    public static String getDeviceAddress(Context context) {
        SharedPreferences pref = context.getSharedPreferences("deviceconfig", 0);

        return pref.getString("device_address", null);
    }
}
