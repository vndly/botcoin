package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.network.HttpRequest;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceCollector
{
    private final String pair;
    private final String interval;
    private final String output;
    private final HttpRequest httpRequest = new HttpRequest();

    public PriceCollector(String pair, String interval, String output)
    {
        this.pair = pair;
        this.interval = interval;
        this.output = output;
    }

    public void start() throws Exception
    {
        FileWriter fileWriter = new FileWriter(output, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        long lastTimestamp = System.currentTimeMillis();

        while (true)
        {
            PriceEntry[] entries = priceEntries(lastTimestamp);

            if (entries.length == 0)
            {
                break;
            }

            for (PriceEntry entry : entries)
            {
                String line = String.format("%s;%.2f\n", entry.timestamp, entry.price);
                bufferedWriter.write(line);
                lastTimestamp = entry.timestamp - 1;
            }

            bufferedWriter.flush();
            Thread.sleep(1000);
        }
    }

    @NotNull
    private PriceEntry[] priceEntries(long lastTimestamp)
    {
        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&endTime=%s", pair, interval, lastTimestamp);
        Object[][] entries = httpRequest.execute(url, Object[][].class);
        List<PriceEntry> result = new ArrayList<>();

        for (Object[] entry : entries)
        {
            long timestamp = ((Double) entry[0]).longValue();
            double high = Double.parseDouble((String) entry[2]);
            double low = Double.parseDouble((String) entry[3]);
            double price = (high + low) / 2;

            result.add(new PriceEntry(timestamp, price));
        }

        Collections.reverse(result);

        return result.toArray(new PriceEntry[result.size()]);
    }

    private static class PriceEntry
    {
        public final long timestamp;
        public final double price;

        private PriceEntry(long timestamp, double price)
        {
            this.timestamp = timestamp;
            this.price = price;
        }
    }

    public static void main(String[] args) throws Exception
    {
        PriceCollector priceCollector = new PriceCollector("LINKEUR", "1m", "input/prices3.csv");
        priceCollector.start();
    }
}