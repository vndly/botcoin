package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.market.Symbol;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ProfitFile
{
    private final String path;
    private BigDecimal total = BigDecimal.ZERO;

    public ProfitFile(@NotNull Symbol symbol)
    {
        this.path = String.format("output/%s/profit.txt", symbol.name);
    }

    public void save(@NotNull BigDecimal value)
    {
        total = total.add(value);

        Log log = new Log(path);
        log.write(total.toString());
    }
}