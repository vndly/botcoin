package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mauriciotogneri.botcoin.exchange.BinanceApi;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
            List<PriceEntry> entries = priceEntries(lastTimestamp);

            for (PriceEntry entry : entries)
            {
                String line = String.format("%s;%.2f\n", entry.timestamp, entry.price);
                bufferedWriter.write(line);
                lastTimestamp = entry.timestamp - 1;
            }

            bufferedWriter.flush();

            count += entries.size();

            if (((limit != 0) && (count >= limit)) || (entries.isEmpty()))
            {
                break;
            }

            Thread.sleep(1000);
        }
    }

    @NotNull
    private List<PriceEntry> priceEntries(long lastTimestamp)
    {
        List<PriceEntry> result = new ArrayList<>();
        List<Candlestick> candlesticks = client.getCandlestickBars(pair, CandlestickInterval.valueOf(interval), null, null, lastTimestamp);

        for (Candlestick candlestick : candlesticks)
        {
            long timestamp = candlestick.getCloseTime();
            BigDecimal high = new BigDecimal(candlestick.getHigh());
            BigDecimal low = new BigDecimal(candlestick.getLow());
            BigDecimal price = (high.add(low)).divide(new BigDecimal(2), 2, RoundingMode.DOWN);

            result.add(new PriceEntry(timestamp, price));
        }

        Collections.reverse(result);

        return result;
    }

    private static class PriceEntry
    {
        public final long timestamp;
        public final BigDecimal price;

        private PriceEntry(long timestamp, BigDecimal price)
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