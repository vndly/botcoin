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

    public Balance(Asset asset, String amount)
    {
        this(asset, new BigDecimal(amount));
    }

    public Balance of(BigDecimal value)
    {
        return new Balance(asset, value);
    }
}