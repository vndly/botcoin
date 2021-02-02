package com.mauriciotogneri.botcoin.strategy;

import org.jetbrains.annotations.NotNull;

public class Operation
{
    public final Action action;
    public final double amount;
    public final double price;

    public Operation(Action action, double amount, double price)
    {
        this.action = action;
        this.amount = amount;
        this.price = price;
    }

    @NotNull
    public static Operation nothing()
    {
        return new Operation(Action.NOTHING, 0, 0);
    }

    @NotNull
    public static Operation buy(double amount, double price)
    {
        return new Operation(Action.BUY, amount, price);
    }

    @NotNull
    public static Operation sell(double amount, double price)
    {
        return new Operation(Action.SELL, amount, price);
    }
}