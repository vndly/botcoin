package com.mauriciotogneri.botcoin.strategy.sell;

import com.mauriciotogneri.botcoin.wallet.BasicWallet;

public class BasicSellStrategy implements SellStrategy
{
    private final BasicWallet wallet;
    private final double minPercentageUp;
    private final double percentageSellMultiplier;
    private final double sellAllLimit;
    private final double minEurToGain;

    public BasicSellStrategy(BasicWallet wallet, double minPercentageUp, double percentageSellMultiplier, double sellAllLimit, double minEurToGain)
    {
        this.wallet = wallet;
        this.minPercentageUp = minPercentageUp;
        this.percentageSellMultiplier = percentageSellMultiplier;
        this.sellAllLimit = sellAllLimit;
        this.minEurToGain = minEurToGain;
    }

    @Override
    public double sell(double price)
    {
        double result = 0;

        if ((price > wallet.boughtPrice()) && (wallet.boughtPrice() > 0))
        {
            double percentageUp = (price / wallet.boughtPrice()) - 1;

            if (percentageUp >= minPercentageUp)
            {
                double btcToSell = Math.min(wallet.balanceBTC() * percentageUp * percentageSellMultiplier, wallet.balanceBTC());

                if (wallet.balanceBTC() <= sellAllLimit)
                {
                    btcToSell = wallet.balanceBTC();
                }

                double eurToGain = btcToSell * price;

                if ((eurToGain >= minEurToGain) && (wallet.balanceBTC() >= btcToSell))
                {
                    result = btcToSell;
                }
            }
        }

        return result;
    }
}