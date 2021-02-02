package com.mauriciotogneri.botcoin.provider;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;

public class BinanceProvider implements PriceProvider
{
    private final String symbol;
    private final int frequency;
    private final BinanceApiRestClient client;

    public BinanceProvider(String symbol, int frequency)
    {
        this.symbol = symbol;
        this.frequency = frequency;
        this.client = BinanceApi.client();
    }

    @Override
    public boolean hasPrice()
    {
        return true;
    }

    @Override
    public double price() throws Exception
    {
        Thread.sleep(frequency * 1000L);
        TickerPrice tickerPrice = client.getPrice(symbol);

        return Double.parseDouble(tickerPrice.getPrice());
    }

    public static class Payload
    {
        public double price;
    }
}