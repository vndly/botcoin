package com.mauriciotogneri.botcoin.mellau;

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
    private final Balance balanceA;
    private final Balance balanceB;
    private final Balance total;

    public LogEvent(String type,
                    Balance quantity,
                    Balance price,
                    Balance spent,
                    Balance gained,
                    Balance profit,
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
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.total = total;
    }

    @NotNull
    public static LogEvent buy(Balance quantity,
                               Balance price,
                               Balance spent,
                               Balance balanceA,
                               Balance balanceB,
                               Balance total)
    {
        return new LogEvent("buy", quantity, price, spent, null, null, balanceA, balanceB, total);
    }

    @NotNull
    public static LogEvent sell(Balance quantity,
                                Balance price,
                                Balance gained,
                                Balance profit,
                                Balance balanceA,
                                Balance balanceB,
                                Balance total)
    {
        return new LogEvent("sell", quantity, price, null, gained, profit, balanceA, balanceB, total);
    }
}
