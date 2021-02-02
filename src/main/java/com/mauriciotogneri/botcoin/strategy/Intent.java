package com.mauriciotogneri.botcoin.strategy;

import org.jetbrains.annotations.NotNull;

public class Intent
{
    public final Action action;
    public final double amount;
    public final double price;

    public Intent(Action action, double amount, double price)
    {
        this.action = action;
        this.amount = amount;
        this.price = price;
    }

    @NotNull
    public static Intent nothing()
    {
        return new Intent(Action.NOTHING, 0, 0);
    }

    @NotNull
    public static Intent buy(double amount, double price)
    {
        return new Intent(Action.BUY, amount, price);
    }

    @NotNull
    public static Intent sell(double amount, double price)
    {
        return new Intent(Action.SELL, amount, price);
    }
}