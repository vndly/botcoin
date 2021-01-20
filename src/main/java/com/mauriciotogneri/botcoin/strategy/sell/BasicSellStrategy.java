package com.mauriciotogneri.botcoin.strategy.sell;

import com.mauriciotogneri.botcoin.wallet.BasicWallet;

public class BasicSellStrategy implements SellStrategy
{
    private final BasicWallet wallet;
    private final float minPercentageUp;
    private final float percentageSellMultiplier;
    private final float sellAllLimit;
    private final float minEurToGain;

    public BasicSellStrategy(BasicWallet wallet, float minPercentageUp, float percentageSellMultiplier, float sellAllLimit, float minEurToGain)
    {
        this.wallet = wallet;
        this.minPercentageUp = minPercentageUp;
        this.percentageSellMultiplier = percentageSellMultiplier;
        this.sellAllLimit = sellAllLimit;
        this.minEurToGain = minEurToGain;
    }

    @Override
    public float sell(float price)
    {
        float result = 0;

        if ((price > wallet.boughtPrice()) && (wallet.boughtPrice() > 0))
        {
            float percentageUp = (price / wallet.boughtPrice()) - 1;

            if (percentageUp >= minPercentageUp)
            {
                float btcToSell = Math.min(wallet.balanceBTC() * percentageUp * percentageSellMultiplier, wallet.balanceBTC());

                if (wallet.balanceBTC() <= sellAllLimit)
                {
                    btcToSell = wallet.balanceBTC();
                }

                float eurToGain = btcToSell * price;

                if ((eurToGain >= minEurToGain) && (wallet.balanceBTC() >= btcToSell))
                {
                    result = btcToSell;
                }
            }
        }

        return result;
    }
}