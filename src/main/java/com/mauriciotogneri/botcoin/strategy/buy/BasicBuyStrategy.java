package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.BasicWallet;

public class BasicBuyStrategy implements BuyStrategy
{
    private float allTimeHigh = 0;
    private final BasicWallet wallet;
    private final float minEurThreshold;
    private final float minPercentageThreshold;
    private final float percentageMultiplier;

    public BasicBuyStrategy(BasicWallet wallet, float minEurThreshold, float minPercentageThreshold, float percentageMultiplier)
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
                result = byFrom(price, allTimeHigh);
            }
            else
            {
                allTimeHigh = price;
            }
        }
        else if (price < wallet.boughtPrice()) // average down
        {
            result = byFrom(price, wallet.boughtPrice());
        }

        return result;
    }

    private float byFrom(float price, float limit)
    {
        float result = 0;
        float percentageDown = 1 - (price / limit);

        if (percentageDown >= minPercentageThreshold)
        {
            float eurToSpend = Math.min(wallet.balanceEUR() * percentageDown * percentageMultiplier, wallet.balanceEUR());

            if ((eurToSpend >= minEurThreshold) && (wallet.balanceEUR() >= eurToSpend))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}