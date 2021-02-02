package com.mauriciotogneri.botcoin.wallet;

import com.google.gson.JsonObject;

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
        return String.format("%." + currency.decimals + "f %s", value, currency);
    }

    public JsonObject json()
    {
        JsonObject json = new JsonObject();
        json.addProperty("amount", Double.parseDouble(String.format("%." + currency.decimals + "f", amount)));
        json.addProperty("currency", currency.symbol);

        return json;
    }

    @Override
    public String toString()
    {
        return format(amount);
    }
}