package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Balance;

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

    public double sell(double price, Balance balanceB, double boughtPrice)
    {
        double result = 0;

        if ((price > boughtPrice) && (boughtPrice > 0))
        {
            double percentageUp = (price / boughtPrice) - 1;

            if (percentageUp >= minPercentageUp)
            {
                double btcToSell = Math.min(balanceB.amount * percentageUp * percentageSellMultiplier, balanceB.amount);

                if (balanceB.amount <= sellAllLimit)
                {
                    btcToSell = balanceB.amount;
                }

                double eurToGain = btcToSell * price;

                if ((eurToGain >= minEurToGain) && (balanceB.amount >= btcToSell))
                {
                    result = btcToSell;
                }
            }
        }

        return result;
    }
}