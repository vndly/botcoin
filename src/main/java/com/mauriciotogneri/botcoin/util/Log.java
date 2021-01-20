package com.mauriciotogneri.botcoin.util;

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
        /*try
        {
            writer.write(data + "\n");
            writer.flush();

            System.out.println(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }
}