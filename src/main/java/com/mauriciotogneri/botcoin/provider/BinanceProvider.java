package com.mauriciotogneri.botcoin.provider;

import com.mauriciotogneri.botcoin.network.HttpRequest;

public class BinanceProvider implements PriceProvider
{
    private final int frequency;
    private final String url;
    private final HttpRequest httpRequest = new HttpRequest();

    public BinanceProvider(String symbol, int frequency)
    {
        this.frequency = frequency;
        this.url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;
    }

    @Override
    public boolean hasMorePrices()
    {
        return true;
    }

    @Override
    public double price() throws Exception
    {
        Thread.sleep(frequency);

        Payload payload = httpRequest.execute(url, Payload.class);

        return payload.price;
    }

    public static class Payload
    {
        public double price;
    }
}