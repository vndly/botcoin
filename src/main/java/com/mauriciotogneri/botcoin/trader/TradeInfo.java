package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.account.Trade;

import org.jetbrains.annotations.NotNull;

public class TradeInfo
{
    public final double quantity;
    public final double price;

    public TradeInfo(@NotNull Trade trade)
    {
        this.quantity = Double.parseDouble(trade.getQty());
        this.price = Double.parseDouble(trade.getPrice());
    }
}