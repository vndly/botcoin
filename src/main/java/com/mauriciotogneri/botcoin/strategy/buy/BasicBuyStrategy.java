package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;

public class BasicBuyStrategy implements BuyStrategy
{
    private float allTimeHigh = 0;
    private final BtcEurWallet wallet;
    private final float minEurThreshold;
    private final float minPercentageThreshold;
    private final float percentageMultiplier;

    public BasicBuyStrategy(BtcEurWallet wallet, float minEurThreshold, float minPercentageThreshold, float percentageMultiplier)
    {
        this.wallet = wallet;
        this.minEurThreshold = minEurThreshold;
        this.minPercentageThreshold = minPercentageThreshold;
        this.percentageMultiplier = percentageMultiplier;
    }

    @Override
    public float buy(float price)
    {
        float result = 0;

        if (wallet.balanceBTC() == 0) // first buy
        {
            if (price < allTimeHigh)
            {
                float percentageDown = 1 - (price / allTimeHigh);

                if (percentageDown >= minPercentageThreshold)
                {
                    float eurToSpend = wallet.balanceEUR() * percentageDown * percentageMultiplier;

                    if ((eurToSpend >= minEurThreshold) && (wallet.balanceEUR() >= eurToSpend))
                    {
                        result = eurToSpend / price;
                    }
                }
            }
            else
            {
                allTimeHigh = price;
            }
        }
        else if (price < wallet.boughtPrice()) // average down
        {
            float percentageDown = 1 - (price / wallet.boughtPrice());
            float eurToSpend = wallet.balanceEUR() * percentageDown * percentageMultiplier;

            if ((eurToSpend >= minEurThreshold) && (wallet.balanceEUR() >= eurToSpend))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}