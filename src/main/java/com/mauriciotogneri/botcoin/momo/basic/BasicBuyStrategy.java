package com.mauriciotogneri.botcoin.momo.basic;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BasicBuyStrategy
{
    private BigDecimal allTimeHigh = BigDecimal.ZERO;
    private final BigDecimal minPercentageDown;
    private final BigDecimal percentageBuyMultiplier;
    private final BigDecimal minTradeAmountA;
    private final BigDecimal minTradeAmountB;

    public BasicBuyStrategy(String minPercentageDown,
                            String percentageBuyMultiplier,
                            String minTradeAmountA,
                            String minTradeAmountB)
    {
        this.minPercentageDown = new BigDecimal(minPercentageDown);
        this.percentageBuyMultiplier = new BigDecimal(percentageBuyMultiplier);
        this.minTradeAmountA = new BigDecimal(minTradeAmountA);
        this.minTradeAmountB = new BigDecimal(minTradeAmountB);
    }

    public BigDecimal buy(BigDecimal price,
                          @NotNull Balance balanceA,
                          @NotNull Balance balanceB,
                          BigDecimal boughtPrice)
    {
        BigDecimal result = BigDecimal.ZERO;

        if (balanceB.amount.compareTo(BigDecimal.ZERO) == 0) // first buy
        {
            if (price.compareTo(allTimeHigh) < 0)
            {
                System.out.printf("Trying first buy: %s/%s%n", price, allTimeHigh);
                result = byFrom(price, allTimeHigh, balanceA, balanceB);
            }
            else
            {
                allTimeHigh = price;
                System.out.printf("New all time high: %s%n", allTimeHigh);
            }
        }
        else if (price.compareTo(boughtPrice) < 0) // average down
        {
            System.out.printf("Trying Average down: %s/%s%n", price, boughtPrice);
            result = byFrom(price, boughtPrice, balanceA, balanceB);
        }

        return result;
    }

    private BigDecimal byFrom(@NotNull BigDecimal price,
                              BigDecimal limit,
                              @NotNull Balance balanceA,
                              @NotNull Balance balanceB)
    {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal percentageDown = BigDecimal.ONE.subtract(price.divide(limit, 10, RoundingMode.DOWN));

        if (percentageDown.compareTo(minPercentageDown) >= 0)
        {
            BigDecimal amountAToSpend = balanceA.amount.min(
                    balanceA.amount.multiply(percentageDown).multiply(percentageBuyMultiplier)
            );
            BigDecimal amountBToBuy = amountAToSpend
                    .divide(price, balanceB.asset.decimals, RoundingMode.DOWN)
                    .setScale(balanceB.asset.step, RoundingMode.DOWN);

            if ((amountAToSpend.compareTo(minTradeAmountA) >= 0) &&
                    (amountAToSpend.compareTo(balanceA.amount) <= 0) &&
                    (amountBToBuy.compareTo(minTradeAmountB) >= 0))
            {
                result = amountBToBuy;
            }
        }

        return result;
    }
}