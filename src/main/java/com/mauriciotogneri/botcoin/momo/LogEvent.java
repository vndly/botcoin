package com.mauriciotogneri.botcoin.momo;

import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

public class LogEvent
{
    private final String type;
    private final Balance quantity;
    private final Balance price;
    private final Balance spent;
    private final Balance gained;
    private final Balance profit;
    private final Balance boughtPrice;
    private final Balance balanceA;
    private final Balance balanceB;
    private final Balance total;

    public LogEvent(String type,
                    Balance quantity,
                    Balance price,
                    Balance spent,
                    Balance gained,
                    Balance profit,
                    Balance boughtPrice,
                    Balance balanceA,
                    Balance balanceB,
                    Balance total)
    {
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.spent = spent;
        this.gained = gained;
        this.profit = profit;
        this.boughtPrice = boughtPrice;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.total = total;
    }

    public void log(@NotNull Symbol symbol)
    {
        Log balanceLog = new Log(String.format("output/%s/balance.json", symbol.name));
        balanceLog.file(properties());
    }

    @NotNull
    private String properties()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("type=%s%n", type));
        builder.append(String.format("%s%n", (quantity != null) ? quantity.property() : "-"));
        builder.append(String.format("%s%n", (price != null) ? price.property() : "-"));
        builder.append(String.format("%s%n", (spent != null) ? spent.property() : "-"));
        builder.append(String.format("%s%n", (gained != null) ? gained.property() : "-"));
        builder.append(String.format("%s%n", (profit != null) ? profit.property() : "-"));
        builder.append(String.format("%s%n", (boughtPrice != null) ? boughtPrice.property() : "-"));
        builder.append(String.format("%s%n", (balanceA != null) ? balanceA.property() : "-"));
        builder.append(String.format("%s%n", (balanceB != null) ? balanceB.property() : "-"));
        builder.append(String.format("%s", (total != null) ? total.property() : "-"));

        return builder.toString();
    }

    @NotNull
    public static LogEvent buy(Balance quantity,
                               Balance price,
                               Balance spent,
                               Balance boughtPrice,
                               Balance balanceA,
                               Balance balanceB,
                               Balance total)
    {
        return new LogEvent("buy", quantity, price, spent, null, null, boughtPrice, balanceA, balanceB, total);
    }

    @NotNull
    public static LogEvent sell(Balance quantity,
                                Balance price,
                                Balance gained,
                                Balance profit,
                                Balance boughtPrice,
                                Balance balanceA,
                                Balance balanceB,
                                Balance total)
    {
        return new LogEvent("sell", quantity, price, null, gained, profit, boughtPrice, balanceA, balanceB, total);
    }
}