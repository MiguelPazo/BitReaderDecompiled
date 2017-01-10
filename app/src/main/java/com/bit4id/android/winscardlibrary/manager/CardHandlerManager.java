package com.bit4id.android.winscardlibrary.manager;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import com.bit4id.android.winscardlibrary.Utils;
import com.bit4id.android.winscardlibrary.adapter.BluetoothDeviceAdapter;

import java.util.HashMap;

public class CardHandlerManager {
    private final HashMap<Integer, Object> cardHandlers = new HashMap();
    static final CardHandlerManager instance = new CardHandlerManager();

    public static CardHandlerManager getInstance() {
        return instance;
    }

    private BluetoothDeviceAdapter mBlueReader = null;
    private int mBlueReaderId = 0;

    public BluetoothDeviceAdapter getBlueReader() {
        return this.mBlueReader;
    }

    public int getBlueReaderId() {
        return this.mBlueReaderId;
    }

    public Integer add(Integer id, Object reader) {
        Integer res = Integer.valueOf(-1);
        synchronized (this.cardHandlers) {
            try {
                this.cardHandlers.put(id, reader);
                res = id;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public Integer add(Object reader) {
        Integer res = Integer.valueOf(-1);
        synchronized (this.cardHandlers) {
            try {
                Integer id;
                do {
                    id = Utils.randInt(10, 1000);
                } while (this.cardHandlers.containsKey(id));
                this.cardHandlers.put(id, reader);
                res = id;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public Integer addBluetooth(Integer id, Object reader) {
        Integer res = Integer.valueOf(-1);
        res = add(id, reader);
        if (res.intValue() >= 0) {
            this.mBlueReaderId = res.intValue();
            this.mBlueReader = ((BluetoothDeviceAdapter) reader);
        }
        return res;
    }

    public Integer addBluetooth(Object reader) {
        Integer res = Integer.valueOf(-1);
        res = add(reader);
        if (res.intValue() >= 0) {
            this.mBlueReaderId = res.intValue();
            this.mBlueReader = ((BluetoothDeviceAdapter) reader);
        }
        return res;
    }

    public Object get(Integer id, boolean remove) {
        Object res = null;
        synchronized (this.cardHandlers) {
            if (this.cardHandlers.containsKey(id)) {
                res = this.cardHandlers.get(id);
                if (remove) {
                    this.cardHandlers.remove(id);
                    if (this.mBlueReaderId == id.intValue()) {
                        this.mBlueReader = null;
                        this.mBlueReaderId = -1;
                    }
                }
            }
        }
        return res;
    }

    public Object get(Integer id) {
        return get(id, false);
    }
}
