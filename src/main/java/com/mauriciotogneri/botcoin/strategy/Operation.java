package com.mauriciotogneri.botcoin.strategy;

import org.jetbrains.annotations.NotNull;

public class Operation
{
    public final Action action;
    public final double amount;

    public Operation(Action action, double amount)
    {
        this.action = action;
        this.amount = amount;
    }

    @NotNull
    public static Operation nothing()
    {
        return new Operation(Action.NOTHING, 0);
    }

    @NotNull
    public static Operation buy(double amount)
    {
        return new Operation(Action.BUY, amount);
    }
    
    @NotNull
    public static Operation sell(double amount)
    {
        return new Operation(Action.SELL, amount);
    }
}