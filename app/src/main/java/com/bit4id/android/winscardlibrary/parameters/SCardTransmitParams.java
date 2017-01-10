package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

public class SCardTransmitParams {
    private int hCard;
    private Object pioSendPci;
    private byte[] sendBuffer;
    private long cbSendLength;
    private Object pioRecvPci;
    public byte[] recvBuffer;
    public long cbRecvLength;

    public SCardTransmitParams(int hCard, byte[] sendBuffer, int cbSendLength, byte[] recvBuffer, int cbRecvLength) {
        this.recvBuffer = recvBuffer;
        sethCard(hCard);
        setSendBuffer(sendBuffer);
        setCbSendLength(cbSendLength);
        setCbRecvLength(cbRecvLength);
        setPioSendPci(null);
        setPioRecvPci(null);
    }

    public int gethCard() {
        return this.hCard;
    }

    public void sethCard(int hCard) {
        this.hCard = hCard;
    }

    public Object getPioSendPci() {
        return this.pioSendPci;
    }

    public void setPioSendPci(Object pioSendPci) {
        this.pioSendPci = pioSendPci;
    }

    public byte[] getSendBuffer() {
        return this.sendBuffer;
    }

    public void setSendBuffer(byte[] sendBuffer) {
        this.sendBuffer = sendBuffer;
    }

    public long getCbSendLength() {
        return this.cbSendLength;
    }

    public void setCbSendLength(long cbSendLength) {
        this.cbSendLength = cbSendLength;
    }

    public Object getPioRecvPci() {
        return this.pioRecvPci;
    }

    public void setPioRecvPci(Object pioRecvPci) {
        this.pioRecvPci = pioRecvPci;
    }

    public byte[] getRecvBuffer() {
        return this.recvBuffer;
    }

    public void setRecvBuffer(byte[] recvBuffer) {
        for (int i = 0; i < Math.min(recvBuffer.length, this.cbRecvLength); i++) {
            this.recvBuffer[i] = recvBuffer[i];
        }
    }

    public long getCbRecvLength() {
        return this.cbRecvLength;
    }

    public void setCbRecvLength(long cbRecvLength) {
        this.cbRecvLength = cbRecvLength;
    }
}
