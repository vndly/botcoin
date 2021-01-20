package com.mauriciotogneri.botcoin.strategy.sell;

import com.mauriciotogneri.botcoin.wallet.BasicWallet;

public class BasicSellStrategy implements SellStrategy
{
    private final BasicWallet wallet;
    private final float minEurThreshold;
    private final float minPercentageThreshold;
    private final float percentageMultiplier;
    private final float sellAllLimit;

    public BasicSellStrategy(BasicWallet wallet, float minEurThreshold, float minPercentageThreshold, float percentageMultiplier, float sellAllLimit)
    {
        this.wallet = wallet;
        this.minEurThreshold = minEurThreshold;
        this.minPercentageThreshold = minPercentageThreshold;
        this.percentageMultiplier = percentageMultiplier;
        this.sellAllLimit = sellAllLimit;
    }

    @Override
    public float sell(float price)
    {
        float result = 0;

        if ((price > wallet.boughtPrice()) && (wallet.boughtPrice() > 0))
        {
            float percentageUp = (price / wallet.boughtPrice()) - 1;

            if (percentageUp >= minPercentageThreshold)
            {
                float btcToSell = Math.min(wallet.balanceBTC() * percentageUp * percentageMultiplier, wallet.balanceBTC());

                if (wallet.balanceBTC() <= sellAllLimit)
                {
                    btcToSell = wallet.balanceBTC();
                }

                float eurToGain = btcToSell * price;

                if ((eurToGain >= minEurThreshold) && (wallet.balanceBTC() >= btcToSell))
                {
                    result = btcToSell;
                }
            }
        }

        return result;
    }
}