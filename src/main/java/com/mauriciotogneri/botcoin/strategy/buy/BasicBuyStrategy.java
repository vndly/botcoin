package com.mauriciotogneri.botcoin.strategy.buy;

public class BasicBuyStrategy implements BuyStrategy
{
    private float lastPrice = 0;

    @Override
    public float buy(float price)
    {
        lastPrice = price;

        return 0.1f;
    }
}