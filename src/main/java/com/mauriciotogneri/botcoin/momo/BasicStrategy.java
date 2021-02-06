package com.mauriciotogneri.botcoin.momo;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                    String.valueOf(price.value)
            ));
        }
        else if (sellAmount > 0)
        {
            return Collections.singletonList(new NewOrder(
                    symbol,
                    OrderSide.SELL,
                    OrderType.MARKET,
                    TimeInForce.GTC,
                    String.valueOf(buyAmount),
                    String.valueOf(price.value)
            ));
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public JsonArray update(@NotNull List<NewOrderResponse> responses)
    {
        JsonArray array = new JsonArray();

        for (NewOrderResponse response : responses)
        {
            array.add(process(response));
        }

        return array;
    }

    private JsonObject process(@NotNull NewOrderResponse response)
    {
        if (response.getSide() == OrderSide.BUY)
        {
            return buy(response);
        }
        else if (response.getSide() == OrderSide.SELL)
        {
            return sell(response);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @NotNull
    private JsonObject buy(@NotNull NewOrderResponse response)
    {
        // TODO: check if filled
        double quantity = Double.parseDouble(response.getExecutedQty());
        double price = Double.parseDouble(response.getPrice());
        double toSpend = quantity * price;

        balanceA.amount -= toSpend;
        balanceB.amount += quantity;
        spent += toSpend;

        //console("OPERATION: BUY\n");
        //console("AMOUNT:    " + buyOperation.amount);
        //console("PRICE:     " + buyOperation.price);
        //console("SPENT:     " + buyOperation.spent);
        //balance(buyOperation.balanceA, buyOperation.balanceB, buyOperation.total);

        JsonObject json = new JsonObject();
        json.addProperty("type", "buy");
        json.add("amount", balanceB.of(quantity).json());
        json.add("price", balanceA.of(price).json());
        json.add("spent", balanceA.of(spent).json());
        json.add("balanceA", balanceA.json());
        json.add("balanceB", balanceB.json());
        json.add("total", totalBalance(price).json());

        return json;
    }

    @NotNull
    private JsonObject sell(@NotNull NewOrderResponse response)
    {
        // TODO: check if filled
        double quantity = Double.parseDouble(response.getExecutedQty());
        double price = Double.parseDouble(response.getPrice());
        double originalCost = quantity * boughtPrice();
        double toGain = quantity * price;
        double profit = toGain - originalCost;

        balanceA.amount += toGain;
        balanceB.amount -= quantity;
        spent -= originalCost;

        //console("OPERATION: SELL\n");
        //console("AMOUNT:    " + sellOperation.amount);
        //console("PRICE:     " + sellOperation.price);
        //console("GAINED:    " + sellOperation.gained);
        //console("PROFIT:    " + sellOperation.profit);
        //balance(sellOperation.balanceA, sellOperation.balanceB, sellOperation.total);

        JsonObject json = new JsonObject();
        json.addProperty("type", "sell");
        json.add("amount", balanceB.of(quantity).json());
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