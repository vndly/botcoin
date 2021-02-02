package com.mauriciotogneri.botcoin.strategy;

public class BasicStrategy implements Strategy
{
    private final BasicBuyStrategy buyStrategy;
    private final BasicSellStrategy sellStrategy;

    public BasicStrategy(BasicBuyStrategy buyStrategy, BasicSellStrategy sellStrategy)
    {
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    @Override
    public Intent intent(double price)
    {
        double buyAmount = buyStrategy.buy(price);
        double sellAmount = sellStrategy.sell(price);

        if (buyAmount > 0)
        {
            return Intent.buy(buyAmount, price);
        }
        else if (sellAmount > 0)
        {
            return Intent.sell(sellAmount, price);
        }
        else
        {
            return Intent.nothing();
        }
    }
}