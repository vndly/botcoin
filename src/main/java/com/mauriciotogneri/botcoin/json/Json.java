package com.mauriciotogneri.botcoin.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

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
            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    //.registerTypeAdapter(Asset.class, serializer)
                    .create();
        }

        return gson;
    }

    //private static final JsonSerializer<Asset> serializer = (currency, typeOfSrc, context) -> new JsonPrimitive(currency);
}