package com.mauriciotogneri.botcoin.momo.complex;

import com.binance.api.client.domain.account.Account;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ComplexBuyStrategy
{
    private final BigDecimal MIN_PERCENTAGE_DOWN = new BigDecimal("0.01");
    private final BigDecimal minQuantity;

    public ComplexBuyStrategy(BigDecimal minQuantity)
    {
        this.minQuantity = minQuantity;
    }

    public BigDecimal amount(Symbol symbol,
                             @NotNull BigDecimal price,
                             BigDecimal limit,
                             BigDecimal percentageDown,
                             Balance balanceB)
    {
        BigDecimal result = BigDecimal.ZERO;

        if (price.compareTo(limit) < 0)
        {
            Log.console("[%s] Price diff: %s/%s (-%s%%)", symbol.name, price, limit, percentageDown.multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());

            if (percentageDown.compareTo(MIN_PERCENTAGE_DOWN) >= 0)
            {
                BigDecimal amountToBuy = minQuantity.multiply(new BigDecimal("10"));
                BigDecimal cost = amountToBuy.multiply(price);

                Account account = Binance.account();
                BigDecimal updatedBalanceB = Binance.balance(account, balanceB);

                if (updatedBalanceB.compareTo(cost) >= 0)
                {
                    result = amountToBuy;
                }
            }
        }

        return result;
    }
}