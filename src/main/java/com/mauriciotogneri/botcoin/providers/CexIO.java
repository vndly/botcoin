package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.providers.CexIO.Payload;

public class CexIO extends Provider<Payload>
{
    public CexIO()
    {
        super(Payload.class, "https://cex.io/api/ticker/BTC/USD");
    }

    @Override
    public float value(Payload object)
    {
        return (object.ask + object.ask) / 2;
    }

    public static class Payload
    {
        public float bid;
        public float ask;
    }
}
