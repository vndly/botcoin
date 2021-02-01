package com.mauriciotogneri.botcoin.wallet;

public class Currency
{
    public final String symbol;
    public final int decimals;

    public Currency(String symbol, int decimals)
    {
        this.symbol = symbol;
        this.decimals = decimals;
    }

    @Override
    public String toString()
    {
        return symbol;
    }
}