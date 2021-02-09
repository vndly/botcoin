package com.mauriciotogneri.botcoin.momo.complex;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComplexStrategy implements Strategy<Price>, BinanceApiCallback<OrderTradeUpdateEvent>
{
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;

    public ComplexStrategy(@NotNull Balance balanceA,
                           @NotNull Balance balanceB)
    {
        this.symbol = String.format("%s%s", balanceB.currency.name, balanceA.currency.name);
        this.balanceA = balanceA;
        this.balanceB = balanceB;

        Binance.onOrderTradeUpdateEvent(this);
    }

    @Override
    public void onResponse(OrderTradeUpdateEvent orderTradeUpdateEvent)
    {
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        return new ArrayList<>();
    }

    @Override
    public List<Object> update(@NotNull List<OrderSent> sent)
    {
        return new ArrayList<>();
    }
}