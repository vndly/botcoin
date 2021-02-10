package com.mauriciotogneri.botcoin.momo.complex;

import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ComplexSellStrategy
{
    private final BigDecimal MIN_PERCENTAGE_UP = new BigDecimal("0.01");
    private final BigDecimal minQuantity;

    public ComplexSellStrategy(BigDecimal minQuantity)
    {
        this.minQuantity = minQuantity;
    }

    public BigDecimal amount(@NotNull BigDecimal price,
                             BigDecimal boughtPrice,
                             Balance balanceA)
    {
        BigDecimal result = BigDecimal.ZERO;

        if ((price.compareTo(boughtPrice) > 0) && (boughtPrice.compareTo(BigDecimal.ZERO) > 0))
        {
            BigDecimal percentageUp = price.divide(boughtPrice, 10, RoundingMode.DOWN).subtract(BigDecimal.ONE);
            Log.console("Trying to sell at: %s/%s (+%s%%)", price, boughtPrice, percentageUp.multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN).toString());

            if (percentageUp.compareTo(MIN_PERCENTAGE_UP) >= 0)
            {
                BigDecimal amountToSell = balanceA.amount.setScale(balanceA.asset.step, RoundingMode.DOWN);

                if (amountToSell.compareTo(minQuantity) >= 0)
                {
                    result = amountToSell;
                }
            }
        }

        return result;
    }
}