package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.log.ConfigFile;
import com.mauriciotogneri.botcoin.log.Log;

import java.io.File;

public class CleanFiles
{
    public static void main(String[] args)
    {
        Log.truncate("error.txt");

        File root = new File("output");

        for (File symbol : root.listFiles())
        {
            ConfigFile configFile = new ConfigFile(symbol.getName());
            configFile.reset();

            Log.truncate(String.format("%s/last_operation.properties", symbol));
            Log.truncate(String.format("%s/logs.json", symbol));
            Log.truncate(String.format("%s/profit.txt", symbol));
            Log.truncate(String.format("%s/status.properties", symbol));
        }
    }
}