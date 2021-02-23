package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatusFile
{
    private final String path;

    public StatusFile(@NotNull Symbol symbol)
    {
        this.path = String.format("output/%s/status.properties", symbol.name);
    }

    public void save(@NotNull BigDecimal allTimeHigh,
                     @NotNull BigDecimal boughtPrice,
                     @NotNull BigDecimal currentPrice,
                     @NotNull BigDecimal percentage,
                     @NotNull Balance balanceA,
                     @NotNull Balance balanceB)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("allTimeHigh=%s%n", allTimeHigh.setScale(8, RoundingMode.DOWN).toString()));
        builder.append(String.format("boughtPrice=%s%n", boughtPrice.setScale(8, RoundingMode.DOWN).toString()));
        builder.append(String.format("currentPrice=%s%n", currentPrice.setScale(8, RoundingMode.DOWN).toString()));

        String percentageString = percentage.multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString();

        if (percentage.compareTo(BigDecimal.ZERO) >= 0)
        {
            builder.append(String.format("percentage=+%s%%%n", percentageString));
        }
        else
        {
            builder.append(String.format("percentage=%s%%%n", percentageString));
        }

        builder.append(balanceA.property("balanceA"));
        builder.append(balanceB.property("balanceB"));
        builder.append(String.format("timestamp=%s", System.currentTimeMillis()));

        Log log = new Log(path);
        log.write(builder.toString());
    }
}