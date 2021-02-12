package com.mauriciotogneri.botcoin.wallet;

public class Asset
{
    public final Currency currency;
    public final int decimals;
    public final int step;

    public Asset(Currency currency, int decimals, int step)
    {
        this.currency = currency;
        this.decimals = decimals;
        this.step = step;
    }
}