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

    public Balance of(double value)
    {
        return new Balance(currency, value);
    }

    public String format(double value)
    {
        return String.format("%s %s", formatAmount(value), currency);
    }

    public double formatAmount(double value)
    {
        return Double.parseDouble(String.format("%." + currency.decimals + "f", value));
    }

    @Override
    public String toString()
    {
        return format(amount);
    }
}