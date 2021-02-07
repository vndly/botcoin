package com.mauriciotogneri.botcoin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class Json
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static JsonElement toJsonObject(Object object)
    {
        return gson.toJsonTree(object);
    }
    
    public static String toJsonString(Object object)
    {
        return gson.toJson(object);
    }
}