package com.mauriciotogneri.botcoin.strategy;

import org.jetbrains.annotations.NotNull;

public class Intent
{
    public final Action action;
    public final double quantity;
    public final double price;

    public Intent(Action action, double quantity, double price)
    {
        this.action = action;
        this.quantity = quantity;
        this.price = price;
    }

    @NotNull
    public static Intent nothing()
    {
        return new Intent(Action.NOTHING, 0, 0);
    }

    @NotNull
    public static Intent buy(double quantity, double price)
    {
        return new Intent(Action.BUY, quantity, price);
    }

    @NotNull
    public static Intent sell(double quantity, double price)
    {
        return new Intent(Action.SELL, quantity, price);
    }
}