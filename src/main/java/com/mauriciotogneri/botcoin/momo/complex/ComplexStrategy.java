package com.mauriciotogneri.botcoin.momo.complex;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
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

    private BigDecimal spent = BigDecimal.ZERO;
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
        this.buyStrategy = new ComplexBuyStrategy();
        this.sellStrategy = new ComplexSellStrategy();
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        if (state == State.BUYING)
        {
            BigDecimal amount = buyStrategy.amount();

            if (amount.compareTo(minQuantity) > 0)
            {
                return Collections.singletonList(NewOrder.marketBuy(symbol.name, amount.toString()));
            }
        }
        else if (state == State.SELLING)
        {
            BigDecimal amount = sellStrategy.amount();

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
            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toSpend = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toSpend.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            balanceA.amount = balanceA.amount.subtract(toSpend);
            balanceB.amount = Binance.balance(balanceB.asset.currency);
            spent = spent.add(toSpend);

            return LogEvent.buy(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(spent),
                    balanceA.of(boughtPrice()),
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
            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toGain = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toGain.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice());
            BigDecimal profit = toGain.subtract(originalCost);

            balanceA.amount = Binance.balance(balanceA.asset.currency);
            balanceB.amount = balanceB.amount.subtract(quantity);
            spent = spent.subtract(originalCost);

            return LogEvent.sell(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(toGain),
                    balanceA.of(profit),
                    balanceA.of(boughtPrice()),
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

    private BigDecimal boughtPrice()
    {
        return (balanceB.amount.compareTo(BigDecimal.ZERO) > 0) ?
                spent.divide(balanceB.amount, balanceA.asset.decimals, RoundingMode.DOWN) :
                BigDecimal.ZERO;
    }

    private Balance totalBalance(BigDecimal price)
    {
        return balanceA.of(balanceA.amount.add(balanceB.amount.multiply(price)));
    }

    private enum State
    {
        BUYING,
        SELLING
    }
}