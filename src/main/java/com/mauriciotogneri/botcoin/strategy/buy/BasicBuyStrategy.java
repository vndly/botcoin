package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.BasicWallet;

public class BasicBuyStrategy implements BuyStrategy
{
    private double allTimeHigh = 0;
    private final BasicWallet wallet;
    private final double minPercentageDown;
    private final double percentageBuyMultiplier;
    private final double minEurToSpend;

    public BasicBuyStrategy(BasicWallet wallet, double minPercentageDown, double percentageBuyMultiplier, double minEurToSpend)
    {
        this.wallet = wallet;
        this.minPercentageDown = minPercentageDown;
        this.percentageBuyMultiplier = percentageBuyMultiplier;
        this.minEurToSpend = minEurToSpend;
    }

    @Override
    public double buy(double price)
    {
        double result = 0;

        if (wallet.balanceBTC() == 0) // first buy
        {
            if (price < allTimeHigh)
            {
                result = byFrom(price, allTimeHigh);
            }
            else
            {
                allTimeHigh = price;
            }
        }
        else if (price < wallet.boughtPrice()) // average down
        {
            result = byFrom(price, wallet.boughtPrice());
        }

        return result;
    }

    private double byFrom(double price, double limit)
    {
        double result = 0;
        double percentageDown = 1 - (price / limit);

        if (percentageDown >= minPercentageDown)
        {
            double eurToSpend = Math.min(wallet.balanceEUR() * percentageDown * percentageBuyMultiplier, wallet.balanceEUR());

            if ((eurToSpend > 0) && (eurToSpend >= minEurToSpend) && (eurToSpend <= wallet.balanceEUR()))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}