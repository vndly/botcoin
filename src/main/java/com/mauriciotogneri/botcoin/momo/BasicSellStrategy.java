package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Wallet;

public class BasicSellStrategy
{
    private final Wallet wallet;
    private final double minPercentageUp;
    private final double percentageSellMultiplier;
    private final double sellAllLimit;
    private final double minEurToGain;

    public BasicSellStrategy(Wallet wallet, double minPercentageUp, double percentageSellMultiplier, double sellAllLimit, double minEurToGain)
    {
        this.wallet = wallet;
        this.minPercentageUp = minPercentageUp;
        this.percentageSellMultiplier = percentageSellMultiplier;
        this.sellAllLimit = sellAllLimit;
        this.minEurToGain = minEurToGain;
    }

    public double sell(double price)
    {
        double result = 0;

        if ((price > wallet.boughtPrice()) && (wallet.boughtPrice() > 0))
        {
            double percentageUp = (price / wallet.boughtPrice()) - 1;

            if (percentageUp >= minPercentageUp)
            {
                double btcToSell = Math.min(wallet.balanceB.amount * percentageUp * percentageSellMultiplier, wallet.balanceB.amount);

                if (wallet.balanceB.amount <= sellAllLimit)
                {
                    btcToSell = wallet.balanceB.amount;
                }

                double eurToGain = btcToSell * price;

                if ((eurToGain >= minEurToGain) && (wallet.balanceB.amount >= btcToSell))
                {
                    result = btcToSell;
                }
            }
        }

        return result;
    }
}