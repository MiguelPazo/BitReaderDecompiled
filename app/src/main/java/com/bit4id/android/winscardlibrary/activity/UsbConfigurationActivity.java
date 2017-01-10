package com.bit4id.android.winscardlibrary.activity;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acs.smartcard.Reader;
import com.bit4id.android.winscardlibrary.R;
import com.bit4id.android.winscardlibrary.Winscard;
import com.bit4id.android.winscardlibrary.parameters.SCardConnectParams;

import java.util.ArrayList;
import java.util.HashMap;

public class UsbConfigurationActivity extends AppCompatActivity {
    private final String TAG = UsbConfigurationActivity.class.getSimpleName();
    private UsbManager mUsbManager;
    private DeviceListAdapter mDeviceListAdapter;
    private ListView mListView;
    private Reader mReader;

    static class ViewHolder {
        TextView deviceName;
        TextView productName;
        ImageView deviceIcon;
    }

    private class DeviceListAdapter extends BaseAdapter {
        private ArrayList<UsbDevice> mDevices;
        private LayoutInflater mInflator;

        public DeviceListAdapter() {
            this.mDevices = new ArrayList();
            this.mInflator = UsbConfigurationActivity.this.getLayoutInflater();
        }

        public void addDevice(UsbDevice device) {
            if ((!this.mDevices.contains(device)) && (UsbConfigurationActivity.this.mReader.isSupported(device))) {
                this.mDevices.add(device);
            }
        }

        public UsbDevice getDevice(int position) {
            return (UsbDevice) this.mDevices.get(position);
        }

        public void clear() {
            this.mDevices.clear();
        }

        public int getCount() {
            return this.mDevices.size();
        }

        public Object getItem(int i) {
            return this.mDevices.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public ArrayList<UsbDevice> getmDevices() {
            return mDevices;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            UsbConfigurationActivity.ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflator.inflate(R.layout.list_item, null);
                viewHolder = new UsbConfigurationActivity.ViewHolder();
                viewHolder.deviceName = ((TextView) view.findViewById(R.id.device_name));
                viewHolder.productName = ((TextView) view.findViewById(R.id.device_address));
                viewHolder.deviceIcon = ((ImageView) view.findViewById(R.id.device_icon));
                view.setTag(viewHolder);
            } else {
                viewHolder = (UsbConfigurationActivity.ViewHolder) view.getTag();
            }
            UsbDevice device = (UsbDevice) this.mDevices.get(i);
            viewHolder.deviceName.setText(device.getDeviceName());
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.productName.setText(device.getProductName());
            } else {
                viewHolder.productName.setText("");
            }
            viewHolder.deviceIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.usb_pocket));
            return view;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Receiving broadcast");
            Log.d(TAG, "action: " + intent.getAction());

            if ("com.bit4id.android.DEVICE_ATTACHED".equals(intent.getAction())) {
                UsbDevice device = (UsbDevice) intent.getExtras().get(UsbManager.EXTRA_DEVICE);
                UsbConfigurationActivity.this.mDeviceListAdapter.addDevice(device);
                UsbConfigurationActivity.this.mDeviceListAdapter.notifyDataSetChanged();
            } else if ("com.bit4id.android.DEVICE_DETACHED".equals(intent.getAction())) {
                UsbDevice device = (UsbDevice) intent.getExtras().get(UsbManager.EXTRA_DEVICE);
//                UsbConfigurationActivity.access$100(UsbConfigurationActivity.this).mDevices.remove(device);
                UsbConfigurationActivity.this.mDeviceListAdapter.getmDevices().remove(device);
                UsbConfigurationActivity.this.mDeviceListAdapter.notifyDataSetChanged();
            } else if ("com.bit4id.android.USB_PERMISSION".equals(intent.getAction())) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if ((null != device) && (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("device_name", device.getDeviceName());

                    Log.d(TAG, "device name: " + device.getDeviceName());

//                    try {
//                        Winscard winscard = new Winscard(getApplicationContext(), Winscard.DEVICE_TYPE_USB);
//
//                        SCardConnectParams sCardConnectParams = new SCardConnectParams();
//                        sCardConnectParams.sethContext(getApplicationContext());
//                        sCardConnectParams.setSzReader(device.getDeviceName());
//                        sCardConnectParams.setDwPreferredProtocols(2L);
//
//                        long response = winscard.SCardConnect(sCardConnectParams);
//                        Log.d("-->long", String.valueOf(response));
//
//                    } catch (Winscard.IllegalDeviceType illegalDeviceType) {
//                        illegalDeviceType.printStackTrace();
//                    }
                    UsbConfigurationActivity.this.setResult(-1, resultIntent);
                } else {
                    Log.d(UsbConfigurationActivity.this.TAG, "permission denied for device " + device);
                    UsbConfigurationActivity.this.setResult(0);
                }
                UsbConfigurationActivity.this.finish();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_configuration);

        this.mListView = ((ListView) findViewById(R.id.listViewDevice));
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsbDevice device = UsbConfigurationActivity.this.mDeviceListAdapter.getDevice(position);
                if (device == null) {
                    return;
                }
                if (!UsbConfigurationActivity.this.mUsbManager.hasPermission(device)) {
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(UsbConfigurationActivity.this, 0, new Intent("com.bit4id.android.USB_PERMISSION"), 0);

                    UsbConfigurationActivity.this.mUsbManager.requestPermission(device, permissionIntent);
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("device_type", 0);
                    resultIntent.putExtra("device_name", device.getDeviceName());
                    UsbConfigurationActivity.this.setResult(-1, resultIntent);
                    UsbConfigurationActivity.this.finish();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();

        this.mDeviceListAdapter = new DeviceListAdapter();
        this.mListView.setAdapter(this.mDeviceListAdapter);

        this.mUsbManager = ((UsbManager) getSystemService(Context.USB_SERVICE));
        this.mReader = new Reader(this.mUsbManager);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.bit4id.android.DEVICE_ATTACHED");
        filter.addAction("com.bit4id.android.DEVICE_DETACHED");
        filter.addAction("com.bit4id.android.USB_PERMISSION");
        registerReceiver(this.mBroadcastReceiver, filter);

        HashMap<String, UsbDevice> devices = this.mUsbManager.getDeviceList();
        for (String device : devices.keySet()) {
            this.mDeviceListAdapter.addDevice((UsbDevice) devices.get(device));
        }
        this.mDeviceListAdapter.notifyDataSetChanged();
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mBroadcastReceiver);
    }
}
