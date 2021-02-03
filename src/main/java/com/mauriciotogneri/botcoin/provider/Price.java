package com.mauriciotogneri.botcoin.provider;

import com.google.gson.JsonObject;

public class Price extends Data
{
    public final double value;

    public Price(long timestamp, double value)
    {
        super(timestamp);

        this.value = value;
    }

    public JsonObject json()
    {
        JsonObject json = new JsonObject();
        json.addProperty("timestamp", timestamp);
        json.addProperty("price", value);

        return json;
    }
}