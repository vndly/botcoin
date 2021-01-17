package com.mauriciotogneri.botcoin.strategies.buy;

public class BasicBuyStrategy implements BuyStrategy
{
    @Override
    public float buy()
    {
        return 0.1f;
    }
}