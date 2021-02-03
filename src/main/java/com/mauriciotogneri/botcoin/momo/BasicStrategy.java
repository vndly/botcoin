package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Intent;
import com.mauriciotogneri.botcoin.strategy.Strategy;

import org.jetbrains.annotations.NotNull;

public class BasicStrategy implements Strategy<Price>
{
    private final BasicBuyStrategy buyStrategy;
    private final BasicSellStrategy sellStrategy;

    public BasicStrategy(BasicBuyStrategy buyStrategy, BasicSellStrategy sellStrategy)
    {
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    @Override
    public Intent intent(@NotNull Price price)
    {
        double buyAmount = buyStrategy.buy(price.value);
        double sellAmount = sellStrategy.sell(price.value);

        if (buyAmount > 0)
        {
            return Intent.buy(buyAmount, price.value);
        }
        else if (sellAmount > 0)
        {
            return Intent.sell(sellAmount, price.value);
        }
        else
        {
            return Intent.nothing();
        }
    }
}