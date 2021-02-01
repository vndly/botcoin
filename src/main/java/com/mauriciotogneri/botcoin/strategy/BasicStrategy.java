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
        Operation operation = null;
        double buyAmount = buyStrategy.buy(price);
        double sellAmount = sellStrategy.sell(price);

        if (buyAmount > 0)
        {
            wallet.buy(buyAmount, price);
            operation = Operation.buy(sellAmount);
        }
        else if (sellAmount > 0)
        {
            wallet.sell(sellAmount, price);
            operation = Operation.sell(sellAmount);
        }
        else
        {
            operation = Operation.nothing();
        }

        return operation;
    }
}