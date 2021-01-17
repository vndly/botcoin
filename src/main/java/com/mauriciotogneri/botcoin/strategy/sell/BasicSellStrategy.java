package com.mauriciotogneri.botcoin.strategy.sell;

public class BasicSellStrategy implements SellStrategy
{
    @Override
    public float sell(float price)
    {
        return 0.1f;
    }
}