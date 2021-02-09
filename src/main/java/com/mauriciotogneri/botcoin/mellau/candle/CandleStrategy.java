package com.mauriciotogneri.botcoin.mellau.candle;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.momo.LogEvent;
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

public class CandleStrategy implements Strategy<List<Candlestick>> {
    private BigDecimal spent = BigDecimal.ZERO;
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;

    public CandleStrategy(@NotNull Balance balanceA,
                          @NotNull Balance balanceB) {
        this.symbol = String.format("%s%s", balanceB.currency.symbol, balanceA.currency.symbol);
        this.balanceA = balanceA;
        this.balanceB = balanceB;
    }

    @Override
    public List<NewOrder> orders(@NotNull List<Candlestick> price) {
        Candlestick lastCandlestick = price.get(price.size() - 1);
        boolean haveBalanceA = balanceA.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_EUR_TO_TRADE;
        boolean haveBalanceB = balanceB.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_BTC_TO_TRADE;

        boolean possibleSell = 0 > spent.compareTo(BigDecimal.valueOf(Integer.parseInt(lastCandlestick.getClose()) * 1.0025));

        boolean haveBigVolume = Integer.parseInt(lastCandlestick.getVolume()) > 350; // 500 for ETH
        boolean haveMassiveVolume = Integer.parseInt(lastCandlestick.getVolume()) > 600; // 1000 for ETH
        boolean isRedCandle = (Integer.parseInt(lastCandlestick.getClose()) * 1.0025) < Integer.parseInt(lastCandlestick.getOpen());
        boolean isRedLowPrice = (Integer.parseInt(lastCandlestick.getLow()) * 1.0025) < Integer.parseInt(lastCandlestick.getClose());
        boolean isRedBigCandle = (Integer.parseInt(lastCandlestick.getClose()) * 1.006) < Integer.parseInt(lastCandlestick.getOpen());
        boolean isRedBigLowPrice = (Integer.parseInt(lastCandlestick.getLow()) * 1.006) < Integer.parseInt(lastCandlestick.getClose());

        // Checks that last 4 ticks didn't pump a 2% or more
        boolean comeFromPick = Integer.parseInt(price.get(price.size() - 4).getOpen()) < (Integer.parseInt(lastCandlestick.getOpen()) * 0.98);

        if (haveBalanceA && !comeFromPick && (haveBigVolume && isRedCandle && isRedLowPrice || (haveMassiveVolume && (isRedBigCandle || isRedBigLowPrice)))) {
            // Available to buy
            return Collections.singletonList(
                    Binance.buyMarketOrder(
                            symbol,
                            balanceA.amount.multiply(new BigDecimal(lastCandlestick.getClose()))));

        } else if (haveBalanceB && possibleSell) {
            // Available to sell

            return Collections.singletonList(
                    Binance.sellMarketOrder(
                            symbol,
                            balanceB.amount.divide(new BigDecimal(lastCandlestick.getClose()),
                                    balanceA.currency.decimals,
                                    RoundingMode.DOWN)));
        }

        return new ArrayList<>();
    }

    @Override
    public List<Object> update(@NotNull Map<NewOrder, NewOrderResponse> orders) {
        List<Object> result = new ArrayList<>();

        for (Entry<NewOrder, NewOrderResponse> entry : orders.entrySet()) {
            NewOrder order = entry.getKey();
            NewOrderResponse response = entry.getValue();
            JsonObject event = process(order, response);

            result.add(event);
        }

        return result;
    }

    private JsonObject process(@NotNull NewOrder order, NewOrderResponse response) {
        if (order.getSide() == OrderSide.BUY) {
            return buy(order, response);
        } else if (order.getSide() == OrderSide.SELL) {
            return sell(order, response);
        } else {
            throw new RuntimeException();
        }
    }

    @NotNull
    private JsonObject buy(NewOrder order, @NotNull NewOrderResponse response) {
        JsonObject json = new JsonObject();
        json.add("order", Json.toJsonObject(order));
        json.add("response", Json.toJsonObject(response));

        if (response.getStatus() == OrderStatus.FILLED) {
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
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );
            json.add("custom", Json.toJsonObject(logEvent));
        } else {
            json.addProperty("custom", "error");
        }

        return json;
    }

    @NotNull
    private JsonObject sell(NewOrder order, @NotNull NewOrderResponse response) {
        JsonObject json = new JsonObject();
        json.add("order", Json.toJsonObject(order));
        json.add("response", Json.toJsonObject(response));

        if (response.getStatus() == OrderStatus.FILLED) {
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
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );
            json.add("custom", Json.toJsonObject(logEvent));
        } else {
            json.addProperty("custom", "error");
        }

        return json;
    }

    private BigDecimal boughtPrice() {
        return (balanceB.amount.compareTo(BigDecimal.ZERO) > 0) ?
                spent.divide(balanceB.amount, balanceA.currency.decimals, RoundingMode.DOWN) :
                BigDecimal.ZERO;
    }

    private Balance totalBalance(BigDecimal price) {
        return balanceA.of(balanceA.amount.add(balanceB.amount.multiply(price)));
    }
}
