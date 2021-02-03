package com.mauriciotogneri.botcoin.provider;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;

public class BinancePriceProvider implements DataProvider<Price>
{
    private final String symbol;
    private final int frequency;
    private final BinanceApiRestClient client;

    public BinancePriceProvider(String symbol, int frequency)
    {
        this.symbol = symbol;
        this.frequency = frequency;
        this.client = BinanceApi.client();
    }

    @Override
    public boolean hasData()
    {
        return true;
    }

    @Override
    public Price data() throws Exception
    {
        Thread.sleep(frequency * 1000L);
        TickerPrice tickerPrice = client.getPrice(symbol);

        return new Price(
                System.currentTimeMillis(),
                Double.parseDouble(tickerPrice.getPrice())
        );
    }

    public static class Payload
    {
        public double price;
    }
}