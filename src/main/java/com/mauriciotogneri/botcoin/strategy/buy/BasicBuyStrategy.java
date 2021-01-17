package com.mauriciotogneri.botcoin.strategy.buy;

import com.mauriciotogneri.botcoin.wallet.Wallet;

import static com.mauriciotogneri.botcoin.util.Decimal.crypto;

public class BasicBuyStrategy implements BuyStrategy
{
    private float lastPrice = 0;
    private final float minAmountToSpend;
    private final Wallet wallet;

    public BasicBuyStrategy(Wallet wallet, float minAmountToSpend)
    {
        this.wallet = wallet;
        this.minAmountToSpend = minAmountToSpend;
    }

    @Override
    public float buy(float price)
    {
        lastPrice = price;

        if ((price < this.lastPrice) && (this.lastPrice >= 0))
        {
            float percentageDown = 100 - ((price * 100) / this.lastPrice);
            float amountToSpend = wallet.balanceTarget() * percentageDown / 100;

            if ((amountToSpend >= minAmountToSpend) && (wallet.balanceTarget() >= amountToSpend))
            {
                float btcToBuy = amountToSpend / price;

                return crypto(btcToBuy);
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }
    }
}