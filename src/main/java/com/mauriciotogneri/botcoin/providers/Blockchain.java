package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.providers.Blockchain.Payload;

public class Blockchain extends Provider<Payload>
{
    public Blockchain()
    {
        super(Payload.class, "https://blockchain.info/ticker");
    }

    @Override
    public float value(Payload object)
    {
        return (object.USD.buy + object.USD.sell) / 2;
    }

    public static class Payload
    {
        public USD USD;
    }

    public static class USD
    {
        public float buy;
        public float sell;
    }
}
