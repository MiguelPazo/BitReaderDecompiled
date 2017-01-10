package com.bit4id.android.winscardlibrary.adapter;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;

import com.acs.bluetooth.Acr1255uj1Reader;
import com.acs.bluetooth.Acr1255uj1Reader.OnBatteryLevelAvailableListener;
import com.acs.bluetooth.Acr1255uj1Reader.OnBatteryLevelChangeListener;
import com.acs.bluetooth.Acr3901us1Reader;
import com.acs.bluetooth.Acr3901us1Reader.OnBatteryStatusAvailableListener;
import com.acs.bluetooth.Acr3901us1Reader.OnBatteryStatusChangeListener;
import com.acs.bluetooth.BluetoothReader;
import com.acs.bluetooth.BluetoothReader.OnAtrAvailableListener;
import com.acs.bluetooth.BluetoothReader.OnAuthenticationCompleteListener;
import com.acs.bluetooth.BluetoothReader.OnCardPowerOffCompleteListener;
import com.acs.bluetooth.BluetoothReader.OnCardStatusAvailableListener;
import com.acs.bluetooth.BluetoothReader.OnCardStatusChangeListener;
import com.acs.bluetooth.BluetoothReader.OnDeviceInfoAvailableListener;
import com.acs.bluetooth.BluetoothReader.OnEnableNotificationCompleteListener;
import com.acs.bluetooth.BluetoothReader.OnEscapeResponseAvailableListener;
import com.acs.bluetooth.BluetoothReader.OnResponseApduAvailableListener;
import com.acs.bluetooth.BluetoothReaderGattCallback;
import com.acs.bluetooth.BluetoothReaderGattCallback.OnConnectionStateChangeListener;
import com.acs.bluetooth.BluetoothReaderManager;
import com.acs.bluetooth.BluetoothReaderManager.OnReaderDetectionListener;
import com.bit4id.android.winscardlibrary.exception.BTDeviceNotFoundException;
import com.bit4id.android.winscardlibrary.manager.BluetoothDeviceManager;

import java.util.ArrayList;

public class BluetoothDeviceAdapter {
    private static final String TAG = BluetoothDeviceAdapter.class.getSimpleName();
    private static final long MAX_TIMEOUT = 10000L;
    private BluetoothDevice mDevice;
    private Context mContext;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothReader mBluetoothReader;
    private BluetoothReaderManager mBluetoothReaderManager;
    private BluetoothReaderGattCallback mGattCallback;
    private String mReaderName;
    private Boolean mAuthenticated;
    private int mReaderStatus;
    private final ArrayList<Byte> mAtrBuffer = new ArrayList();
    private final ArrayList<Byte> mApduResponseBuffer = new ArrayList();
    private final Object connectionSync = new Object();
    private final Object disconnectionSync = new Object();
    private final Object authenticationSync = new Object();

    public BluetoothDeviceAdapter(Context context, String readerName)
            throws BTDeviceNotFoundException {
        this.mContext = context;
        this.mReaderName = readerName;
        this.mDevice = BluetoothDeviceManager.getInstance().getDevice(context, readerName);
        if (null == this.mDevice) {
            throw new BTDeviceNotFoundException();
        }
        this.mAuthenticated = Boolean.valueOf(false);
        this.mReaderStatus = 0;

        this.mBluetoothReaderManager = new BluetoothReaderManager();
        this.mGattCallback = new BluetoothReaderGattCallback();

        this.mBluetoothReaderManager.setOnReaderDetectionListener(new BluetoothReaderManager.OnReaderDetectionListener() {
            public void onReaderDetection(BluetoothReader bluetoothReader) {
                if ((bluetoothReader instanceof Acr3901us1Reader)) {
                    BluetoothDeviceAdapter.this.setReader(bluetoothReader);
                    BluetoothDeviceAdapter.this.setListener();
                    bluetoothReader.enableNotification(true);
                }
            }
        });
        this.mGattCallback.setOnConnectionStateChangeListener(new BluetoothReaderGattCallback.OnConnectionStateChangeListener() {
            public void onConnectionStateChange(BluetoothGatt gatt, int state, int newState) {
                Log.i(BluetoothDeviceAdapter.TAG, "onConnectionStateChangeListener  " + state + " " + newState);
                if (state == 0) {
                    if (2 == newState) {
                        BluetoothDeviceAdapter.this.mBluetoothReaderManager.detectReader(gatt, BluetoothDeviceAdapter.this.mGattCallback);
                    } else if (0 == newState) {
                        BluetoothDeviceAdapter.this.mBluetoothReader = null;
                        if (BluetoothDeviceAdapter.this.mBluetoothGatt != null) {
                            BluetoothDeviceAdapter.this.mBluetoothGatt.close();
                            BluetoothDeviceAdapter.this.mBluetoothGatt = null;
                        }
                        Log.i(BluetoothDeviceAdapter.TAG, "DISCONNESSIONE OK");
                        BluetoothDeviceAdapter.this.notifyDisconnection();
                    }
                }
            }
        });
    }

