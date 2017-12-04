package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.providers.Bitaps.Payload;

public class Bitaps extends Provider<Payload>
{
    public Bitaps()
    {
        super(Payload.class, "https://bitaps.com/api/ticker");
    }

    @Override
    public float value(Payload object)
    {
        return object.usd;
    }

    public static class Payload
    {
        public float usd;
    }
}
