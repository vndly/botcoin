package com.mauriciotogneri.botcoin.provider;

import com.google.gson.JsonObject;

public abstract class Data
{
    public final long timestamp;

    public Data(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public abstract JsonObject json();
}