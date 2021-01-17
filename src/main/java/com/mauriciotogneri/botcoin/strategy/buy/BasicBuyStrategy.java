package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;

import static com.mauriciotogneri.botcoin.util.Decimal.crypto;

public class BasicBuyStrategy implements BuyStrategy
{
    private float allTimeHigh = 0;
    private final BtcEurWallet wallet;
    private final float minAmountToSpend;

    public BasicBuyStrategy(BtcEurWallet wallet, float minAmountToSpend)
    {
        this.wallet = wallet;
        this.minAmountToSpend = minAmountToSpend;
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
                float eurToSpend = wallet.balanceEUR() * percentageDown;

                if ((eurToSpend >= minAmountToSpend) && (wallet.balanceEUR() >= eurToSpend))
                {
                    result = crypto(eurToSpend / price);
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
            float eurToSpend = wallet.balanceEUR() * percentageDown;

            if ((eurToSpend >= minAmountToSpend) && (wallet.balanceEUR() >= eurToSpend))
            {
                result = crypto(eurToSpend / price);
            }
        }

        return result;
    }
}