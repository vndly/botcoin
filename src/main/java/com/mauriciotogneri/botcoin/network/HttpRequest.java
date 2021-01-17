package com.mauriciotogneri.botcoin.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

public class HttpRequest
{
    private final OkHttpClient client;

    public HttpRequest()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);

        this.client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public <T> T execute(String url, Class<T> clazz)
    {
        String body = response(url);

        return new Gson().fromJson(body, clazz);
    }

    public JsonElement execute(String url)
    {
        String body = response(url);

        return JsonParser.parseString(body);
    }

    @NotNull
    private String response(String url)
    {
        try
        {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() == 429)
            {
                System.exit(-1);
            }

            return response.body().string();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}