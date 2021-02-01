package com.mauriciotogneri.botcoin.strategy;

import com.mauriciotogneri.botcoin.wallet.Wallet;

public class BasicStrategy implements Strategy
{
    private final Wallet wallet;
    private final BasicBuyStrategy buyStrategy;
    private final BasicSellStrategy sellStrategy;

    public BasicStrategy(Wallet wallet, BasicBuyStrategy buyStrategy, BasicSellStrategy sellStrategy)
    {
        this.wallet = wallet;
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    @Override
    public Operation operation(double price)
    {
        double buyAmount = buyStrategy.buy(price);
        double sellAmount = sellStrategy.sell(price);

        if (buyAmount > 0)
        {
            return Operation.buy(sellAmount);
        }
        else if (sellAmount > 0)
        {
            return Operation.sell(sellAmount);
        }
        else
        {
            return Operation.nothing();
        }
    }
}