package com.mauriciotogneri.botcoin.momo.complex;

import com.binance.api.client.domain.account.NewOrder;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ComplexStrategy implements Strategy<Price>
{
    private final Symbol symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private final BigDecimal minQuantity;

    public ComplexStrategy(@NotNull Symbol symbol,
                           @NotNull Balance balanceA,
                           @NotNull Balance balanceB,
                           BigDecimal minQuantity)
    {
        this.symbol = symbol;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.minQuantity = minQuantity;
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