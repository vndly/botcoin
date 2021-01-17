package com.mauriciotogneri.botcoin.strategy.sell;

import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;

public class BasicSellStrategy implements SellStrategy
{
    private final BtcEurWallet wallet;
    private final float minEurThreshold;
    private final float minPercentageThreshold;
    private final float percentageMultiplier;

    public BasicSellStrategy(BtcEurWallet wallet, float minEurThreshold, float minPercentageThreshold, float percentageMultiplier)
    {
        this.wallet = wallet;
        this.minEurThreshold = minEurThreshold;
        this.minPercentageThreshold = minPercentageThreshold;
        this.percentageMultiplier = percentageMultiplier;
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
                // TODO: if the BTC balance is less than a threshold => sell all

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