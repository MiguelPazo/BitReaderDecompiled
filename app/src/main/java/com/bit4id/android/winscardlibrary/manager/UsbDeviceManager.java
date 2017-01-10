package com.bit4id.android.winscardlibrary.manager;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.content.Context;

import java.util.ArrayList;

public class UsbDeviceManager {
    private static UsbDeviceManager instance = new UsbDeviceManager();

    public static UsbDeviceManager getInstance() {
        return instance;
    }

    public ArrayList<String> getDevices(Context context) {
        ArrayList<String> res = new ArrayList();
        if (PreferencesManager.getDeviceType(context) == 0) {
            String name = PreferencesManager.getDeviceName(context);
            if (null != name) {
                res.add(name);
                return res;
            }
        }
        return res;
    }
}
