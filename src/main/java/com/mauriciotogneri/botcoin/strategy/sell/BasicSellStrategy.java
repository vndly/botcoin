package com.mauriciotogneri.botcoin.strategy.sell;

import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;

public class BasicSellStrategy implements SellStrategy
{
    private final BtcEurWallet wallet;
    private final float minAmountToSpend;

    public BasicSellStrategy(BtcEurWallet wallet, float minAmountToSpend)
    {
        this.wallet = wallet;
        this.minAmountToSpend = minAmountToSpend;
    }

    @Override
    public float sell(float price)
    {
        float result = 0;

        if ((price > wallet.boughtPrice()) && (wallet.boughtPrice() > 0))
        {
            float percentageUp = (price / wallet.boughtPrice()) - 1;
            float btcToSell = Math.min(wallet.balanceBTC() * percentageUp, wallet.balanceBTC());
            float eurToGain = btcToSell * price;

            if ((eurToGain >= minAmountToSpend) && (wallet.balanceBTC() >= btcToSell))
            {
                result = btcToSell;
            }
        }

        return result;
    }
}