package com.mauriciotogneri.botcoin.momo.complex;

import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ComplexBuyStrategy
{
    private final BigDecimal MIN_PERCENTAGE_DOWN = new BigDecimal("0.01");
    private BigDecimal allTimeHigh = BigDecimal.ZERO;
    private final BigDecimal minQuantity;

    public ComplexBuyStrategy(BigDecimal minQuantity)
    {
        this.minQuantity = minQuantity;
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
            Log.console("New all time high: %s", allTimeHigh);
        }
        else if (price.compareTo(allTimeHigh) < 0)
        {
            BigDecimal percentageDown = BigDecimal.ONE.subtract(price.divide(allTimeHigh, 10, RoundingMode.DOWN));
            Log.console("Trying to buy at: %s/%s (+%s%%)", price, allTimeHigh, percentageDown.multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN).toString());

            if (percentageDown.compareTo(MIN_PERCENTAGE_DOWN) >= 0)
            {
                BigDecimal amountBToBuy = balanceB.amount.setScale(balanceB.asset.step, RoundingMode.DOWN);

                if (amountBToBuy.compareTo(minQuantity) >= 0)
                {
                    result = amountBToBuy;
                }
            }
        }

        return result;
    }
}