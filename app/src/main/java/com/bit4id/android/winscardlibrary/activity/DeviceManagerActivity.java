package com.bit4id.android.winscardlibrary.activity;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.bit4id.android.winscardlibrary.R;
import com.bit4id.android.winscardlibrary.manager.PreferencesManager;

public class DeviceManagerActivity
        extends AppCompatActivity {
    private static final String TAG = DeviceManagerActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_manager_bluetooth_usb);

        ImageButton usbButton = (ImageButton) findViewById(R.id.usbButton);
        ImageButton bleButton = (ImageButton) findViewById(R.id.bleButton);
        if (null != usbButton) {
            usbButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent usbConfigurationItent = new Intent(DeviceManagerActivity.this, UsbConfigurationActivity.class);

                    DeviceManagerActivity.this.startActivityForResult(usbConfigurationItent, 2);
                }
            });
        }
        if (null != bleButton) {
            bleButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent bluetoothDiscoverIntent = new Intent(DeviceManagerActivity.this, BluetoothDeviceScanActivity.class);

                    DeviceManagerActivity.this.startActivityForResult(bluetoothDiscoverIntent, 1);
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int result = 0;
        PreferencesManager.resetConfiguration(this);
        if (requestCode == 1) {
            if ((-1 == resultCode) && (null != data)) {
                Log.i(TAG, "Dispositivo Bluetooth configurato");
                if (PreferencesManager.saveConfiguration(this, data)) {
                    result = -1;
                }
            }
        } else if ((requestCode == 2) &&
                (-1 == resultCode) && (null != data)) {
            Log.i(TAG, "Dispositivo USB configurato");
            if (PreferencesManager.saveConfiguration(this, data)) {
                result = -1;
            }
        }
        if (result == -1) {
            setResult(result);
            finish();
        }
    }
}
