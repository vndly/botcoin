package com.mauriciotogneri.botcoin.mellau.basic;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.mellau.basic.dto.LastPricesAverageDTO;
import com.mauriciotogneri.botcoin.mellau.candle.dto.RequestDataDTO;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.util.Json;
import com.mauriciotogneri.botcoin.wallet.Balance;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class CrossStrategy implements Strategy<RequestDataDTO> {
    private BigDecimal spent = BigDecimal.ZERO;
    private String lostLimitOrderId;
    private BigDecimal oldLimit = BigDecimal.ZERO;
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;

    public CrossStrategy(@NotNull Balance balanceA,
                         @NotNull Balance balanceB) {
        this.symbol = String.format("%s%s", balanceB.currency.symbol, balanceA.currency.symbol);
        this.balanceA = balanceA;
        this.balanceB = balanceB;
    }

    @Override
    public List<NewOrder> orders(@NotNull RequestDataDTO requestDataDTO) {
        List<Candlestick> candlestickBars = requestDataDTO.candlestickBars;

        BigDecimal lastPrice = new BigDecimal(requestDataDTO.tickerPrice.getPrice());
        LastPricesAverageDTO lastPricesAverageDTO = new LastPricesAverageDTO();
        lastPricesAverageDTO.getAverages(candlestickBars);
        boolean shortIsUp = 0 < lastPricesAverageDTO.avgShort.compareTo(lastPricesAverageDTO.avgLong);

        LastPricesAverageDTO oldPricesAverageDTO = new LastPricesAverageDTO();
        oldPricesAverageDTO.getAverages(candlestickBars);
        boolean shortWasUp = 0 < oldPricesAverageDTO.avgShort.compareTo(oldPricesAverageDTO.avgLong);

        boolean possibleSell = 0 > boughtPrice().compareTo(lastPrice.multiply(new BigDecimal("1.005")));

        BigDecimal newLimit = lastPrice.multiply(new BigDecimal("0.999"));

        if (!shortWasUp && shortIsUp) {
            System.out.println("Short is up and now is down: ");
        } else if (!shortWasUp && shortIsUp) {
            System.out.println("Short is down and now is up: ");
        }

        if (!shortWasUp && shortIsUp && balanceA.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_EUR_TO_TRADE){
            System.out.println("---------------------- BUY -------------------------");
            System.out.println("Last Price: " + lastPrice.toString());

            oldLimit = new BigDecimal(0);
            lostLimitOrderId = "";

            List<NewOrder> newOrders = new ArrayList<>();
            newOrders.add(Binance.buyMarketOrder(symbol, balanceA.amount.multiply(lastPrice)));
            newOrders.add(Binance.limitSell(symbol, balanceB.amount.divide(lastPrice, balanceA.currency.decimals, RoundingMode.DOWN).toString(), newLimit.toString()));
            return newOrders;

        } else if (shortWasUp && possibleSell && balanceB.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_BTC_TO_TRADE){
            System.out.println("---------------------- SELL -------------------------");
            System.out.println("Last Price: " + lastPrice.toString());

            if (0 < newLimit.compareTo(oldLimit) && !isEmpty(lostLimitOrderId)) {
                System.out.println("New Limit: " + newLimit.toString());
                CancelOrderResponse response = Binance.cancelOrder(symbol, Long.parseLong(lostLimitOrderId));
                if (response.getStatus().equals(OrderStatus.CANCELED.toString())) {
                    return Collections.singletonList(Binance.limitSell(symbol, balanceB.amount.divide(lastPrice, balanceA.currency.decimals, RoundingMode.DOWN).toString(), newLimit.toString()));
                }
            }

            if (isEmpty(lostLimitOrderId)){
                System.out.println("Set Limit: " + newLimit.toString());
              return Collections.singletonList(Binance.limitSell(symbol, balanceB.amount.divide(lastPrice, balanceA.currency.decimals, RoundingMode.DOWN).toString(), newLimit.toString()));
            }
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

            if (order.getType() == OrderType.STOP_LOSS_LIMIT) {
                lostLimitOrderId = order.getNewClientOrderId();
            }

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
