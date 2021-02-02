package com.mauriciotogneri.botcoin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.operation.BuyOperation;
import com.mauriciotogneri.botcoin.operation.SellOperation;
import com.mauriciotogneri.botcoin.provider.Price;
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

    public void buy(@NotNull Price price, @NotNull BuyOperation buyOperation)
    {
        console("OPERATION: BUY\n");
        console("AMOUNT:    " + buyOperation.amount);
        console("PRICE:     " + buyOperation.price);
        console("SPENT:     " + buyOperation.spent);
        balance(buyOperation.balanceA, buyOperation.balanceB, buyOperation.total);

        JsonObject buyJson = new JsonObject();
        buyJson.add("amount", buyOperation.amount.json());
        buyJson.add("price", buyOperation.price.json());
        buyJson.add("spent", buyOperation.spent.json());
        buyJson.add("balanceA", buyOperation.balanceA.json());
        buyJson.add("balanceB", buyOperation.balanceB.json());
        buyJson.add("total", buyOperation.total.json());

        JsonObject json = price.json();
        json.add("buy", buyJson);

        file(gson.toJson(json) + ",");
    }

    public void sell(@NotNull Price price, @NotNull SellOperation sellOperation)
    {
        console("OPERATION: SELL\n");
        console("AMOUNT:    " + sellOperation.amount);
        console("PRICE:     " + sellOperation.price);
        console("GAINED:    " + sellOperation.gained);
        console("PROFIT:    " + sellOperation.profit);
        balance(sellOperation.balanceA, sellOperation.balanceB, sellOperation.total);

        JsonObject sellJson = new JsonObject();
        sellJson.add("amount", sellOperation.amount.json());
        sellJson.add("price", sellOperation.price.json());
        sellJson.add("gained", sellOperation.gained.json());
        sellJson.add("profit", sellOperation.profit.json());
        sellJson.add("balanceA", sellOperation.balanceA.json());
        sellJson.add("balanceB", sellOperation.balanceB.json());
        sellJson.add("total", sellOperation.total.json());

        JsonObject json = price.json();
        json.add("sell", sellJson);

        file(gson.toJson(json) + ",");
    }

    public void balance(@NotNull Balance balanceA, @NotNull Balance balanceB, @NotNull Balance total)
    {
        console("");
        console("BALANCE:   " + balanceA);
        console("BALANCE:   " + balanceB);
        console("TOTAL:     " + total);
        console("\n====================================\n");
    }

    public void price(@NotNull Price price)
    {
        file(gson.toJson(price.json()) + ",");
    }
}