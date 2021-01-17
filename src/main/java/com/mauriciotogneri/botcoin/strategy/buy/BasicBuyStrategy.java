package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;

public class BasicBuyStrategy implements BuyStrategy
{
    private float allTimeHigh = 0;
    private final BtcEurWallet wallet;
    private final float minAmountToSpend;
    private final float percentageMultiplier;

    public BasicBuyStrategy(BtcEurWallet wallet, float minAmountToSpend, float percentageMultiplier)
    {
        this.wallet = wallet;
        this.minAmountToSpend = minAmountToSpend;
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
                float eurToSpend = wallet.balanceEUR() * percentageDown * percentageMultiplier;

                if ((eurToSpend >= minAmountToSpend) && (wallet.balanceEUR() >= eurToSpend))
                {
                    result = eurToSpend / price;
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

            if ((eurToSpend >= minAmountToSpend) && (wallet.balanceEUR() >= eurToSpend))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}