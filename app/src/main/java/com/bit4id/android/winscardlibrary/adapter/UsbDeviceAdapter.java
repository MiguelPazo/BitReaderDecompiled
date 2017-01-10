package com.bit4id.android.winscardlibrary.adapter;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import com.acs.smartcard.Reader;

public class UsbDeviceAdapter
{
    private Reader reader;
    private String readerName;

    public UsbDeviceAdapter(Reader reader, String readerName)
    {
        setReader(reader);
        setReaderName(readerName);
    }

    public Reader getReader()
    {
        return this.reader;
    }

    public void setReader(Reader reader)
    {
        this.reader = reader;
    }

    public String getReaderName()
    {
        return this.readerName;
    }

    public void setReaderName(String readerName)
    {
        this.readerName = readerName;
    }
}
