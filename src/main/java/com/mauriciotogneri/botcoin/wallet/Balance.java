package com.mauriciotogneri.botcoin.wallet;

public class Balance
{
    public final String currency;
    public final int decimals;
    public double amount;

    public Balance(String currency, int decimals, double amount)
    {
        this.currency = currency;
        this.decimals = decimals;
        this.amount = amount;
    }

    public String format(double value)
    {
        return String.format("%." + decimals + "f", value);
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", format(amount), currency);
    }
}