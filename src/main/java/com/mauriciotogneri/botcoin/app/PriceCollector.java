package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mauriciotogneri.botcoin.exchange.BinanceApi;

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
    private final Integer limit;
    private final String output;
    private final BinanceApiRestClient client;

    public PriceCollector(String pair, String interval, Integer limit, String output)
    {
        this.pair = pair;
        this.interval = interval;
        this.limit = limit;
        this.output = output;
        this.client = BinanceApi.client();
    }

    public void start() throws Exception
    {
        FileWriter fileWriter = new FileWriter(output, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        long lastTimestamp = System.currentTimeMillis();
        int count = 0;

        while (true)
        {
            PriceEntry[] entries = priceEntries(lastTimestamp);

            for (PriceEntry entry : entries)
            {
                String line = String.format("%s;%.2f\n", entry.timestamp, entry.price);
                bufferedWriter.write(line);
                lastTimestamp = entry.timestamp - 1;
            }

            bufferedWriter.flush();

            count += entries.length;

            if (((limit != 0) && (count >= limit)) || (entries.length == 0))
            {
                break;
            }

            Thread.sleep(1000);
        }
    }

    @NotNull
    private PriceEntry[] priceEntries(long lastTimestamp)
    {
        List<PriceEntry> result = new ArrayList<>();
        List<Candlestick> candlesticks = client.getCandlestickBars(pair, CandlestickInterval.valueOf(interval), null, null, lastTimestamp);

        for (Candlestick candlestick : candlesticks)
        {
            long timestamp = candlestick.getCloseTime();
            double high = Double.parseDouble(candlestick.getHigh());
            double low = Double.parseDouble(candlestick.getLow());
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
        String pair = "LINKEUR";
        String interval = "ONE_MINUTE";
        Integer limit = 2000;
        String fileName = String.format("input/prices_%s_%s.csv", pair, interval);

        PriceCollector priceCollector = new PriceCollector(pair, interval, limit, fileName);
        priceCollector.start();
    }
}