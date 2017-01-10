package com.bit4id.android.winscardlibrary.listener;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.bluetooth.BluetoothDevice;

public abstract class OnDeviceScanListener {
    public abstract void onDeviceStart();

    public abstract void onDeviceFound(BluetoothDevice paramBluetoothDevice);

    public abstract void OnDeviceEnd();
}
