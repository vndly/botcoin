package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.json.Json;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;

public class Log
{
    private boolean empty = true;
    private final BufferedWriter writer;

    public Log(@NotNull String path)
    {
        try
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
        catch (Exception e)
        {
            e.printStackTrace();

            throw new RuntimeException("Cannot create log file " + path);
        }
    }

    public static void console(String var1, Object... var2)
    {
        System.out.printf(var1 + "%n", var2);
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

    public void jsonFile(Object object)
    {
        file(Json.toJsonString(object));
    }

    public static void jsonConsole(Object object)
    {
        console(Json.toJsonString(object));
    }
}