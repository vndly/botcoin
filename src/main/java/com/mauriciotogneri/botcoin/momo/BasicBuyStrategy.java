package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

public class BasicBuyStrategy
{
    private double allTimeHigh = 0;
    private final double minPercentageDown;
    private final double percentageBuyMultiplier;
    private final double minEurToSpend;

    public BasicBuyStrategy(double minPercentageDown, double percentageBuyMultiplier, double minEurToSpend)
    {
        this.minPercentageDown = minPercentageDown;
        this.percentageBuyMultiplier = percentageBuyMultiplier;
        this.minEurToSpend = minEurToSpend;
    }

    public double buy(double price, @NotNull Balance balanceA, @NotNull Balance balanceB, double boughtPrice)
    {
        double result = 0;

        if (balanceB.amount == 0) // first buy
        {
            if (price < allTimeHigh)
            {
                result = byFrom(price, allTimeHigh, balanceA);
            }
            else
            {
                allTimeHigh = price;
            }
        }
        else if (price < boughtPrice) // average down
        {
            result = byFrom(price, boughtPrice, balanceA);
        }

        return result;
    }

    private double byFrom(double price, double limit, @NotNull Balance balanceA)
    {
        double result = 0;
        double percentageDown = 1 - (price / limit);

        if (percentageDown >= minPercentageDown)
        {
            double eurToSpend = Math.min(balanceA.amount * percentageDown * percentageBuyMultiplier, balanceA.amount);

            if ((eurToSpend > 0) && (eurToSpend >= minEurToSpend) && (eurToSpend <= balanceA.amount))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}