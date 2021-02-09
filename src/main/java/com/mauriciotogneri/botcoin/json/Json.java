package com.mauriciotogneri.botcoin.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.mauriciotogneri.botcoin.wallet.Currency;

public class Json
{
    private static Gson gson;

    public static JsonElement toJsonObject(Object object)
    {
        return gson().toJsonTree(object);
    }

    public static String toJsonString(Object object)
    {
        return gson().toJson(object);
    }

    private static Gson gson()
    {
        if (gson == null)
        {
            gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Currency.class, serializer).create();
        }

        return gson;
    }

    private static final JsonSerializer<Currency> serializer = (currency, typeOfSrc, context) -> new JsonPrimitive(currency.symbol);
}