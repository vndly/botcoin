package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.network.HttpRequest;
import com.mauriciotogneri.botcoin.utils.Decimal;

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

    public float price() throws Exception
    {
        Thread.sleep(frequency);

        Payload payload = httpRequest.execute(url, Payload.class);

        return Decimal.currency(payload.price);
    }

    public static class Payload
    {
        public float price;
    }
}