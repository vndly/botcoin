package com.mauriciotogneri.botcoin.momo.complex;

import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ComplexBuyStrategy
{
    private final BigDecimal minPercentageDown;
    private BigDecimal allTimeHigh = BigDecimal.ZERO;

    public ComplexBuyStrategy(BigDecimal minPercentageDown)
    {
        this.minPercentageDown = minPercentageDown;
    }

    public void reset()
    {
        allTimeHigh = BigDecimal.ZERO;
    }

    public BigDecimal amount(@NotNull BigDecimal price,
                             @NotNull Balance balanceB)
    {
        BigDecimal result = BigDecimal.ZERO;

        if (price.compareTo(allTimeHigh) >= 0)
        {
            allTimeHigh = price;
            System.out.printf("New all time high: %s%n", allTimeHigh);
        }
        else if (price.compareTo(allTimeHigh) < 0)
        {

        }

        return result;
    }
}