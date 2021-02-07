package com.mauriciotogneri.botcoin.wallet;

import java.math.BigDecimal;

public class Balance
{
    public final Currency currency;
    public BigDecimal amount;

    public Balance(Currency currency, BigDecimal amount)
    {
        this.currency = currency;
        this.amount = amount;
    }

    public Balance(Currency currency, String amount)
    {
        this(currency, new BigDecimal(amount));
    }

    public Balance of(BigDecimal value)
    {
        return new Balance(currency, value);
    }
}