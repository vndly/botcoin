package com.mauriciotogneri.botcoin.providers;

import com.mauriciotogneri.botcoin.providers.eToro.Payload;

public class eToro extends Provider<Payload>
{
    public eToro()
    {
        super(Payload.class, "https://candle.etoro.com/candles/desc.json/OneMinute/2/100000");
    }

    @Override
    public float value(Payload object)
    {
        return (object.Candles[0].RangeOpen + object.Candles[0].RangeClose + object.Candles[0].RangeHigh + object.Candles[0].RangeLow) / 4;
    }

    public static class Payload
    {
        public Candles[] Candles;
    }

    public static class Candles
    {
        public float RangeOpen;
        public float RangeClose;
        public float RangeHigh;
        public float RangeLow;
    }
}
