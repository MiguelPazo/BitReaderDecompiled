package com.bit4id.android.winscardlibrary.manager;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.bit4id.android.winscardlibrary.listener.OnDeviceScanListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothDeviceManager {
    private final HashMap<String, String> mBluetoothDevices = new HashMap();

    class ScanBroadcastReceiver
            extends BroadcastReceiver {
        private OnDeviceScanListener mListener;

        ScanBroadcastReceiver() {
        }

        public void setListener(OnDeviceScanListener listener) {
            this.mListener = listener;
        }

        public OnDeviceScanListener getListener() {
            return this.mListener;
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.device.action.FOUND")) {
                Log.i("BROADCAST RECEIVER", "ACTION_FOUND");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (null != device.getName()) {
                    Log.i("BROADCAST RECEIVER", device.getName());
                }
                if ((null != device.getName()) && (
                        (device.getName().toUpperCase().startsWith("ACR3901U-S1-")) ||
                                (device.getName().toUpperCase().startsWith("ACR1255U-J1-")))) {
                    Log.i("BROADCAST RECEIVER", "MEMORIZZATO");
                    if (!BluetoothDeviceManager.this.mBluetoothDevices.containsKey(device.getName())) {
                        BluetoothDeviceManager.this.mBluetoothDevices.put(device.getName(), device.getAddress());
                    }
                }
                if (null != this.mListener) {
                    this.mListener.onDeviceFound(device);
                }
            } else if (intent.getAction().equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
                Log.i("BROADCAST RECEIVER", "ACTION_DISCOVERY_FINISHED");
                synchronized (BluetoothDeviceManager.this.mBluetoothDevices) {
                    BluetoothDeviceManager.this.mBluetoothDevices.notifyAll();
                }
                if (null != this.mListener) {
                    this.mListener.OnDeviceEnd();
                }
            } else if (intent.getAction().equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
                Log.i("BROADCAST RECEIVER", "ACTION_DISCOVERY_STARTED");
                BluetoothDeviceManager.this.mBluetoothDevices.clear();
                if (null != this.mListener) {
                    this.mListener.onDeviceStart();
                }
            }
        }
    }

    private ScanBroadcastReceiver mBroadcastReceiver = new ScanBroadcastReceiver();
    private static BluetoothDeviceManager instance = new BluetoothDeviceManager();

    public static BluetoothDeviceManager getInstance() {
        return instance;
    }

    private BluetoothDeviceManager() {
        Log.i("BluetoothScanService", "Constructor of BluetoothScanService");
    }

    private BluetoothAdapter getAdapter(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth");
        return bluetoothManager.getAdapter();
    }

    public ArrayList<String> getDevices(Context context) {
        ArrayList<String> res = new ArrayList();
        if (PreferencesManager.getDeviceType(context) == 1) {
            String address = PreferencesManager.getDeviceAddress(context);
            String name = PreferencesManager.getDeviceName(context);
            if ((null != address) && (null != name)) {
                if (!this.mBluetoothDevices.containsKey(name)) {
                    this.mBluetoothDevices.put(name, address);
                }
                res.add(name);
                return res;
            }
        }
        return res;
    }

    public BluetoothDevice getDevice(Context context, String name) {
        BluetoothDevice res = null;
        synchronized (this.mBluetoothDevices) {
            if (this.mBluetoothDevices.containsKey(name)) {
                res = getAdapter(context).getRemoteDevice((String) this.mBluetoothDevices.get(name));
            }
        }
        return res;
    }

    public synchronized void discoverDevices(Context context, boolean enable, OnDeviceScanListener listener) {
        if (!enable) {
            if (getAdapter(context).isDiscovering()) {
                getAdapter(context).cancelDiscovery();
                if (null != this.mBroadcastReceiver.getListener()) {
                    this.mBroadcastReceiver.getListener().OnDeviceEnd();
                }
            }
            try {
                context.unregisterReceiver(this.mBroadcastReceiver);
            } catch (IllegalArgumentException localIllegalArgumentException) {
            }
        } else if (!getAdapter(context).isDiscovering()) {
            this.mBroadcastReceiver.setListener(listener);
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.bluetooth.device.action.FOUND");
            filter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
            filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
            context.registerReceiver(this.mBroadcastReceiver, filter);
            getAdapter(context).startDiscovery();
        }
    }

    public boolean isBleEnabled(Context context) {
        return getAdapter(context).isEnabled();
    }
}
