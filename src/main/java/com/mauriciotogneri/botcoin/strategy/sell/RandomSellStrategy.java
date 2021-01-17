package com.mauriciotogneri.botcoin.strategy.sell;

import java.util.Random;

public class RandomSellStrategy implements SellStrategy
{
    @Override
    public float sell(float price)
    {
        return new Random().nextFloat();
    }
}