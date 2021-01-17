package com.mauriciotogneri.botcoin;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Botcoin
{
    public static void main(String[] args) throws Exception
    {
        FileWriter fileWriter = new FileWriter("prices.csv", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        Binance binance = new Binance();

        while (true)
        {
            long timestamp = System.currentTimeMillis() / 1000;
            float price = binance.price("BTCEUR");
            String line = String.format("%s;%s\n", timestamp, price);

            bufferedWriter.write(line);
            bufferedWriter.flush();

            Thread.sleep(1000 * 60);
        }
    }
}