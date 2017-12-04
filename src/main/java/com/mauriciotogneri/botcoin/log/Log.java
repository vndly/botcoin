package com.mauriciotogneri.botcoin.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Log
{
    private final BufferedWriter writer;

    public Log(File file) throws IOException
    {
        if (!file.exists())
        {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file, true);
        writer = new BufferedWriter(fileWriter);
    }

    public void log(List<Float> values)
    {
        List<String> list = values.stream().map(Object::toString).collect(Collectors.toList());
        log(String.join(",", list));
    }

    public void log(String data)
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
}