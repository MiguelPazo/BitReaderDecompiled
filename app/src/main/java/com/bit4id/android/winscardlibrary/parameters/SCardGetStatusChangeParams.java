package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardGetStatusChangeParams
{
    private Object hContext;
    private long dwTimeout;
    private SCardReaderState[] rgReaderStates;
    private long cReaders;

    public SCardGetStatusChangeParams(long timeout, SCardReaderState[] rgReaderStates, long cReaders)
    {
        sethContext(null);
        setDwTimeout(timeout);
        setRgReaderStates(rgReaderStates);
        setcReaders(cReaders);
    }

    public Object gethContext()
    {
        return this.hContext;
    }

    public void sethContext(Object hContext)
    {
        this.hContext = hContext;
    }

    public long getDwTimeout()
    {
        return this.dwTimeout;
    }

    public void setDwTimeout(long dwTimeout)
    {
        this.dwTimeout = dwTimeout;
    }

    public SCardReaderState[] getRgReaderStates()
    {
        return this.rgReaderStates;
    }

    public void setRgReaderStates(SCardReaderState[] rgReaderStates)
    {
        this.rgReaderStates = rgReaderStates;
    }

    public long getcReaders()
    {
        return this.cReaders;
    }

    public void setcReaders(long cReaders)
    {
        this.cReaders = cReaders;
    }
}
