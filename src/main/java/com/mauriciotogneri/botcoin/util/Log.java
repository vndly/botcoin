package com.mauriciotogneri.botcoin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Log
{
    private final BufferedWriter writer;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Log(@NotNull String path) throws IOException
    {
        File file = new File(path);

        if (file.exists())
        {
            FileChannel fileChannel = new FileOutputStream(file, true).getChannel();
            fileChannel.truncate(0);
            fileChannel.close();
        }
        else
        {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file, true);
        writer = new BufferedWriter(fileWriter);
    }

    public void log(String data)
    {
        console(data);
        file(data);
    }

    public void console(String data)
    {
        System.out.println(data);
    }

    public void file(String data)
    {
        try
        {
            writer.write(data + "\n");
            writer.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void buy(@NotNull Balance amount, @NotNull Balance price, @NotNull Balance spent)
    {
        console("OPERATION: BUY\n");
        console("AMOUNT:    " + amount);
        console("PRICE:     " + price);
        console("SPENT:     " + spent);

        JsonObject json = new JsonObject();
        json.addProperty("operation", "buy");
        json.add("amount", amount.json());
        json.add("price", price.json());
        json.add("spent", spent.json());
        file(gson.toJson(json));
    }

    public void sell(@NotNull Balance amount, @NotNull Balance price, @NotNull Balance gained, @NotNull Balance profit)
    {
        console("OPERATION: SELL\n");
        console("AMOUNT:    " + amount);
        console("PRICE:     " + price);
        console("GAINED:    " + gained);
        console("PROFIT:    " + profit);

        JsonObject json = new JsonObject();
        json.addProperty("operation", "sell");
        json.add("amount", amount.json());
        json.add("price", price.json());
        json.add("gained", gained.json());
        json.add("profit", profit.json());
        file(gson.toJson(json));
    }
}