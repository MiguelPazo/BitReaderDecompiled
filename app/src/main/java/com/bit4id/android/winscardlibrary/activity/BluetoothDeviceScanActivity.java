package com.bit4id.android.winscardlibrary.activity;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bit4id.android.winscardlibrary.R;
import com.bit4id.android.winscardlibrary.listener.OnDeviceScanListener;
import com.bit4id.android.winscardlibrary.manager.BluetoothDeviceManager;

import java.util.ArrayList;

public class BluetoothDeviceScanActivity extends ActionBarActivity {
    private final String TAG = BluetoothDeviceScanActivity.class.getName();
    private static boolean isDiscovering = false;
    private DeviceListAdapter mDeviceListAdapter;
    private ListView listView;

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        ImageView deviceIcon;
    }

    private class DeviceListAdapter
            extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public DeviceListAdapter() {
            this.mLeDevices = new ArrayList();
            this.mInflator = BluetoothDeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if ((!this.mLeDevices.contains(device)) && (device.getName() != null) && (BluetoothDeviceScanActivity.this.isQualifiedDevice(device.getName()))) {
                this.mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return (BluetoothDevice) this.mLeDevices.get(position);
        }

        public void clear() {
            this.mLeDevices.clear();
        }

        public int getCount() {
            return this.mLeDevices.size();
        }

        public Object getItem(int i) {
            return this.mLeDevices.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            BluetoothDeviceScanActivity.ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflator.inflate(R.layout.list_item, null);
                viewHolder = new BluetoothDeviceScanActivity.ViewHolder();
                viewHolder.deviceAddress = ((TextView) view.findViewById(R.id.device_address));
                viewHolder.deviceName = ((TextView) view.findViewById(R.id.device_name));
                viewHolder.deviceIcon = ((ImageView) view.findViewById(R.id.device_icon));
                view.setTag(viewHolder);
            } else {
                viewHolder = (BluetoothDeviceScanActivity.ViewHolder) view.getTag();
            }
            BluetoothDevice device = (BluetoothDevice) this.mLeDevices.get(i);
            String deviceName = device.getName();
            if ((deviceName != null) && (deviceName.length() > 0)) {
                viewHolder.deviceName.setText(BluetoothDeviceScanActivity.this.getBit4IDName(deviceName));
                viewHolder.deviceIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.minilector));
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.bt));
//                viewHolder.deviceAddress.setTextColor(-16777216);
//                viewHolder.deviceName.setTextColor(-16777216);
                viewHolder.deviceAddress.setTextColor(Color.parseColor("#FFFFFF"));
                viewHolder.deviceName.setTextColor(Color.parseColor("#FFFFFF"));
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bledevice_scan);

        this.listView = ((ListView) findViewById(R.id.listViewDevice));
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BluetoothDevice device = BluetoothDeviceScanActivity.this.mDeviceListAdapter.getDevice(position);
                if (device == null) {
                    return;
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("device_type", 1);
                resultIntent.putExtra("device_name", device.getName());
                resultIntent.putExtra("device_address", device.getAddress());
                BluetoothDeviceScanActivity.this.setResult(-1, resultIntent);
                BluetoothDeviceScanActivity.this.finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bledevice_scan, menu);
        menu.findItem(R.id.action_settings).setVisible(true);
        if (!isDiscovering) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_scan == item.getItemId()) {
            this.mDeviceListAdapter.clear();
            scan();
        } else if (R.id.menu_stop == item.getItemId()) {
            scan(false);
        } else if (R.id.action_settings == item.getItemId()) {
            setResult(0);
            finish();
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            switch (keyCode) {
                case 4:
                    moveTaskToBack(false);
                    setResult(0);
                    finish();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onPause() {
        super.onPause();
        scan(false);
        this.mDeviceListAdapter.clear();
    }

    public void onResume() {
        super.onResume();

        boolean location = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.access_location_title);
                builder.setMessage(R.string.access_location_message);
//                builder.setPositiveButton(17039370, null);
                builder.setPositiveButton("setPositiveButton", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        ActivityCompat.requestPermissions(BluetoothDeviceScanActivity.this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 1);
                    }
                });
                builder.show();
                location = true;
            }
        }
        this.mDeviceListAdapter = new DeviceListAdapter();
        this.listView.setAdapter(this.mDeviceListAdapter);
        if (!location) {
            if (!BluetoothDeviceManager.getInstance().isBleEnabled(this)) {
                Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");

                startActivityForResult(enableBtIntent, 1);
            } else {
                scan();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == 0) {
                    Log.d(this.TAG, "coarse location permission granted");
                } else {
                    setResult(0);
                    finish();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 0) {
                setResult(0);
                finish();
            } else {
                scan();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void finish() {
        scan(false);
        super.finish();
    }

    private void scan() {
        scan(true);
    }

    private synchronized void scan(boolean enable) {
        BluetoothDeviceManager.getInstance().discoverDevices(this, enable, new OnDeviceScanListener() {
            public void onDeviceStart() {
//                BluetoothDeviceScanActivity.access$302(true);
                BluetoothDeviceScanActivity.this.scan(true);
                BluetoothDeviceScanActivity.this.supportInvalidateOptionsMenu();
            }

            public void onDeviceFound(BluetoothDevice device) {
                if ((device != null) && (BluetoothDeviceScanActivity.this.mDeviceListAdapter != null)) {
                    BluetoothDeviceScanActivity.this.mDeviceListAdapter.addDevice(device);
                    BluetoothDeviceScanActivity.this.mDeviceListAdapter.notifyDataSetChanged();
                }
            }

            public void OnDeviceEnd() {
                BluetoothDeviceScanActivity.this.scan(false);
                BluetoothDeviceScanActivity.this.supportInvalidateOptionsMenu();
            }
        });
    }

    private boolean isQualifiedDevice(String name) {
        return name.contains(getString(R.string.acs_ble_item_suffix));
    }

    private String getBit4IDName(String name) {
        if (isQualifiedDevice(name)) {
            return name.replace(getString(R.string.acs_ble_item_suffix),
                    getString(R.string.bit4id_ble_item_suffix));
        }
        return name;
    }
}
