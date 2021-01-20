package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.BasicWallet;

public class BasicBuyStrategy implements BuyStrategy
{
    private float allTimeHigh = 0;
    private final BasicWallet wallet;
    private final float minPercentageDown;
    private final float percentageBuyMultiplier;
    private final float minEurToSpend;

    public BasicBuyStrategy(BasicWallet wallet, float minPercentageDown, float percentageBuyMultiplier, float minEurToSpend)
    {
        this.wallet = wallet;
        this.minPercentageDown = minPercentageDown;
        this.percentageBuyMultiplier = percentageBuyMultiplier;
        this.minEurToSpend = minEurToSpend;
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

        if (percentageDown >= minPercentageDown)
        {
            float eurToSpend = Math.min(wallet.balanceEUR() * percentageDown * percentageBuyMultiplier, wallet.balanceEUR());

            if ((eurToSpend > 0) && (eurToSpend >= minEurToSpend) && (eurToSpend <= wallet.balanceEUR()))
            {
                result = eurToSpend / price;
            }
        }

        return result;
    }
}