    public void connect() {
        if (Build.VERSION.SDK_INT >= 23) {
            this.mBluetoothGatt = this.mDevice.connectGatt(this.mContext, false, this.mGattCallback, 2);
        } else {
            this.mBluetoothGatt = this.mDevice.connectGatt(this.mContext, false, this.mGattCallback);
        }
    }

    public void close() {
        Log.i(TAG, "START CLOSE");
        this.mBluetoothGatt.disconnect();
        if ((null != this.mBluetoothGatt) &&
                (!waitForDisconnection())) {
            Log.i(TAG, "CLOSE TIMEOUT");
        }
        Log.i(TAG, "END CLOSE");
    }

    public String getReaderName() {
        return this.mReaderName;
    }

    public BluetoothReader getReader() {
        return this.mBluetoothReader;
    }

    private void setReader(BluetoothReader value) {
        this.mBluetoothReader = value;
    }

    public ArrayList<Byte> getAtr() {
        return this.mAtrBuffer;
    }

    public void setAtr(byte[] value) {
        if (null == value) {
            return;
        }
        synchronized (this.mAtrBuffer) {
            this.mAtrBuffer.clear();
            for (byte b : value) {
                this.mAtrBuffer.add(Byte.valueOf(b));
            }
            this.mAtrBuffer.notify();
        }
    }

    public Integer getStatus() {
        return Integer.valueOf(this.mReaderStatus);
    }

    public void setStatus(Integer status) {
        this.mReaderStatus = status.intValue();
    }

    public ArrayList<Byte> getApduResponse() {
        return this.mApduResponseBuffer;
    }

    public void setApduResponse(byte[] value) {
        if (null == value) {
            return;
        }
        synchronized (this.mApduResponseBuffer) {
            this.mApduResponseBuffer.clear();
            for (byte b : value) {
                this.mApduResponseBuffer.add(Byte.valueOf(b));
            }
            this.mApduResponseBuffer.notify();
        }
    }

    public void setListener() {
        if ((this.mBluetoothReader instanceof Acr3901us1Reader)) {
            ((Acr3901us1Reader) this.mBluetoothReader).setOnBatteryStatusChangeListener(new Acr3901us1Reader.OnBatteryStatusChangeListener() {
                public void onBatteryStatusChange(BluetoothReader bluetoothReader, int batteryStatus) {
                    Log.i(BluetoothDeviceAdapter.TAG, "onBatteryStatusChange data: " + batteryStatus);
                }
            });
        } else if ((this.mBluetoothReader instanceof Acr1255uj1Reader)) {
            ((Acr1255uj1Reader) this.mBluetoothReader).setOnBatteryLevelChangeListener(new Acr1255uj1Reader.OnBatteryLevelChangeListener() {
                public void onBatteryLevelChange(BluetoothReader bluetoothReader, int batteryLevel) {
                    Log.i(BluetoothDeviceAdapter.TAG, "onBatteryLevelChange data: " + batteryLevel);
                }
            });
        }
        this.mBluetoothReader.setOnCardStatusChangeListener(new BluetoothReader.OnCardStatusChangeListener() {
            public void onCardStatusChange(BluetoothReader bluetoothReader, int status) {
                Log.i(BluetoothDeviceAdapter.TAG, "onCardStatusChange data: " + status);

                BluetoothDeviceAdapter.this.setStatus(Integer.valueOf(status));
            }
        });
        this.mBluetoothReader.setOnAuthenticationCompleteListener(new BluetoothReader.OnAuthenticationCompleteListener() {
            public void onAuthenticationComplete(BluetoothReader bluetoothReader, int errorCode) {
                Log.i(BluetoothDeviceAdapter.TAG, "onAuthenticationComplete data: " + errorCode);
                if (errorCode == 0) {
                    BluetoothDeviceAdapter.this.mAuthenticated = Boolean.valueOf(true);
                }
                BluetoothDeviceAdapter.this.notifyAuthentication();
            }
        });
        this.mBluetoothReader.setOnAtrAvailableListener(new BluetoothReader.OnAtrAvailableListener() {
            public void onAtrAvailable(BluetoothReader bluetoothReader, byte[] atr, int errorCode) {
                Log.i(BluetoothDeviceAdapter.TAG, "onAtrAvailable");
                BluetoothDeviceAdapter.this.setAtr(atr);
            }
        });
        this.mBluetoothReader.setOnCardPowerOffCompleteListener(new BluetoothReader.OnCardPowerOffCompleteListener() {
            public void onCardPowerOffComplete(BluetoothReader bluetoothReader, int result) {
                Log.i(BluetoothDeviceAdapter.TAG, "onCardPowerOffComplete");
            }
        });
        this.mBluetoothReader.setOnResponseApduAvailableListener(new BluetoothReader.OnResponseApduAvailableListener() {
            public void onResponseApduAvailable(BluetoothReader bluetoothReader, byte[] apdu, int errorCode) {
                Log.i(BluetoothDeviceAdapter.TAG, "onResponseApduAvailable");
                BluetoothDeviceAdapter.this.setApduResponse(apdu);
            }
        });
        this.mBluetoothReader.setOnEscapeResponseAvailableListener(new BluetoothReader.OnEscapeResponseAvailableListener() {
            public void onEscapeResponseAvailable(BluetoothReader bluetoothReader, byte[] response, int errorCode) {
                Log.i(BluetoothDeviceAdapter.TAG, "onEscapeResponseAvailable");
            }
        });
        this.mBluetoothReader.setOnDeviceInfoAvailableListener(new BluetoothReader.OnDeviceInfoAvailableListener() {
            public void onDeviceInfoAvailable(BluetoothReader bluetoothReader, int infoId, Object o, int status) {
                Log.i(BluetoothDeviceAdapter.TAG, "onDeviceInfoAvailable");
            }
        });
        if ((this.mBluetoothReader instanceof Acr1255uj1Reader)) {
            ((Acr1255uj1Reader) this.mBluetoothReader).setOnBatteryLevelAvailableListener(new Acr1255uj1Reader.OnBatteryLevelAvailableListener() {
                public void onBatteryLevelAvailable(BluetoothReader bluetoothReader, int batteryLevel, int status) {
                    Log.i(BluetoothDeviceAdapter.TAG, "onBatteryLevelAvailable");
                }
            });
        }
        if ((this.mBluetoothReader instanceof Acr3901us1Reader)) {
            ((Acr3901us1Reader) this.mBluetoothReader).setOnBatteryStatusAvailableListener(new Acr3901us1Reader.OnBatteryStatusAvailableListener() {
                public void onBatteryStatusAvailable(BluetoothReader bluetoothReader, int batteryStatus, int status) {
                    Log.i(BluetoothDeviceAdapter.TAG, "onBatteryStatusAvailable");
                }
            });
        }
        this.mBluetoothReader.setOnCardStatusAvailableListener(new BluetoothReader.OnCardStatusAvailableListener() {
            public void onCardStatusAvailable(BluetoothReader bluetoothReader, int cardStatus, int errorCode) {
                Log.i(BluetoothDeviceAdapter.TAG, "onCardStatusAvailable");
            }
        });
        this.mBluetoothReader.setOnEnableNotificationCompleteListener(new BluetoothReader.OnEnableNotificationCompleteListener() {
            public void onEnableNotificationComplete(BluetoothReader bluetoothReader, int result) {
                Log.i(BluetoothDeviceAdapter.TAG, "onEnableNotificationComplete");
                BluetoothDeviceAdapter.this.notifyConnection();
            }
        });
    }

