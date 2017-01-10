package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import java.util.ArrayList;

public class SCardGetAttribParams {
    private int hCard;
    private long dwAttrId;
    private byte[] pbAttr;
    private long pcbAttrLen;

    public SCardGetAttribParams(int hCard, long dwAttrId) {
        sethCard(hCard);
        setDwAttrId(dwAttrId);
    }

    public int gethCard() {
        return this.hCard;
    }

    public void sethCard(int hCard) {
        this.hCard = hCard;
    }

    public long getDwAttrId() {
        return this.dwAttrId;
    }

    public void setDwAttrId(long dwAttrId) {
        this.dwAttrId = dwAttrId;
    }

    public byte[] getPbAttr() {
        return this.pbAttr;
    }

    public void setPbAttr(byte[] pbAttr) {
        this.pbAttr = pbAttr;
    }

    public long getPcbAttrLen() {
        return this.pcbAttrLen;
    }

    public void setPcbAttrLen(long pcbAttrLen) {
        this.pcbAttrLen = pcbAttrLen;
    }

    public void setAttrValue(byte[] value) {
        this.pbAttr = value;
        this.pcbAttrLen = value.length;
    }

    public void setAttrValue(ArrayList<Byte> value) {
        this.pcbAttrLen = value.size();
        this.pbAttr = new byte[(int) this.pcbAttrLen];
        for (int i = 0; i < value.size(); i++) {
            this.pbAttr[i] = ((Byte) value.get(i)).byteValue();
        }
    }
}
