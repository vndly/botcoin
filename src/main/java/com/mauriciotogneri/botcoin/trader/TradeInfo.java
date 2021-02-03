package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.account.Trade;

import org.jetbrains.annotations.NotNull;

public class TradeInfo
{
    public final double quantity;
    public final double price;

    public TradeInfo(double quantity, double price)
    {
        this.quantity = quantity;
        this.price = price;
    }

    public TradeInfo(@NotNull Trade trade)
    {
        this(Double.parseDouble(trade.getQty()), Double.parseDouble(trade.getPrice()));
    }
}