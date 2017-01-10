package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardReconnectParams
{
    private int hCard;
    private long dwShareMode;
    private long dwPreferredProtocols;
    private long dwInitialization;
    private long pdwActiveProtocol;

    public SCardReconnectParams(int hCard, long dwShareMode, long dwPreferredProtocols, long dwInitialization)
    {
        this.hCard = hCard;
        this.dwShareMode = dwShareMode;
        this.dwPreferredProtocols = dwPreferredProtocols;
        this.dwInitialization = dwInitialization;
        this.pdwActiveProtocol = 0L;
    }

    public int gethCard()
    {
        return this.hCard;
    }

    public void sethCard(int hCard)
    {
        this.hCard = hCard;
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

    public long getDwInitialization()
    {
        return this.dwInitialization;
    }

    public void setDwInitialization(long dwInitialization)
    {
        this.dwInitialization = dwInitialization;
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
