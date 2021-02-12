package com.mauriciotogneri.botcoin.momo.complex;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComplexStrategy implements Strategy<Price>
{
    private final Symbol symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private final BigDecimal minQuantity;
    private final ComplexBuyStrategy buyStrategy;
    private final ComplexSellStrategy sellStrategy;

    private BigDecimal boughtPrice = BigDecimal.ZERO;
    private State state = State.BUYING;

    public ComplexStrategy(Symbol symbol,
                           Balance balanceA,
                           Balance balanceB,
                           BigDecimal minQuantity)
    {
        this.symbol = symbol;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.minQuantity = minQuantity;
        this.buyStrategy = new ComplexBuyStrategy(minQuantity);
        this.sellStrategy = new ComplexSellStrategy(minQuantity);
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        if (state == State.BUYING)
        {
            BigDecimal amount = buyStrategy.amount(symbol, price.value, balanceA, balanceB);

            if (amount.compareTo(minQuantity) > 0)
            {
                return Collections.singletonList(NewOrder.marketBuy(symbol.name, amount.toString()));
            }
        }
        else if (state == State.SELLING)
        {
            BigDecimal amount = sellStrategy.amount(symbol, price.value, boughtPrice, balanceA);

            if (amount.compareTo(minQuantity) > 0)
            {
                return Collections.singletonList(NewOrder.marketSell(symbol.name, amount.toString()));
            }
        }

        return new ArrayList<>();
    }

    @Override
    public List<Object> update(@NotNull List<OrderSent> sent)
    {
        List<Object> result = new ArrayList<>();

        for (OrderSent orderSent : sent)
        {
            Object event = process(orderSent.order, orderSent.response);
            result.add(event);
        }

        return result;
    }

    private Object process(@NotNull NewOrder order, NewOrderResponse response)
    {
        if (order.getSide() == OrderSide.BUY)
        {
            return buy(order, response);
        }
        else if (order.getSide() == OrderSide.SELL)
        {
            return sell(order, response);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @NotNull
    private Object buy(NewOrder order, @NotNull NewOrderResponse response)
    {
        if (response.getStatus() == OrderStatus.FILLED)
        {
            state = State.SELLING;

            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toSpend = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toSpend.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            Account account = Binance.account();
            balanceA.amount = Binance.balance(account, balanceA);
            balanceB.amount = Binance.balance(account, balanceB);

            //balanceA.amount.add(quantity);
            //balanceB.amount = balanceB.amount.subtract(toSpend);

            boughtPrice = price;

            return LogEvent.buy(
                    balanceA.of(quantity),
                    balanceB.of(price),
                    balanceB.of(toSpend),
                    balanceB.of(boughtPrice),
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );
        }
        else
        {
            return "ERROR";
        }
    }

    @NotNull
    private Object sell(NewOrder order, @NotNull NewOrderResponse response)
    {
        if (response.getStatus() == OrderStatus.FILLED)
        {
            state = State.BUYING;
            buyStrategy.reset();

            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toGain = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toGain.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice);
            BigDecimal profit = toGain.subtract(originalCost);

            Account account = Binance.account();
            balanceA.amount = Binance.balance(account, balanceA);
            balanceB.amount = Binance.balance(account, balanceB);

            //balanceA.amount = balanceA.amount.subtract(quantity);
            //balanceB.amount.add(toGain);

            return LogEvent.sell(
                    balanceA.of(quantity),
                    balanceB.of(price),
                    balanceB.of(toGain),
                    balanceB.of(profit),
                    balanceB.of(boughtPrice),
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );
        }
        else
        {
            return "ERROR";
        }
    }

    private Balance totalBalance(BigDecimal price)
    {
        return balanceB.of(balanceB.amount.add(balanceB.amount.multiply(price)).setScale(balanceB.asset.decimals, RoundingMode.DOWN));
    }

    private enum State
    {
        BUYING,
        SELLING
    }
}