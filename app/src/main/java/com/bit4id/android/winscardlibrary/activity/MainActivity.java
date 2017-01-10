package com.bit4id.android.winscardlibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bit4id.android.winscardlibrary.Constant;
import com.bit4id.android.winscardlibrary.R;
import com.bit4id.android.winscardlibrary.Winscard;
import com.bit4id.android.winscardlibrary.parameters.SCardConnectParams;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 07/01/2017.
 */

public class MainActivity extends Activity {

    private Winscard winscard;
    private String deviceName = "/dev/bus/usb/001/002";
    private final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try {
            winscard = new Winscard(getApplicationContext(), Winscard.DEVICE_TYPE_USB);
        } catch (Winscard.IllegalDeviceType illegalDeviceType) {
            illegalDeviceType.printStackTrace();
        }
    }

    public void handleSelect(View view) {
        Intent deviceManagerIntent = new Intent(
                MainActivity.this, DeviceManagerActivity.class);
        startActivityForResult(deviceManagerIntent, Constant.CONFIGURATION_REQUEST_CODE);
    }

    public void handleConnect(View view) {
        Log.d(TAG, "connecting with device: " + deviceName);
        SCardConnectParams params = new SCardConnectParams();

        params.sethContext(getApplicationContext());
        params.setSzReader(deviceName);
        params.setDwPreferredProtocols(1L);

        long response = winscard.SCardConnect(params);
        byte[] blong = longToBytes(response);


        Log.e("response long", String.valueOf(response));
        Log.e("response", "code: " + bytesToHex(blong));
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode: " + String.valueOf(requestCode));

        switch (requestCode) {
            case Constant.CONFIGURATION_REQUEST_CODE:
                break;
            default:
                break;
        }
    }
}
