package com.bit4id.android.winscardlibrary.parameters;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import java.util.ArrayList;

public class SCardListReadersParams
{
    private Object hContext;
    private ArrayList<String> mszGroups;
    private ArrayList<String> mszReaders;
    private long pcchReaders;

    public SCardListReadersParams()
    {
        this.hContext = null;
        this.mszReaders = new ArrayList();
        this.mszGroups = new ArrayList();
        this.pcchReaders = 0L;
    }

    public Object gethContext()
    {
        return this.hContext;
    }

    public void sethContext(Object hContext)
    {
        this.hContext = hContext;
    }

    public ArrayList<String> getMszGroups()
    {
        return this.mszGroups;
    }

    public void setMszGroups(ArrayList<String> mszGroups)
    {
        this.mszGroups = mszGroups;
    }

    public ArrayList<String> getMszReaders()
    {
        return this.mszReaders;
    }

    public void setMszReaders(ArrayList<String> mszReaders)
    {
        this.mszReaders = mszReaders;
    }

    public long getPcchReaders()
    {
        return this.pcchReaders;
    }

    public void setPcchReaders(long pcchReaders)
    {
        this.pcchReaders = pcchReaders;
    }

    public void clearReaders()
    {
        this.mszReaders.clear();
    }

    public void addReader(String reader)
    {
        this.mszReaders.add(reader);
    }

    public void addReaders(ArrayList<String> readers)
    {
        this.mszReaders.addAll(readers);
    }

    public int numReaders()
    {
        return this.mszReaders.size();
    }
}
