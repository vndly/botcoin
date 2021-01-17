package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.BinanceProvider;
import com.mauriciotogneri.botcoin.provider.PriceProvider;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class PriceCollector
{
    private final String output;
    private final PriceProvider priceProvider;

    public PriceCollector(String output, PriceProvider priceProvider)
    {
        this.output = output;
        this.priceProvider = priceProvider;
    }

    public void start() throws Exception
    {
        FileWriter fileWriter = new FileWriter(output, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        while (priceProvider.hasMorePrices())
        {
            long timestamp = System.currentTimeMillis() / 1000;
            float price = priceProvider.price();
            String line = String.format("%s;%.2f\n", timestamp, price);

            bufferedWriter.write(line);
            bufferedWriter.flush();
        }
    }

    public static void main(String[] args) throws Exception
    {
        PriceProvider binance = new BinanceProvider("BTCEUR", 1000 * 60);

        PriceCollector priceCollector = new PriceCollector(
                "input/prices.csv",
                binance
        );
        priceCollector.start();
    }
}