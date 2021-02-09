package com.mauriciotogneri.botcoin.momo.basic;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.util.Json;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BasicStrategy implements Strategy<Price>
{
    private BigDecimal spent = BigDecimal.ZERO;
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private final BasicBuyStrategy buyStrategy;
    private final BasicSellStrategy sellStrategy;

    public BasicStrategy(@NotNull Balance balanceA,
                         @NotNull Balance balanceB,
                         String minPercentageDown,
                         String percentageBuyMultiplier,
                         String minPercentageUp,
                         String percentageSellMultiplier,
                         String sellAllLimit,
                         String minTradeAmountA,
                         String minTradeAmountB)
    {
        this.symbol = String.format("%s%s", balanceB.currency.symbol, balanceA.currency.symbol);
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
            return Collections.singletonList(Binance.buyMarketOrder(symbol, buyAmount));
        }
        else if (sellAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            return Collections.singletonList(Binance.sellMarketOrder(symbol, sellAmount));
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> update(@NotNull Map<NewOrder, NewOrderResponse> orders)
    {
        List<Object> result = new ArrayList<>();

        for (Entry<NewOrder, NewOrderResponse> entry : orders.entrySet())
        {
            NewOrder order = entry.getKey();
            NewOrderResponse response = entry.getValue();
            JsonObject event = process(order, response);

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
            BigDecimal price = toSpend.divide(quantity, balanceA.currency.decimals, RoundingMode.DOWN);

            balanceA.amount = balanceA.amount.subtract(toSpend);
            balanceB.amount = Binance.balance(balanceB.currency.symbol);
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
            BigDecimal price = toGain.divide(quantity, balanceA.currency.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice());
            BigDecimal profit = toGain.subtract(originalCost);

            balanceA.amount = Binance.balance(balanceA.currency.symbol);
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
                spent.divide(balanceB.amount, balanceA.currency.decimals, RoundingMode.DOWN) :
                BigDecimal.ZERO;
    }

    private Balance totalBalance(BigDecimal price)
    {
        return balanceA.of(balanceA.amount.add(balanceB.amount.multiply(price)));
    }
}