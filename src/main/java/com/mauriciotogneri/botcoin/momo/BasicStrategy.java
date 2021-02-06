package com.mauriciotogneri.botcoin.momo;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BasicStrategy implements Strategy<Price>
{
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private double spent;
    private final BasicBuyStrategy buyStrategy;
    private final BasicSellStrategy sellStrategy;

    public BasicStrategy(@NotNull Balance balanceA,
                         @NotNull Balance balanceB,
                         BasicBuyStrategy buyStrategy,
                         BasicSellStrategy sellStrategy)
    {
        this.symbol = String.format("%s%s", balanceB.currency.symbol, balanceA.currency.symbol);
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        double boughtPrice = boughtPrice();
        double buyAmount = buyStrategy.buy(price.value, balanceA, balanceB, boughtPrice);
        double sellAmount = sellStrategy.sell(price.value, balanceB, boughtPrice);

        if (buyAmount > 0)
        {
            return Collections.singletonList(new NewOrder(
                    symbol,
                    OrderSide.BUY,
                    OrderType.MARKET,
                    TimeInForce.GTC,
                    String.valueOf(buyAmount),
                    String.valueOf(price.value) // TODO: remove?
            ));
        }
        else if (sellAmount > 0)
        {
            return Collections.singletonList(new NewOrder(
                    symbol,
                    OrderSide.SELL,
                    OrderType.MARKET,
                    TimeInForce.GTC,
                    String.valueOf(sellAmount),
                    String.valueOf(price.value) // TODO: remove?
            ));
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
        // TODO: check if filled
        double quantity = Double.parseDouble(response.getExecutedQty());
        double price = Double.parseDouble(response.getPrice());
        double toSpend = balanceA.formatAmount(quantity * price);

        balanceA.amount -= toSpend;
        balanceB.amount += quantity;
        spent += toSpend;

        JsonObject json = new JsonObject();
        json.addProperty("type", "buy");
        json.add("quantity", balanceB.of(quantity).json());
        json.add("price", balanceA.of(price).json());
        json.add("spent", balanceA.of(spent).json());
        json.add("balanceA", balanceA.json());
        json.add("balanceB", balanceB.json());
        json.add("total", totalBalance(price).json());

        return json;
    }

    @NotNull
    private JsonObject sell(NewOrder order, @NotNull NewOrderResponse response)
    {
        // TODO: check if filled
        double quantity = Double.parseDouble(response.getExecutedQty());
        double price = Double.parseDouble(response.getPrice());
        double toGain = balanceA.formatAmount(quantity * price);
        double originalCost = balanceA.formatAmount(quantity * boughtPrice());
        double profit = balanceA.formatAmount(toGain - originalCost);

        balanceA.amount += toGain;
        balanceB.amount -= quantity;
        spent -= originalCost;

        JsonObject json = new JsonObject();
        json.addProperty("type", "sell");
        json.add("quantity", balanceB.of(quantity).json());
        json.add("price", balanceA.of(price).json());
        json.add("gained", balanceA.of(toGain).json());
        json.add("profit", balanceA.of(profit).json());
        json.add("balanceA", balanceA.json());
        json.add("balanceB", balanceB.json());
        json.add("total", totalBalance(price).json());

        return json;
    }

    public double boughtPrice()
    {
        return (balanceB.amount > 0) ? (spent / balanceB.amount) : 0;
    }

    public Balance totalBalance(double price)
    {
        return balanceA.of(balanceA.amount + (balanceB.amount * price));
    }
}