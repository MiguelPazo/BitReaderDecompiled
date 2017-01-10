package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardConnectParams
{
    private Object hContext;
    private String szReader;
    private long dwShareMode;
    private long dwPreferredProtocols;
    private int phCard;
    private long pdwActiveProtocol;

    public SCardConnectParams()
    {
        this.hContext = null;
        this.szReader = "UNKNOWN";
        this.dwShareMode = 0L;
        this.dwPreferredProtocols = 0L;
        this.phCard = 0;
        this.pdwActiveProtocol = 0L;
    }

    public SCardConnectParams(Object hContext, String szReader, long dwShareMode, long dwPreferredProtocols)
    {
        this.hContext = hContext;
        this.szReader = szReader;
        this.dwShareMode = dwShareMode;
        this.dwPreferredProtocols = dwPreferredProtocols;
        this.phCard = 0;
        this.pdwActiveProtocol = 0L;
    }

    public Object gethContext()
    {
        return this.hContext;
    }

    public void sethContext(Object hContext)
    {
        this.hContext = hContext;
    }

    public String getSzReader()
    {
        return this.szReader;
    }

    public void setSzReader(String szReader)
    {
        this.szReader = szReader;
    }

    public long getDwShareMode()
    {
        return this.dwShareMode;
    }

    public void setDwShareMode(long dwShareMode)
    {
        this.dwShareMode = dwShareMode;
    }

    public long getDwPreferredProtocols()
    {
        return this.dwPreferredProtocols;
    }

    public void setDwPreferredProtocols(long dwPreferredProtocols)
    {
        this.dwPreferredProtocols = dwPreferredProtocols;
    }

    public int getPhCard()
    {
        return this.phCard;
    }

    public void setPhCard(int phCard)
    {
        this.phCard = phCard;
    }

    public long getPdwActiveProtocol()
    {
        return this.pdwActiveProtocol;
    }

    public void setPdwActiveProtocol(long pdwActiveProtocol)
    {
        this.pdwActiveProtocol = pdwActiveProtocol;
    }
}
