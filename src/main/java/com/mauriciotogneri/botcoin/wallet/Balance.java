package com.mauriciotogneri.botcoin.wallet;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Balance
{
    public final Asset asset;
    public BigDecimal amount;

    public Balance(Asset asset, BigDecimal amount)
    {
        this.asset = asset;
        this.amount = amount;
    }

    public Balance of(@NotNull BigDecimal value)
    {
        return new Balance(asset, value);
    }

    public String property(String name)
    {
        return String.format("%s=%s %s%n", name, amount.setScale(asset.decimals, RoundingMode.DOWN).toString(), asset.currency.name());
    }
}