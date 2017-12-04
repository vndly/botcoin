package com.mauriciotogneri.botcoin.providers;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Provider<T>
{
    private final Class<T> clazz;
    private final String url;
    private final OkHttpClient client;
    private final Gson gson;
    private float last;

    public Provider(Class<T> clazz, String url)
    {
        this.clazz = clazz;
        this.url = url;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    private T payload() throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        String string = response.body().string();

        return gson.fromJson(string, clazz);
    }

    public float value()
    {
        try
        {
            last = value(payload());

            return last;
        }
        catch (Exception e)
        {
            return last;
        }
    }

    public abstract float value(T object);
}