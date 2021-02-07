package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BasicSellStrategy
{
    private final BigDecimal minPercentageUp;
    private final BigDecimal percentageSellMultiplier;
    private final BigDecimal sellAllLimit;
    private final BigDecimal minTradeAmountA;
    private final BigDecimal minTradeAmountB;

    public BasicSellStrategy(String minPercentageUp,
                             String percentageSellMultiplier,
                             String sellAllLimit,
                             String minTradeAmountA,
                             String minTradeAmountB)
    {
        this.minPercentageUp = new BigDecimal(minPercentageUp);
        this.percentageSellMultiplier = new BigDecimal(percentageSellMultiplier);
        this.sellAllLimit = new BigDecimal(sellAllLimit);
        this.minTradeAmountA = new BigDecimal(minTradeAmountA);
        this.minTradeAmountB = new BigDecimal(minTradeAmountB);
    }

    public BigDecimal sell(@NotNull BigDecimal price, @NotNull Balance balanceB, BigDecimal boughtPrice)
    {
        BigDecimal result = BigDecimal.ZERO;

        if ((price.compareTo(boughtPrice) > 0) && (boughtPrice.compareTo(BigDecimal.ZERO) > 0))
        {
            BigDecimal percentageUp = price.divide(boughtPrice, 10, RoundingMode.DOWN).subtract(BigDecimal.ONE);

            if (percentageUp.compareTo(minPercentageUp) >= 0)
            {
                BigDecimal amountBToSell;

                if (balanceB.amount.compareTo(sellAllLimit) <= 0)
                {
                    amountBToSell = balanceB.amount;
                }
                else
                {
                    amountBToSell = balanceB.amount.min(
                            balanceB.amount.multiply(percentageUp).multiply(percentageSellMultiplier)
                    );
                }

                BigDecimal amountAToGain = amountBToSell.multiply(price);

                if ((amountAToGain.compareTo(minTradeAmountA) >= 0) &&
                        (amountBToSell.compareTo(balanceB.amount) <= 0) &&
                        (amountBToSell.compareTo(minTradeAmountB) >= 0))
                {
                    result = amountBToSell;
                }
            }
        }

        return result;
    }
}