    private void notifyConnection() {
        synchronized (this.connectionSync) {
            this.connectionSync.notify();
        }
    }

    private void notifyAuthentication() {
        synchronized (this.authenticationSync) {
            this.authenticationSync.notify();
        }
    }

    private void notifyDisconnection() {
        synchronized (this.disconnectionSync) {
            this.disconnectionSync.notify();
        }
    }

    public boolean waitForAuthentication() {
        boolean res = false;
        long start = System.nanoTime();
        synchronized (this.authenticationSync) {
            try {
                this.authenticationSync.wait(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long elapsed = System.nanoTime() - start;
        Log.i(TAG, "ELAPSED TIME: " + elapsed);
        if (elapsed <= 10000000000L) {
            res = this.mAuthenticated.booleanValue();
        }
        return res;
    }

    public boolean waitForAtr() {
        boolean res = false;
        long start = System.nanoTime();
        synchronized (this.mAtrBuffer) {
            try {
                this.mAtrBuffer.wait(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long elapsed = System.nanoTime() - start;
        Log.i(TAG, "ELAPSED TIME: " + elapsed);
        if (elapsed <= 10000000000L) {
            res = this.mAtrBuffer.size() > 0;
        }
        return res;
    }

    public boolean waitForApduResponse() {
        boolean res = false;
        long start = System.nanoTime();
        synchronized (this.mApduResponseBuffer) {
            try {
                this.mApduResponseBuffer.wait(100000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long elapsed = System.nanoTime() - start;
        Log.i(TAG, "ELAPSED TIME: " + elapsed);
        if (elapsed <= 100000000000L) {
            res = this.mApduResponseBuffer.size() > 0;
        }
        return res;
    }

    public boolean waitForConnection() {
        boolean res = false;
        long start = System.nanoTime();
        synchronized (this.connectionSync) {
            try {
                this.connectionSync.wait(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long elapsed = System.nanoTime() - start;
        Log.i(TAG, "ELAPSED TIME: " + elapsed);
        if (elapsed <= 10000000000L) {
            res = null != this.mBluetoothReader;
        }
        return res;
    }

    public boolean waitForDisconnection() {
        boolean res = false;
        long start = System.nanoTime();
        synchronized (this.disconnectionSync) {
            try {
                this.disconnectionSync.wait(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long elapsed = System.nanoTime() - start;
        Log.i(TAG, "ELAPSED TIME: " + elapsed);
        if (elapsed <= 10000000000L) {
            res = true;
        }
        return res;
    }
}
