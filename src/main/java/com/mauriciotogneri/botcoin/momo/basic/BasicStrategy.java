package com.mauriciotogneri.botcoin.momo.basic;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.json.Json;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicStrategy implements Strategy<Price>
{
    private BigDecimal spent = BigDecimal.ZERO;
    private final Symbol symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private final BasicBuyStrategy buyStrategy;
    private final BasicSellStrategy sellStrategy;

    public BasicStrategy(Symbol symbol,
                         @NotNull Balance balanceA,
                         @NotNull Balance balanceB,
                         String minPercentageDown,
                         String percentageBuyMultiplier,
                         String minPercentageUp,
                         String percentageSellMultiplier,
                         String sellAllLimit,
                         String minTradeAmountA,
                         String minTradeAmountB)
    {
        this.symbol = symbol;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.buyStrategy = new BasicBuyStrategy(minPercentageDown, percentageBuyMultiplier, minTradeAmountA, minTradeAmountB);
        this.sellStrategy = new BasicSellStrategy(minPercentageUp, percentageSellMultiplier, sellAllLimit, minTradeAmountA, minTradeAmountB);
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        BigDecimal boughtPrice = boughtPrice();
        BigDecimal buyAmount = buyStrategy.buy(price.value, balanceA, balanceB, boughtPrice);
        BigDecimal sellAmount = sellStrategy.sell(price.value, balanceB, boughtPrice);

        if (buyAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            return Collections.singletonList(NewOrder.marketBuy(symbol.name, buyAmount.toString()));
        }
        else if (sellAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            return Collections.singletonList(NewOrder.marketSell(symbol.name, sellAmount.toString()));
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> update(@NotNull List<OrderSent> sent)
    {
        List<Object> result = new ArrayList<>();

        for (OrderSent orderSent : sent)
        {
            JsonObject event = process(orderSent.order, orderSent.response);
            result.add(event);
        }

        return result;
    }

    private JsonObject process(@NotNull NewOrder order, NewOrderResponse response)
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
    private JsonObject buy(NewOrder order, @NotNull NewOrderResponse response)
    {
        JsonObject json = new JsonObject();
        json.add("order", Json.toJsonObject(order));
        json.add("response", Json.toJsonObject(response));

        if (response.getStatus() == OrderStatus.FILLED)
        {
            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toSpend = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toSpend.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            balanceA.amount = balanceA.amount.subtract(toSpend);
            balanceB.amount = Binance.balance(Binance.account(), balanceB);
            spent = spent.add(toSpend);

            LogEvent logEvent = LogEvent.buy(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(spent),
                    balanceA.of(boughtPrice()),
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );
            json.add("custom", Json.toJsonObject(logEvent));
        }
        else
        {
            json.addProperty("custom", "error");
        }

        return json;
    }

    @NotNull
    private JsonObject sell(NewOrder order, @NotNull NewOrderResponse response)
    {
        JsonObject json = new JsonObject();
        json.add("order", Json.toJsonObject(order));
        json.add("response", Json.toJsonObject(response));

        if (response.getStatus() == OrderStatus.FILLED)
        {
            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toGain = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toGain.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice());
            BigDecimal profit = toGain.subtract(originalCost);

            balanceA.amount = Binance.balance(Binance.account(), balanceA);
            balanceB.amount = balanceB.amount.subtract(quantity);
            spent = spent.subtract(originalCost);

            LogEvent logEvent = LogEvent.sell(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(toGain),
                    balanceA.of(profit),
                    balanceA.of(boughtPrice()),
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );
            json.add("custom", Json.toJsonObject(logEvent));
        }
        else
        {
            json.addProperty("custom", "error");
        }

        return json;
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
}