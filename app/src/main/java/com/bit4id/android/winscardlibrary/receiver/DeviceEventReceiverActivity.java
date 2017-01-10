package com.bit4id.android.winscardlibrary.receiver;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class DeviceEventReceiverActivity
        extends Activity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getIntent().getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            checkDevice(getIntent());
        }
        finish();
    }

    protected void onDestroy()
    {
        super.onDestroy();
    }

    protected void checkDevice(Intent intent)
    {
        if ((intent != null) &&
                (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")))
        {
            Parcelable usbDevice = intent.getParcelableExtra("device");

            Intent broadcastIntent = new Intent("com.bit4id.android.DEVICE_ATTACHED");
            broadcastIntent.putExtra("device", usbDevice);

            sendBroadcast(broadcastIntent);
        }
    }
}
