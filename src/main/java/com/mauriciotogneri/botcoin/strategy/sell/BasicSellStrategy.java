package com.mauriciotogneri.botcoin.strategy.sell;

import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;

public class BasicSellStrategy implements SellStrategy
{
    private final BtcEurWallet wallet;
    private final float minAmountToGain;
    private final float percentageMultiplier;

    public BasicSellStrategy(BtcEurWallet wallet, float minEurToGain, float percentageMultiplier)
    {
        this.wallet = wallet;
        this.minAmountToGain = minEurToGain;
        this.percentageMultiplier = percentageMultiplier;
    }

    @Override
    public float sell(float price)
    {
        float result = 0;

        if ((price > wallet.boughtPrice()) && (wallet.boughtPrice() > 0))
        {
            float percentageUp = (price / wallet.boughtPrice()) - 1;
            float btcToSell = Math.min(wallet.balanceBTC() * percentageUp * percentageMultiplier, wallet.balanceBTC());
            float eurToGain = btcToSell * price;

            if ((eurToGain >= minAmountToGain) && (wallet.balanceBTC() >= btcToSell))
            {
                result = btcToSell;
            }
        }

        return result;
    }
}