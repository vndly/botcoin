package com.mauriciotogneri.botcoin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mauriciotogneri.botcoin.provider.Data;
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
    private boolean empty = true;
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
            if (!file.createNewFile())
            {
                throw new RuntimeException("Cannot create log file " + path);
            }
        }

        FileWriter fileWriter = new FileWriter(file, true);
        writer = new BufferedWriter(fileWriter);
    }

    public void console(String data)
    {
        System.out.println(data);
    }

    public void file(String data)
    {
        try
        {
            if (empty)
            {
                empty = false;
            }
            else
            {
                writer.write(",\n");
            }

            writer.write(data);
            writer.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void log(Object object)
    {
        file(gson.toJson(object));
    }

    public void balance(@NotNull Balance balanceA, @NotNull Balance balanceB, @NotNull Balance total)
    {
        console("");
        console("BALANCE:   " + balanceA);
        console("BALANCE:   " + balanceB);
        console("TOTAL:     " + total);
        console("\n====================================\n");
    }

    public void price(@NotNull Data data)
    {
        file(gson.toJson(data.json()));
    }
}