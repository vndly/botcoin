package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.providers.CoinDesk.Payload;

public class CoinDesk extends Provider<Payload>
{
    public CoinDesk()
    {
        super(Payload.class, "https://api.coindesk.com/v1/bpi/currentprice/USD.json");
    }

    @Override
    public float value(Payload object)
    {
        return object.bpi.USD.rate_float;
    }

    public static class Payload
    {
        public Bpi bpi;
    }

    public static class Bpi
    {
        public USD USD;
    }

    public static class USD
    {
        public float rate_float;
    }
}
