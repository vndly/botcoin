package com.mauriciotogneri.botcoin.provider;

public class Price
{
    public final long timestamp;
    public final double value;

    public Price(long timestamp, double value)
    {
        this.timestamp = timestamp;
        this.value = value;
    }
}