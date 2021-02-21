package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.log.Log;

import java.io.File;

public class CleanFiles
{
    public static void main(String[] args)
    {
        File root = new File("output");

        for (File symbol : root.listFiles())
        {
            Log.truncate(String.format("%s/last_operation.properties", symbol));
            Log.truncate(String.format("%s/logs.json", symbol));
            Log.truncate(String.format("%s/profit.txt", symbol));
            Log.truncate(String.format("%s/status.properties", symbol));
        }
    }
}