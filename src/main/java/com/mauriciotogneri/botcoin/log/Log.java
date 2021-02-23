package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.json.Json;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
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
                truncate(file.getAbsolutePath());
            }
            else if (!file.createNewFile())
            {
                throw new RuntimeException("Cannot create log file " + path);
            }

            FileWriter fileWriter = new FileWriter(file, true);
            writer = new BufferedWriter(fileWriter);
        }
        catch (Exception e)
        {
            Log.error(e);

            throw new RuntimeException("Cannot create log file " + path);
        }
    }

    public static void truncate(String path)
    {
        try
        {
            FileChannel fileChannel = new FileOutputStream(path, true).getChannel();
            fileChannel.truncate(0);
            fileChannel.close();
        }
        catch (Exception e)
        {
            Log.error(e);
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
            Log.error(e);
        }
    }

    public void write(String data)
    {
        try
        {
            writer.write(data);
            writer.flush();
        }
        catch (Exception e)
        {
            Log.error(e);
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

    public static void error(Exception exception)
    {
        exception.printStackTrace();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        error(stringWriter.toString());
    }

    public static void error(String text)
    {
        try
        {
            File file = new File("error.txt");

            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(String.format("%s%n%n%n", text));
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}