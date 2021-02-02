package com.mauriciotogneri.botcoin.wallet;

public class Currency
{
    public final String symbol;
    public final int decimals;

    public static final Currency EUR = new Currency("EUR", 2);
    public static final Currency USD = new Currency("USD", 2);

    public static final Currency BTC = new Currency("BTC", 8);
    public static final Currency ETH = new Currency("ETH", 8);

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