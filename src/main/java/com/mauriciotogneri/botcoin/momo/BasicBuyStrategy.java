package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

public class BasicBuyStrategy
{
    private double allTimeHigh = 0;
    private final double minPercentageDown;
    private final double percentageBuyMultiplier;
    private final double minTradeAmountA;
    private final double minTradeAmountB;

    public BasicBuyStrategy(double minPercentageDown, double percentageBuyMultiplier, double minTradeAmountA, double minTradeAmountB)
    {
        this.minPercentageDown = minPercentageDown;
        this.percentageBuyMultiplier = percentageBuyMultiplier;
        this.minTradeAmountA = minTradeAmountA;
        this.minTradeAmountB = minTradeAmountB;
    }

    public double buy(double price, @NotNull Balance balanceA, @NotNull Balance balanceB, double boughtPrice)
    {
        double result = 0;

        if (balanceB.amount == 0) // first buy
        {
            if (price < allTimeHigh)
            {
                result = byFrom(price, allTimeHigh, balanceA, balanceB);
            }
            else
            {
                allTimeHigh = price;
            }
        }
        else if (price < boughtPrice) // average down
        {
            result = byFrom(price, boughtPrice, balanceA, balanceB);
        }

        return result;
    }

    private double byFrom(double price, double limit, @NotNull Balance balanceA, @NotNull Balance balanceB)
    {
        double result = 0;
        double percentageDown = 1 - (price / limit);

        if (percentageDown >= minPercentageDown)
        {
            double amountAToSpend = Math.min(balanceA.amount * percentageDown * percentageBuyMultiplier, balanceA.amount);
            double amountBToBuy = amountAToSpend / price;

            if ((amountAToSpend >= minTradeAmountA) && (amountAToSpend <= balanceA.amount) && (amountBToBuy >= minTradeAmountB))
            {
                result = amountBToBuy;
            }
        }

        return balanceB.formatAmount(result);
    }
}