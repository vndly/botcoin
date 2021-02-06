package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

public class BasicSellStrategy
{
    private final double minPercentageUp;
    private final double percentageSellMultiplier;
    private final double sellAllLimit;
    private final double minEurToGain;

    public BasicSellStrategy(double minPercentageUp, double percentageSellMultiplier, double sellAllLimit, double minEurToGain)
    {
        this.minPercentageUp = minPercentageUp;
        this.percentageSellMultiplier = percentageSellMultiplier;
        this.sellAllLimit = sellAllLimit;
        this.minEurToGain = minEurToGain;
    }

    public double sell(double price, @NotNull Balance balanceB, double boughtPrice)
    {
        double result = 0;

        if ((price > boughtPrice) && (boughtPrice > 0))
        {
            double percentageUp = (price / boughtPrice) - 1;

            if (percentageUp >= minPercentageUp)
            {
                double btcToSell;

                if (balanceB.amount <= sellAllLimit)
                {
                    btcToSell = balanceB.amount;
                }
                else
                {
                    btcToSell = Math.min(balanceB.amount * percentageUp * percentageSellMultiplier, balanceB.amount);
                }

                double eurToGain = btcToSell * price;

                if ((eurToGain >= minEurToGain) && (btcToSell <= balanceB.amount))
                {
                    result = btcToSell;
                }
            }
        }

        return balanceB.formatAmount(result);
    }
}