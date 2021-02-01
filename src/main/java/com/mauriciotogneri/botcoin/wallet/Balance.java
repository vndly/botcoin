package com.mauriciotogneri.botcoin.wallet;

public class Balance
{
    public final Currency currency;
    public double amount;

    public Balance(Currency currency, double amount)
    {
        this.currency = currency;
        this.amount = amount;
    }

    public String format(double value)
    {
        return String.format("%." + currency.decimals + "f %s", value, currency);
    }

    @Override
    public String toString()
    {
        return format(amount);
    }
}