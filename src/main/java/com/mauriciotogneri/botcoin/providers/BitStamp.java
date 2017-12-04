package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.providers.BitStamp.Payload;

public class BitStamp extends Provider<Payload>
{
    public BitStamp()
    {
        super(Payload.class, "https://www.bitstamp.net/api/v2/ticker/btcusd");
    }

    @Override
    public float value(Payload object)
    {
        return (object.bid + object.ask) / 2;
    }

    public static class Payload
    {
        public float bid;
        public float ask;
    }
}
