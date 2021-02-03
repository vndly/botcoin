package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

public class BinanceApi
{
    public static BinanceApiRestClient client()
    {
        String apiKey = System.getenv("BINANCE_API");
        String secret = System.getenv("BINANCE_SECRET");

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secret);

        return factory.newRestClient();
    }
}