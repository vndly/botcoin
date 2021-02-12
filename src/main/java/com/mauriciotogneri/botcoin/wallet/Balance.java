package com.mauriciotogneri.botcoin.wallet;

import java.math.BigDecimal;

public class Balance
{
    public final Asset asset;
    public BigDecimal amount;

    public Balance(Asset asset, BigDecimal amount)
    {
        this.asset = asset;
        this.amount = amount;
    }

    public Balance of(BigDecimal value)
    {
        return new Balance(asset, value);
    }

    public String property()
    {
        return String.format("quantity=%s %s%n", amount.toString(), asset.currency.name());
    }
}