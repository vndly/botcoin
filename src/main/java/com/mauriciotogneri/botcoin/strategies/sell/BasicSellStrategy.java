package com.mauriciotogneri.botcoin.strategies.sell;

public class BasicSellStrategy implements SellStrategy
{
    @Override
    public float sell()
    {
        return 0.1f;
    }
}