package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Wallet;

public class BasicBuyStrategy
{
    private double allTimeHigh = 0;
    private final Wallet wallet;
    private final double minPercentageDown;
    private final double percentageBuyMultiplier;
    private final double minEurToSpend;

    public BasicBuyStrategy(Wallet wallet, double minPercentageDown, double percentageBuyMultiplier, double minEurToSpend)
    {
        this.wallet = wallet;
        this.minPercentageDown = minPercentageDown;
        this.percentageBuyMultiplier = percentageBuyMultiplier;
        this.minEurToSpend = minEurToSpend;
    }

    public double buy(double price)
    {
        double result = 0;

        if (wallet.balanceB.amount == 0) // first buy
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
            double eurToSpend = Math.min(wallet.balanceA.amount * percentageDown * percentageBuyMultiplier, wallet.balanceA.amount);

            if ((eurToSpend > 0) && (eurToSpend >= minEurToSpend) && (eurToSpend <= wallet.balanceA.amount))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}