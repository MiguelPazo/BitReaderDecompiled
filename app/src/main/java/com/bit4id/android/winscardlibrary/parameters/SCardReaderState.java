package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardReaderState
{
    private String szReader;
    private byte[] pbUserData;
    private long dwCurrentState;
    private long dwEventState;
    private long cbAtr;
    private byte[] rgbAtr;

    public SCardReaderState(String readerName)
    {
        this.szReader = readerName;
        this.pbUserData = null;
        this.dwCurrentState = 0L;
        this.dwEventState = 0L;
        this.rgbAtr = null;
        this.cbAtr = 0L;
    }

    public String getReaderName()
    {
        return this.szReader;
    }

    public void setReaderName(String value)
    {
        this.szReader = value;
    }

    public long getAtrLen()
    {
        return this.cbAtr;
    }

    public void setAtrLen(Long value)
    {
        this.cbAtr = value.longValue();
    }

    public byte[] getAtr()
    {
        return this.rgbAtr;
    }

    public void setAtr(byte[] atr)
    {
        this.rgbAtr = atr;
    }

    public long getCurrentState()
    {
        return this.dwCurrentState;
    }

    public void setCurrentState(long value)
    {
        this.dwCurrentState = value;
    }

    public long getEventState()
    {
        return this.dwEventState;
    }

    public void setEventState(long value)
    {
        this.dwEventState = value;
    }
}
