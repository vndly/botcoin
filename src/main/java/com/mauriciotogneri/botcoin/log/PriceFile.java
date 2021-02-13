package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.complex.ComplexStrategy.State;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class PriceFile
{
    private final String path;

    public PriceFile(@NotNull Symbol symbol)
    {
        this.path = String.format("output/%s/price.properties", symbol.name);
    }

    public void save(@NotNull State state,
                     @NotNull BigDecimal allTimeHigh,
                     @NotNull BigDecimal boughtPrice,
                     @NotNull BigDecimal currentPrice,
                     @NotNull BigDecimal percentage)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("state=%s%n", state.name()));
        builder.append(String.format("allTimeHigh=%s%n", allTimeHigh.toString()));
        builder.append(String.format("boughtPrice=%s%n", boughtPrice.toString()));
        builder.append(String.format("currentPrice=%s%n", currentPrice.toString()));

        if (percentage.compareTo(BigDecimal.ZERO) >= 0)
        {
            builder.append(String.format("percentage=+%s%%", percentage.toString()));
        }
        else
        {
            builder.append(String.format("percentage=-%s%%", percentage.toString()));
        }

        Log log = new Log(path);
        log.write(builder.toString());
    }
}