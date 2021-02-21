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
        Log balanceLog = new Log(balancePath(symbol));
        balanceLog.file(lastOperationProperties());
    }

    public static String balancePath(@NotNull Symbol symbol)
    {
        return String.format("output/%s/last_operation.properties", symbol.name);
    }

    @NotNull
    private String lastOperationProperties()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("type=%s%n", type));

        if (quantity != null)
        {
            builder.append(quantity.property("quantity"));
        }

        if (price != null)
        {
            builder.append(price.property("price"));
        }

        if (spent != null)
        {
            builder.append(spent.property("spent"));
        }

        if (gained != null)
        {
            builder.append(gained.property("gained"));
        }

        if (profit != null)
        {
            builder.append(profit.property("profit"));
        }

        if (boughtPrice != null)
        {
            builder.append(boughtPrice.property("boughtPrice"));
        }

        if (balanceA != null)
        {
            builder.append(balanceA.property("balanceA"));
        }

        if (balanceB != null)
        {
            builder.append(balanceB.property("balanceB"));
        }

        if (total != null)
        {
            builder.append(total.property("total"));
        }

        builder.append(String.format("timestamp=%s", System.currentTimeMillis()));

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