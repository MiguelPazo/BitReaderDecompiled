package com.bit4id.android.winscardlibrary.receiver;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

public class DeviceBroadcastEventReceiver
        extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        if ((intent != null) &&
                (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED")))
        {
            Parcelable usbDevice = intent.getParcelableExtra("device");

            Intent broadcastIntent = new Intent("com.bit4id.android.DEVICE_DETACHED");
            broadcastIntent.putExtra("device", usbDevice);

            context.sendBroadcast(broadcastIntent);
        }
    }
}
