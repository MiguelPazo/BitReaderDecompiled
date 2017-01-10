package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardStatusParams
{
    private int hCard;
    private String szReaderName;
    private long pcchReaderLen;
    private long pdwState;
    private long pdwProtocol;
    private byte[] pbAtr;
    private long pcbAtrLen;

    public SCardStatusParams(int hCard)
    {
        sethCard(hCard);
    }

    public int gethCard()
    {
        return this.hCard;
    }

    public void sethCard(int hCard)
    {
        this.hCard = hCard;
    }

    public String getSzReaderName()
    {
        return this.szReaderName;
    }

    public void setSzReaderName(String szReaderName)
    {
        this.szReaderName = szReaderName;
    }

    public long getPcchReaderLen()
    {
        return this.pcchReaderLen;
    }

    public void setPcchReaderLen(long pcchReaderLen)
    {
        this.pcchReaderLen = pcchReaderLen;
    }

    public long getPdwState()
    {
        return this.pdwState;
    }

    public void setPdwState(long pdwState)
    {
        this.pdwState = pdwState;
    }

    public long getPdwProtocol()
    {
        return this.pdwProtocol;
    }

    public void setPdwProtocol(long pdwProtocol)
    {
        this.pdwProtocol = pdwProtocol;
    }

    public byte[] getPbAtr()
    {
        return this.pbAtr;
    }

    public void setPbAtr(byte[] pbAtr)
    {
        this.pbAtr = pbAtr;
    }

    public long getPcbAtrLen()
    {
        return this.pcbAtrLen;
    }

    public void setPcbAtrLen(long pcbAtrLen)
    {
        this.pcbAtrLen = pcbAtrLen;
    }
}
