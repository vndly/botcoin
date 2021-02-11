package com.mauriciotogneri.botcoin.momo.complex;

import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
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
                             @NotNull Balance balanceA,
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

            if (percentageDown.compareTo(MIN_PERCENTAGE_DOWN) >= 0)
            {
                Log.console("Trying to buy at:  %s/%s (-%s%%)", price, allTimeHigh, percentageDown.multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());

                BigDecimal multiplier = percentageDown.multiply(new BigDecimal("10"));
                BigDecimal amountToSpend = balanceB.amount.min(balanceB.amount.multiply(multiplier));
                BigDecimal amountToBuy = amountToSpend.divide(price, balanceA.asset.step, RoundingMode.DOWN);

                if (amountToBuy.compareTo(minQuantity) >= 0)
                {
                    FakeTrader.LAST_PRICE = price;
                    result = amountToBuy;
                }
            }
        }

        return result;
    }
}