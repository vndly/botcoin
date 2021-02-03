package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.domain.account.Trade;
import com.mauriciotogneri.botcoin.strategy.Intent;
import com.mauriciotogneri.botcoin.trader.TradeInfo;
import com.mauriciotogneri.botcoin.trader.Trader;

import org.jetbrains.annotations.NotNull;

public class BinanceTrader implements Trader
{
    @Override
    public TradeInfo buy(@NotNull Intent intent)
    {
        // TODO: do the trading

        Trade trade = new Trade();
        trade.setPrice(String.valueOf(intent.price));
        trade.setQty(String.valueOf(intent.quantity));

        return new TradeInfo(trade);
    }

    @Override
    public TradeInfo sell(@NotNull Intent intent)
    {
        // TODO: do the trading

        Trade trade = new Trade();
        trade.setPrice(String.valueOf(intent.price));
        trade.setQty(String.valueOf(intent.quantity));

        return new TradeInfo(trade);
    }
}