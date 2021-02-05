package com.mauriciotogneri.botcoin.provider;

import com.google.gson.JsonObject;

public class Price implements Data
{
    public final long timestamp;
    public final double value;

    public Price(long timestamp, double value)
    {
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public JsonObject json()
    {
        JsonObject json = new JsonObject();
        json.addProperty("timestamp", timestamp);
        json.addProperty("price", value);

        return json;
    }
}