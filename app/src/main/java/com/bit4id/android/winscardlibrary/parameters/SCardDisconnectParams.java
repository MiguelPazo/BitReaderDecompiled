package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardDisconnectParams
{
    private int hCard;
    private long dwDisposition;

    public SCardDisconnectParams(int hCard, long dwDisposition)
    {
        sethCard(hCard);
        setDwDisposition(dwDisposition);
    }

    public int gethCard()
    {
        return this.hCard;
    }

    public void sethCard(int hCard)
    {
        this.hCard = hCard;
    }

    public long getDwDisposition()
    {
        return this.dwDisposition;
    }

    public void setDwDisposition(long dwDisposition)
    {
        this.dwDisposition = dwDisposition;
    }
}
