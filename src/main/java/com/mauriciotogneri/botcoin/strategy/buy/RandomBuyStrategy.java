package com.mauriciotogneri.botcoin.strategy.buy;

import java.util.Random;

public class RandomBuyStrategy implements BuyStrategy
{
    @Override
    public float buy(float price)
    {
        return new Random().nextFloat();
    }
}