package com.bit4id.android.winscardlibrary;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.acs.bluetooth.BluetoothReader;
import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.acs.smartcard.ReaderException;
import com.bit4id.android.winscardlibrary.adapter.BluetoothDeviceAdapter;
import com.bit4id.android.winscardlibrary.adapter.UsbDeviceAdapter;
import com.bit4id.android.winscardlibrary.exception.BTDeviceNotFoundException;
import com.bit4id.android.winscardlibrary.manager.BluetoothDeviceManager;
import com.bit4id.android.winscardlibrary.manager.CardHandlerManager;
import com.bit4id.android.winscardlibrary.manager.UsbDeviceManager;
import com.bit4id.android.winscardlibrary.parameters.SCardConnectParams;
import com.bit4id.android.winscardlibrary.parameters.SCardDisconnectParams;
import com.bit4id.android.winscardlibrary.parameters.SCardGetAttribParams;
import com.bit4id.android.winscardlibrary.parameters.SCardGetStatusChangeParams;
import com.bit4id.android.winscardlibrary.parameters.SCardListReadersParams;
import com.bit4id.android.winscardlibrary.parameters.SCardReaderState;
import com.bit4id.android.winscardlibrary.parameters.SCardReconnectParams;
import com.bit4id.android.winscardlibrary.parameters.SCardStatusParams;
import com.bit4id.android.winscardlibrary.parameters.SCardTransmitParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Winscard {
    private static Long SCARD_ATTR_VALUE(Long Class, Integer Tag) {
        return Long.valueOf(Class.longValue() << 16 | Tag.intValue());
    }

    private static String TAG = "Winscard";

    public static Long ERROR_NOT_SUPPORTED = Long.valueOf(80L);
    public static Long SCARD_S_SUCCESS = Long.valueOf(0L);
    public static Long SCARD_F_INTERNAL_ERROR = Long.valueOf(2148532225L);
    public static Long SCARD_E_CANCELLED = Long.valueOf(2148532226L);
    public static Long SCARD_E_INVALID_HANDLE = Long.valueOf(2148532227L);
    public static Long SCARD_E_INVALID_PARAMETER = Long.valueOf(2148532228L);
    public static Long SCARD_E_INVALID_TARGET = Long.valueOf(2148532229L);
    public static Long SCARD_E_NO_MEMORY = Long.valueOf(2148532230L);
    public static Long SCARD_F_WAITED_TOO_LONG = Long.valueOf(2148532231L);
    public static Long SCARD_E_INSUFFICIENT_BUFFER = Long.valueOf(2148532232L);
    public static Long SCARD_E_UNKNOWN_READER = Long.valueOf(2148532233L);
    public static Long SCARD_E_TIMEOUT = Long.valueOf(2148532234L);
    public static Long SCARD_E_SHARING_VIOLATION = Long.valueOf(2148532235L);
    public static Long SCARD_E_NO_SMARTCARD = Long.valueOf(2148532236L);
    public static Long SCARD_E_UNKNOWN_CARD = Long.valueOf(2148532237L);
    public static Long SCARD_E_CANT_DISPOSE = Long.valueOf(2148532238L);
    public static Long SCARD_E_PROTO_MISMATCH = Long.valueOf(2148532239L);
    public static Long SCARD_E_NOT_READY = Long.valueOf(2148532240L);
    public static Long SCARD_E_INVALID_VALUE = Long.valueOf(2148532241L);
    public static Long SCARD_E_SYSTEM_CANCELLED = Long.valueOf(2148532242L);
    public static Long SCARD_F_COMM_ERROR = Long.valueOf(2148532243L);
    public static Long SCARD_F_UNKNOWN_ERROR = Long.valueOf(2148532244L);
    public static Long SCARD_E_INVALID_ATR = Long.valueOf(2148532245L);
    public static Long SCARD_E_NOT_TRANSACTED = Long.valueOf(2148532246L);
    public static Long SCARD_E_READER_UNAVAILABLE = Long.valueOf(2148532247L);
    public static Long SCARD_E_PCI_TOO_SMALL = Long.valueOf(2148532249L);
    public static Long SCARD_E_READER_UNSUPPORTED = Long.valueOf(2148532250L);
    public static Long SCARD_E_DUPLICATE_READER = Long.valueOf(2148532251L);
    public static Long SCARD_E_CARD_UNSUPPORTED = Long.valueOf(2148532252L);
    public static Long SCARD_E_NO_SERVICE = Long.valueOf(2148532253L);
    public static Long SCARD_E_SERVICE_STOPPED = Long.valueOf(2148532254L);
    public static Long SCARD_E_UNEXPECTED = Long.valueOf(2148532255L);
    public static Long SCARD_E_ICC_INSTALLATION = Long.valueOf(2148532256L);
    public static Long SCARD_E_ICC_CREATEORDER = Long.valueOf(2148532257L);
    public static Long SCARD_E_DIR_NOT_FOUND = Long.valueOf(2148532259L);
    public static Long SCARD_E_FILE_NOT_FOUND = Long.valueOf(2148532260L);
    public static Long SCARD_E_NO_DIR = Long.valueOf(2148532261L);
    public static Long SCARD_E_NO_FILE = Long.valueOf(2148532262L);
    public static Long SCARD_E_NO_ACCESS = Long.valueOf(2148532263L);
    public static Long SCARD_E_WRITE_TOO_MANY = Long.valueOf(2148532264L);
    public static Long SCARD_E_BAD_SEEK = Long.valueOf(2148532265L);
    public static Long SCARD_E_INVALID_CHV = Long.valueOf(2148532266L);
    public static Long SCARD_E_UNKNOWN_RES_MNG = Long.valueOf(2148532267L);
    public static Long SCARD_E_NO_SUCH_CERTIFICATE = Long.valueOf(2148532268L);
    public static Long SCARD_E_CERTIFICATE_UNAVAILABLE = Long.valueOf(2148532269L);
    public static Long SCARD_E_NO_READERS_AVAILABLE = Long.valueOf(2148532270L);
    public static Long SCARD_E_COMM_DATA_LOST = Long.valueOf(2148532271L);
    public static Long SCARD_E_NO_KEY_CONTAINER = Long.valueOf(2148532272L);
    public static Long SCARD_E_SERVER_TOO_BUSY = Long.valueOf(2148532273L);
    public static Long SCARD_W_UNSUPPORTED_CARD = Long.valueOf(2148532325L);
    public static Long SCARD_W_UNRESPONSIVE_CARD = Long.valueOf(2148532326L);
    public static Long SCARD_W_UNPOWERED_CARD = Long.valueOf(2148532327L);
    public static Long SCARD_W_RESET_CARD = Long.valueOf(2148532328L);
    public static Long SCARD_W_REMOVED_CARD = Long.valueOf(2148532329L);
    public static Long SCARD_W_SECURITY_VIOLATION = Long.valueOf(2148532330L);
    public static Long SCARD_W_WRONG_CHV = Long.valueOf(2148532331L);
    public static Long SCARD_W_CHV_BLOCKED = Long.valueOf(2148532332L);
    public static Long SCARD_W_EOF = Long.valueOf(2148532333L);
    public static Long SCARD_W_CANCELLED_BY_USER = Long.valueOf(2148532334L);
    public static Long SCARD_W_CARD_NOT_AUTHENTICATED = Long.valueOf(2148532335L);
    public static Long SCARD_AUTOALLOCATE = Long.valueOf(-1L);
    public static Long SCARD_SCOPE_USER = Long.valueOf(0L);
    public static Long SCARD_SCOPE_TERMINAL = Long.valueOf(1L);
    public static Long SCARD_SCOPE_SYSTEM = Long.valueOf(2L);
    public static Long SCARD_PROTOCOL_UNDEFINED = Long.valueOf(0L);
    public static Long SCARD_PROTOCOL_UNSET = SCARD_PROTOCOL_UNDEFINED;
    public static Long SCARD_PROTOCOL_T0 = Long.valueOf(1L);
    public static Long SCARD_PROTOCOL_T1 = Long.valueOf(2L);
    public static Long SCARD_PROTOCOL_RAW = Long.valueOf(4L);
    public static Long SCARD_PROTOCOL_T15 = Long.valueOf(8L);
    public static Long SCARD_PROTOCOL_ANY = Long.valueOf(SCARD_PROTOCOL_T0.longValue() | SCARD_PROTOCOL_T1.longValue());
    public static Long SCARD_SHARE_EXCLUSIVE = Long.valueOf(1L);
    public static Long SCARD_SHARE_SHARED = Long.valueOf(2L);
    public static Long SCARD_SHARE_DIRECT = Long.valueOf(3L);
    public static Long SCARD_LEAVE_CARD = Long.valueOf(0L);
    public static Long SCARD_RESET_CARD = Long.valueOf(1L);
    public static Long SCARD_UNPOWER_CARD = Long.valueOf(2L);
    public static Long SCARD_EJECT_CARD = Long.valueOf(3L);
    public static Long SCARD_STATE_UNAWARE = Long.valueOf(0L);
    public static Long SCARD_STATE_IGNORE = Long.valueOf(1L);
    public static Long SCARD_STATE_CHANGED = Long.valueOf(2L);
    public static Long SCARD_STATE_UNKNOWN = Long.valueOf(4L);
    public static Long SCARD_STATE_UNAVAILABLE = Long.valueOf(8L);
    public static Long SCARD_STATE_EMPTY = Long.valueOf(16L);
    public static Long SCARD_STATE_PRESENT = Long.valueOf(32L);
    public static Long SCARD_STATE_ATRMATCH = Long.valueOf(64L);
    public static Long SCARD_STATE_EXCLUSIVE = Long.valueOf(128L);
    public static Long SCARD_STATE_INUSE = Long.valueOf(256L);
    public static Long SCARD_STATE_MUTE = Long.valueOf(512L);
    public static Long SCARD_STATE_UNPOWERED = Long.valueOf(1024L);
    public static Long SCARD_W_INSERTED_CARD = Long.valueOf(2148532330L);
    public static Long SCARD_E_UNSUPPORTED_FEATURE = Long.valueOf(2148532255L);
    public static Long MAX_READERNAME = Long.valueOf(100L);
    public static final Long SCARD_CLASS_VENDOR_INFO = Long.valueOf(1L);
    public static final Long SCARD_CLASS_COMMUNICATIONS = Long.valueOf(2L);
    public static final Long SCARD_CLASS_PROTOCOL = Long.valueOf(3L);
    public static final Long SCARD_CLASS_POWER_MGMT = Long.valueOf(4L);
    public static final Long SCARD_CLASS_SECURITY = Long.valueOf(5L);
    public static final Long SCARD_CLASS_MECHANICAL = Long.valueOf(6L);
    public static final Long SCARD_CLASS_VENDOR_DEFINED = Long.valueOf(7L);
    public static final Long SCARD_CLASS_IFD_PROTOCOL = Long.valueOf(8L);
    public static final Long SCARD_CLASS_ICC_STATE = Long.valueOf(9L);
    public static final Long SCARD_CLASS_SYSTEM = Long.valueOf(32767L);
    public static final Long SCARD_ATTR_VENDOR_NAME = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, Integer.valueOf(256));
    public static final Long SCARD_ATTR_VENDOR_IFD_TYPE = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, Integer.valueOf(257));
    public static final Long SCARD_ATTR_VENDOR_IFD_VERSION = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, Integer.valueOf(258));
    public static final Long SCARD_ATTR_VENDOR_IFD_SERIAL_NO = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, Integer.valueOf(259));
    public static final Long SCARD_ATTR_CHANNEL_ID = SCARD_ATTR_VALUE(SCARD_CLASS_COMMUNICATIONS, Integer.valueOf(272));
    public static final Long SCARD_ATTR_ASYNC_PROTOCOL_TYPES = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(288));
    public static final Long SCARD_ATTR_DEFAULT_CLK = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(289));
    public static final Long SCARD_ATTR_MAX_CLK = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(290));
    public static final Long SCARD_ATTR_DEFAULT_DATA_RATE = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(291));
    public static final Long SCARD_ATTR_MAX_DATA_RATE = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(292));
    public static final Long SCARD_ATTR_MAX_IFSD = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(293));
    public static final Long SCARD_ATTR_SYNC_PROTOCOL_TYPES = SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, Integer.valueOf(294));
    public static final Long SCARD_ATTR_POWER_MGMT_SUPPORT = SCARD_ATTR_VALUE(SCARD_CLASS_POWER_MGMT, Integer.valueOf(305));
    public static final Long SCARD_ATTR_USER_TO_CARD_AUTH_DEVICE = SCARD_ATTR_VALUE(SCARD_CLASS_SECURITY, Integer.valueOf(320));
    public static final Long SCARD_ATTR_USER_AUTH_INPUT_DEVICE = SCARD_ATTR_VALUE(SCARD_CLASS_SECURITY, Integer.valueOf(322));
    public static final Long SCARD_ATTR_CHARACTERISTICS = SCARD_ATTR_VALUE(SCARD_CLASS_MECHANICAL, Integer.valueOf(336));
    public static final Long SCARD_ATTR_CURRENT_PROTOCOL_TYPE = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(513));
    public static final Long SCARD_ATTR_CURRENT_CLK = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(514));
    public static final Long SCARD_ATTR_CURRENT_F = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(515));
    public static final Long SCARD_ATTR_CURRENT_D = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(516));
    public static final Long SCARD_ATTR_CURRENT_N = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(517));
    public static final Long SCARD_ATTR_CURRENT_W = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(518));
    public static final Long SCARD_ATTR_CURRENT_IFSC = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(519));
    public static final Long SCARD_ATTR_CURRENT_IFSD = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(520));
    public static final Long SCARD_ATTR_CURRENT_BWT = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(521));
    public static final Long SCARD_ATTR_CURRENT_CWT = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(522));
    public static final Long SCARD_ATTR_CURRENT_EBC_ENCODING = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(523));
    public static final Long SCARD_ATTR_EXTENDED_BWT = SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, Integer.valueOf(524));
    public static final Long SCARD_ATTR_ICC_PRESENCE = SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, Integer.valueOf(768));
    public static final Long SCARD_ATTR_ICC_INTERFACE_STATUS = SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, Integer.valueOf(769));
    public static final Long SCARD_ATTR_CURRENT_IO_STATE = SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, Integer.valueOf(770));
    public static final Long SCARD_ATTR_ATR_STRING = SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, Integer.valueOf(771));
    public static final Long SCARD_ATTR_ICC_TYPE_PER_ATR = SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, Integer.valueOf(772));
    public static final Long SCARD_ATTR_ESC_RESET = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_DEFINED, Integer.valueOf(40960));
    public static final Long SCARD_ATTR_ESC_CANCEL = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_DEFINED, Integer.valueOf(40963));
    public static final Long SCARD_ATTR_ESC_AUTHREQUEST = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_DEFINED, Integer.valueOf(40965));
    public static final Long SCARD_ATTR_MAXINPUT = SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_DEFINED, Integer.valueOf(40967));
    public static final Long SCARD_ATTR_DEVICE_UNIT = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(1));
    public static final Long SSCARD_ATTR_DEVICE_IN_USE = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(2));
    public static final Long SSCARD_ATTR_DEVICE_FRIENDLY_NAME_A = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(3));
    public static final Long SSCARD_ATTR_DEVICE_SYSTEM_NAME_A = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(4));
    public static final Long SSCARD_ATTR_DEVICE_FRIENDLY_NAME_W = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(5));
    public static final Long SSCARD_ATTR_DEVICE_SYSTEM_NAME_W = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(6));
    public static final Long SCARD_ATTR_SUPRESS_T1_IFS_REQUEST = SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, Integer.valueOf(7));
    public static final Long SCARD_ATTR_DEVICE_FRIENDLY_NAME = SSCARD_ATTR_DEVICE_FRIENDLY_NAME_W;
    public static final Long SCARD_ATTR_DEVICE_SYSTEM_NAME = SSCARD_ATTR_DEVICE_SYSTEM_NAME_W;
    public static final int DEVICE_TYPE_USB = 1;
    public static final int DEVICE_TYPE_BLUETOOTH = 2;
    private static final String DEFAULT_3901_MASTER_KEY = "FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF";
    private Context context;
    private int supportedDeviceType;

    public Winscard(Context context, int deviceType)
            throws Winscard.IllegalDeviceType {
        Log.i("Winscard", "Constructor");
        this.context = context;
        this.supportedDeviceType = deviceType;
        if ((0 == (deviceType & 0x2)) && (0 == (deviceType & 0x1))) {
            throw new IllegalDeviceType();
        }
    }

    public long SCardListReaders(SCardListReadersParams params) {
        Log.i("Winscard", "SCardListReaders");
        params.clearReaders();
        long res = SCARD_E_NO_READERS_AVAILABLE.longValue();
        if (1 == (this.supportedDeviceType & 0x1)) {
            params.addReaders(UsbDeviceManager.getInstance().getDevices(this.context));
        }
        if (2 == (this.supportedDeviceType & 0x2)) {
            params.addReaders(BluetoothDeviceManager.getInstance().getDevices(this.context));
        }
        if (params.numReaders() > 0) {
            res = SCARD_S_SUCCESS.longValue();
        }
        return res;
    }

    public long SCardConnect(SCardConnectParams params) {
        Log.i("Winscard", "SCardConnect");

        if (null != BluetoothDeviceManager.getInstance().getDevice(this.context, params.getSzReader())) {
            Log.i("Winscard", "SCardConnect Bluetooth");
            return BlueSCardConnect(params, null);
        }

        Log.i("Winscard", "SCardConnect USB");
        return UsbSCardConnect(params, null);
    }

    public long SCardDisconnect(SCardDisconnectParams params) {
        Log.i("Winscard", "SCardDisconnect");
        long res = SCARD_E_UNEXPECTED.longValue();
        Object reader = CardHandlerManager.getInstance().get(Integer.valueOf(params.gethCard()), true);
        if (null != reader) {
            if (UsbDeviceAdapter.class.isInstance(reader)) {
                ((UsbDeviceAdapter) reader).getReader().close();
                if (!((UsbDeviceAdapter) reader).getReader().isOpened()) {
                    res = SCARD_S_SUCCESS.longValue();
                }
            } else if (BluetoothDeviceAdapter.class.isInstance(reader)) {
                BluetoothDeviceAdapter blueReader = (BluetoothDeviceAdapter) reader;
                blueReader.close();
                res = SCARD_S_SUCCESS.longValue();
            }
        } else {
            res = SCARD_E_INVALID_HANDLE.longValue();
        }
        return res;
    }

    public long SCardReconnect(SCardReconnectParams params) {
        Log.i("Winscard", "SCardReconnect");

        long res = SCARD_E_INVALID_HANDLE.longValue();
        Object reader;
        if (null == (reader = CardHandlerManager.getInstance().get(Integer.valueOf(params.gethCard())))) {
            return res;
        }
        try {
            if (UsbDeviceAdapter.class.isInstance(reader)) {
                String readerName = ((UsbDeviceAdapter) reader).getReaderName();
                SCardDisconnect(new SCardDisconnectParams(params.gethCard(), 0L));

                SCardConnectParams connParams = new SCardConnectParams(null, readerName, params.getDwShareMode(), params.getDwPreferredProtocols());
                res = UsbSCardConnect(connParams, Integer.valueOf(params.gethCard()));
                if (0L == res) {
                    params.setPdwActiveProtocol(connParams.getPdwActiveProtocol());
                }
            } else if (BluetoothDeviceAdapter.class.isInstance(reader)) {
                BluetoothDeviceAdapter blueReader = (BluetoothDeviceAdapter) reader;
                SCardDisconnect(new SCardDisconnectParams(params.gethCard(), 0L));

                SCardConnectParams connParams = new SCardConnectParams(null, blueReader.getReaderName(), params.getDwShareMode(), params.getDwPreferredProtocols());
                res = BlueSCardConnect(connParams, Integer.valueOf(params.gethCard()));
                if (0L == res) {
                    params.setPdwActiveProtocol(connParams.getPdwActiveProtocol());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public long SCardTransmit(SCardTransmitParams params) {
        Log.i("Winscard", "SCardTransmit");
        long res = SCARD_F_UNKNOWN_ERROR.longValue();
        Object reader = CardHandlerManager.getInstance().get(Integer.valueOf(params.gethCard()));
        if (null == reader) {
            return SCARD_E_READER_UNAVAILABLE.longValue();
        }
        try {
            if (UsbDeviceAdapter.class.isInstance(reader)) {
                UsbDeviceAdapter usbReader = (UsbDeviceAdapter) reader;
                long len = usbReader.getReader().transmit(0, params

                                .getSendBuffer(),
                        (int) params.getCbSendLength(), params
                                .getRecvBuffer(),
                        (int) params.getCbRecvLength());
                if (len >= 0L) {
                    params.setCbRecvLength(len);
                    res = SCARD_S_SUCCESS.longValue();
                }
            } else if (BluetoothDeviceAdapter.class.isInstance(reader)) {
                ((BluetoothDeviceAdapter) reader).getReader().transmitApdu(params.getSendBuffer());
                if (((BluetoothDeviceAdapter) reader).waitForApduResponse()) {
                    ArrayList<Byte> apduResponse = ((BluetoothDeviceAdapter) reader).getApduResponse();

                    int len = apduResponse.size() > params.getCbRecvLength() ? (int) params.getCbRecvLength() : apduResponse.size();
                    byte[] recvBuffer = new byte[len];
                    for (int i = 0; i < len; i++) {
                        recvBuffer[i] = ((Byte) apduResponse.get(i)).byteValue();
                    }
                    params.setRecvBuffer(recvBuffer);
                    params.setCbRecvLength(len);
                    res = SCARD_S_SUCCESS.longValue();
                } else {
                    Log.e(Winscard.class.getSimpleName(), "WAITFORAPDURESPONSE FAILED");
                }
            }
        } catch (ReaderException e) {
            res = SCARD_F_INTERNAL_ERROR.longValue();
        }
        return res;
    }

    public long SCardGetAttrib(SCardGetAttribParams params) {
        Log.i("Winscard", "SCardGetAttrib");
        long res = SCARD_E_INVALID_PARAMETER.longValue();
        Object reader = CardHandlerManager.getInstance().get(Integer.valueOf(params.gethCard()));
        if (null == reader) {
            return res;
        }
        if (params.getDwAttrId() == SCARD_ATTR_ATR_STRING.longValue()) {
            if (UsbDeviceAdapter.class.isInstance(reader)) {
                byte[] value = ((UsbDeviceAdapter) reader).getReader().getAtr(0);
                params.setAttrValue(value);
                res = SCARD_S_SUCCESS.longValue();
            } else if (BluetoothDeviceAdapter.class.isInstance(reader)) {
                ArrayList<Byte> value = ((BluetoothDeviceAdapter) reader).getAtr();
                params.setAttrValue(value);
                res = SCARD_S_SUCCESS.longValue();
            }
        } else {
            res = ERROR_NOT_SUPPORTED.longValue();
        }
        return res;
    }

    public long SCardGetStatusChange(SCardGetStatusChangeParams params) {
        Log.i("Winscard", "SCardGetStatusChange");
        long res = SCARD_E_UNSUPPORTED_FEATURE.longValue();
        for (SCardReaderState readerState : params.getRgReaderStates()) {
            SCardConnectParams connectParams = new SCardConnectParams(null, readerState.getReaderName(), 0L, SCARD_PROTOCOL_T0.longValue() | SCARD_PROTOCOL_T1.longValue());
            res = SCardConnect(connectParams);
            if (SCARD_S_SUCCESS.longValue() == res) {
                SCardGetAttribParams getAttribParams = new SCardGetAttribParams(connectParams.getPhCard(), SCARD_ATTR_ATR_STRING.longValue());
                res = SCardGetAttrib(getAttribParams);
                if (SCARD_S_SUCCESS.longValue() == res) {
                    readerState.setAtr(getAttribParams.getPbAttr());
                    readerState.setAtrLen(Long.valueOf(getAttribParams.getPcbAttrLen()));
                    readerState.setEventState(SCARD_STATE_PRESENT.longValue());
                    res = SCARD_S_SUCCESS.longValue();
                }
                if (UsbDeviceAdapter.class.isInstance(
                        CardHandlerManager.getInstance().get(Integer.valueOf(connectParams.getPhCard())))) {
                    SCardDisconnect(new SCardDisconnectParams(connectParams.getPhCard(), 0L));
                }
            }
        }
        return res;
    }

    public long SCardStatus(SCardStatusParams params) {
        Log.i("Winscard", "SCardStatus");

        long res = SCARD_E_INVALID_HANDLE.longValue();
        Object reader;
        if (null == (reader = CardHandlerManager.getInstance().get(Integer.valueOf(params.gethCard())))) {
            return res;
        }
        if (UsbDeviceAdapter.class.isInstance(reader)) {
            Integer slot = Integer.valueOf(0);
            Reader usbReader = ((UsbDeviceAdapter) reader).getReader();
            params.setSzReaderName(usbReader.getReaderName());
            params.setPcchReaderLen(usbReader.getReaderName().length());
            params.setPdwState(usbReader.getState(slot.intValue()));
            params.setPdwProtocol(usbReader.getProtocol(slot.intValue()));

            SCardGetAttribParams getAttribParams = new SCardGetAttribParams(params.gethCard(), SCARD_ATTR_ATR_STRING.longValue());
            res = SCardGetAttrib(getAttribParams);
            if (res == SCARD_S_SUCCESS.longValue()) {
                params.setPbAtr(getAttribParams.getPbAttr());
                params.setPcbAtrLen(getAttribParams.getPcbAttrLen());
                switch ((int) params.getPdwState()) {
                    case 0:
                        params.setPdwState(SCARD_STATE_UNKNOWN.longValue());
                    case 1:
                        params.setPdwState(SCARD_STATE_EMPTY.longValue());
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        params.setPdwState(SCARD_STATE_PRESENT.longValue());
                }
            }
        } else if (BluetoothDeviceAdapter.class.isInstance(reader)) {
            BluetoothDeviceAdapter blueReader = (BluetoothDeviceAdapter) reader;
            params.setSzReaderName(blueReader.getReaderName());
            params.setPcchReaderLen(blueReader.getReaderName().length());
            params.setPdwState(blueReader.getStatus().intValue());
            params.setPdwProtocol(SCARD_PROTOCOL_T0.longValue());

            SCardGetAttribParams getAttribParams = new SCardGetAttribParams(params.gethCard(), SCARD_ATTR_ATR_STRING.longValue());
            res = SCardGetAttrib(getAttribParams);
            if (res == SCARD_S_SUCCESS.longValue()) {
                params.setPbAtr(getAttribParams.getPbAttr());
                params.setPcbAtrLen(getAttribParams.getPcbAttrLen());
                switch ((int) params.getPdwState()) {
                    case 0:
                        params.setPdwState(SCARD_STATE_UNKNOWN.longValue());
                        break;
                    case 1:
                        params.setPdwState(SCARD_STATE_EMPTY.longValue());
                        break;
                    case 2:
                    case 3:
                    case 255:
                        params.setPdwState(SCARD_STATE_PRESENT.longValue());
                }
            }
        }
        return res;
    }

    private long BlueSCardConnect(SCardConnectParams params, Integer forceCardId) {
        long res = SCARD_E_UNEXPECTED.longValue();
        try {
            BluetoothDeviceAdapter reader = CardHandlerManager.getInstance().getBlueReader();
            if (null == reader) {
                reader = new BluetoothDeviceAdapter(this.context, params.getSzReader());
                Log.i(Winscard.class.getSimpleName(), "PRIMA di connect");
                reader.connect();
                Log.i(Winscard.class.getSimpleName(), "PRIMA di waitForConnection");
                if (reader.waitForConnection()) {
                    Log.i(Winscard.class.getSimpleName(), "PRIMA di authenticate");
                    reader.getReader().authenticate(
                            Utils.hexString2Bytes("FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF".replaceAll(" ", "")));
                    Log.i(Winscard.class.getSimpleName(), "PRIMA di waitForAuthentication");
                    if (reader.waitForAuthentication()) {
                        Log.i(Winscard.class.getSimpleName(), "PRIMA di powerOnCard");
                        if (reader.getReader().powerOnCard()) {
                            Log.i(Winscard.class.getSimpleName(), "PRIMA di waitForAtr");
                            if (reader.waitForAtr()) {
                                Log.i(Winscard.class.getSimpleName(), "PRIMA di add");
                                if (null != forceCardId) {
                                    params.setPhCard(CardHandlerManager.getInstance().addBluetooth(forceCardId, reader).intValue());
                                } else {
                                    params.setPhCard(CardHandlerManager.getInstance().addBluetooth(reader).intValue());
                                }
                                res = SCARD_S_SUCCESS.longValue();
                            }
                        }
                    }
                }
                if ((res != SCARD_S_SUCCESS.longValue()) &&
                        (null != reader)) {
                    reader.close();
                    reader = null;
                }
            } else {
                params.setPhCard(CardHandlerManager.getInstance().getBlueReaderId());
                res = SCARD_S_SUCCESS.longValue();
            }
        } catch (BTDeviceNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    private long UsbSCardConnect(final SCardConnectParams params, Integer forceCardId) {
        long res = SCARD_E_UNEXPECTED.longValue();
        UsbManager usbManager = (UsbManager) this.context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = usbManager.getDeviceList();
        if ((devices != null) && (devices.size() > 0)) {
            final Reader reader = new Reader(usbManager);
            for (String deviceName : devices.keySet()) {
                UsbDevice device = (UsbDevice) devices.get(deviceName);
                if ((reader.isSupported(device)) && (device.getDeviceName().equals(params.getSzReader()))) {
                    try {
                        Log.d(TAG, "Opening devices.");
                        reader.open(device);
                        if (reader.isOpened()) {
                            UsbDeviceAdapter winscardReader = new UsbDeviceAdapter(reader, params.getSzReader());
                            if (null != forceCardId) {
                                params.setPhCard(CardHandlerManager.getInstance().add(forceCardId, winscardReader).intValue());
                            } else {
                                params.setPhCard(CardHandlerManager.getInstance().add(winscardReader).intValue());
                            }
                            final Boolean[] bError = new Boolean[1];
                            bError[0] = Boolean.valueOf(true);

                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                public void run() {
                                    synchronized (bError) {
                                        bError.notify();
                                    }
                                }
                            }, 1000L);

                            reader.setOnStateChangeListener(new Reader.OnStateChangeListener() {
                                public void onStateChange(int slotNum, int prevState, int currState) {
                                    if (currState == 2) {
                                        try {
                                            reader.power(slotNum, 2);
                                            reader.setProtocol(slotNum,
                                                    (int) params.getDwPreferredProtocols());
                                            params.setPdwActiveProtocol(reader
                                                    .getProtocol(0));
                                            bError[0] = Boolean.valueOf(false);
                                        } catch (ReaderException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    synchronized (bError) {
                                        bError.notify();
                                    }
                                }
                            });
                            try {
                                synchronized (bError) {
                                    bError.wait();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Log.d(TAG, e.getMessage());
                            } finally {
                                reader.setOnStateChangeListener(null);
                                t.cancel();
                            }
                            if (!bError[0].booleanValue()) {
                                res = SCARD_S_SUCCESS.longValue();
                            }
                        } else {
                            res = SCARD_F_INTERNAL_ERROR.longValue();
                        }
                    } catch (IllegalArgumentException e) {
                        res = SCARD_F_INTERNAL_ERROR.longValue();
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
            res = SCARD_E_READER_UNAVAILABLE.longValue();
        } else {
            res = SCARD_E_NO_READERS_AVAILABLE.longValue();
        }
        return res;
    }

    public class IllegalDeviceType
            extends Exception {
        public IllegalDeviceType() {
        }
    }
}
