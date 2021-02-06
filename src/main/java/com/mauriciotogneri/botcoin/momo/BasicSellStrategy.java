package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

public class BasicSellStrategy
{
    private final double minPercentageUp;
    private final double percentageSellMultiplier;
    private final double sellAllLimit;
    private final double minTradeAmountA;
    private final double minTradeAmountB;

    public BasicSellStrategy(double minPercentageUp, double percentageSellMultiplier, double sellAllLimit, double minTradeAmountA, double minTradeAmountB)
    {
        this.minPercentageUp = minPercentageUp;
        this.percentageSellMultiplier = percentageSellMultiplier;
        this.sellAllLimit = sellAllLimit;
        this.minTradeAmountA = minTradeAmountA;
        this.minTradeAmountB = minTradeAmountB;
    }

    public double sell(double price, @NotNull Balance balanceB, double boughtPrice)
    {
        double result = 0;

        if ((price > boughtPrice) && (boughtPrice > 0))
        {
            double percentageUp = (price / boughtPrice) - 1;

            if (percentageUp >= minPercentageUp)
            {
                double amountBToSell;

                if (balanceB.amount <= sellAllLimit)
                {
                    amountBToSell = balanceB.amount;
                }
                else
                {
                    amountBToSell = Math.min(balanceB.amount * percentageUp * percentageSellMultiplier, balanceB.amount);
                }

                double amountAToGain = amountBToSell * price;

                if ((amountAToGain >= minTradeAmountA) && (amountBToSell <= balanceB.amount) && (amountBToSell >= minTradeAmountB))
                {
                    result = amountBToSell;
                }
            }
        }

        return balanceB.formatAmount(result);
    }
}