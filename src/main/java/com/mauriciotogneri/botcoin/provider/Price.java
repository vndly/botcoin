package com.mauriciotogneri.botcoin.provider;

import java.math.BigDecimal;

public class Price
{
    public final long timestamp;
    public final BigDecimal value;

    public Price(long timestamp, BigDecimal value)
    {
        this.timestamp = timestamp;
        this.value = value;
    }
}