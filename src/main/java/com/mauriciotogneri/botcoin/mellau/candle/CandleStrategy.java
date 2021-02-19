package com.mauriciotogneri.botcoin.mellau.candle;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.DataProviderSleepTime;
import com.mauriciotogneri.botcoin.json.Json;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.mellau.basic.dto.LastPricesAverageDTO;
import com.mauriciotogneri.botcoin.mellau.candle.dto.RequestDataDTO;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.wallet.Balance;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class CandleStrategy implements Strategy<RequestDataDTO> {
    private BigDecimal spent = BigDecimal.ZERO;
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private final DataProviderSleepTime dataProviderSleepTime;
    private String lostLimitOrderId;
    private BigDecimal oldLimit = BigDecimal.ZERO;

    public CandleStrategy(@NotNull Balance balanceA,
                          @NotNull Balance balanceB,
                          Symbol symbol,
                          DataProviderSleepTime dataProviderSleepTime) {
        this.symbol = symbol.toString();
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.dataProviderSleepTime = dataProviderSleepTime;
    }

    @Override
    public List<NewOrder> orders(@NotNull RequestDataDTO requestDataDTO) {
        List<Candlestick> candlestickBars = requestDataDTO.candlestickBars;
        TickerPrice price = requestDataDTO.tickerPrice;
        Candlestick lastCandlestick = candlestickBars.get(candlestickBars.size() - 1);
        BigDecimal lastPrice = new BigDecimal(requestDataDTO.tickerPrice.getPrice());
        BigDecimal newLimit = lastPrice.multiply(new BigDecimal("0.999"));

        boolean haveBalanceA = balanceA.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_EUR_TO_TRADE;
        boolean haveBalanceB = balanceB.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_BTC_TO_TRADE;

        //boolean possibleSell = 0 > boughtPrice().compareTo(BigDecimal.valueOf(Double.parseDouble(price.getPrice()) * 1.005));
        boolean possibleSell = 0 > boughtPrice().compareTo(new BigDecimal(price.getPrice()).multiply(new BigDecimal("1.005")));

        // Check witch kind of candle is it
        boolean haveBigVolume = Double.parseDouble(lastCandlestick.getVolume()) > 250; // 500 for ETH
        boolean haveMassiveVolume = Double.parseDouble(lastCandlestick.getVolume()) > 450; // 1000 for ETH
        boolean isRedCandle = (Double.parseDouble(lastCandlestick.getClose()) * 1.004) < Double.parseDouble(lastCandlestick.getOpen());
        boolean isRedLowPrice = (Double.parseDouble(lastCandlestick.getLow()) * 1.003) < Double.parseDouble(lastCandlestick.getClose());
        boolean isRedBigCandle = (Double.parseDouble(lastCandlestick.getClose()) * 1.008) < Double.parseDouble(lastCandlestick.getOpen());
        boolean isRedBigLowPrice = (Double.parseDouble(lastCandlestick.getLow()) * 1.006) < Double.parseDouble(lastCandlestick.getClose());

        boolean actualPriceNearCloseCandle = Double.parseDouble(price.getPrice()) < Double.parseDouble(lastCandlestick.getClose()) * 1.001;

        // Checks that last 4 ticks didn't pump a 2% or more
        boolean comeFromPick = Double.parseDouble(candlestickBars.get(candlestickBars.size() - 4).getOpen()) < (Double.parseDouble(lastCandlestick.getOpen()) * 0.98);

        if (haveBalanceA && actualPriceNearCloseCandle && !comeFromPick && (haveBigVolume && isRedCandle && isRedLowPrice || (haveMassiveVolume && (isRedBigCandle || isRedBigLowPrice)))) {
            System.out.println("---------------------- BUY -------------------------");
            System.out.println("Last Price: " + lastPrice.toString());

            oldLimit = new BigDecimal(0);
            lostLimitOrderId = "";

            List<NewOrder> newOrders = new ArrayList<>();
            newOrders.add(NewOrder.marketBuy(symbol, balanceA.amount.multiply(lastPrice).toString()));
            newOrders.add(NewOrder.limitSell(symbol, TimeInForce.GTC,  balanceB.amount.divide(lastPrice, balanceA.asset.decimals, RoundingMode.DOWN).toString(), newLimit.toString()));
            return newOrders;

        } else if (haveBalanceB && possibleSell) { // Sells as soon as made 0.5&
            System.out.println("---------------------- SELL -------------------------");
            System.out.println("Last Price: " + lastPrice.toString());

            if (0 < newLimit.compareTo(oldLimit) && !isEmpty(lostLimitOrderId)) {
                System.out.println("New Limit: " + newLimit.toString());
                CancelOrderResponse response = Binance.cancelOrder(symbol, Long.parseLong(lostLimitOrderId));
                if (response.getStatus().equals(OrderStatus.CANCELED.toString())) {
                    return Collections.singletonList(NewOrder.limitSell(symbol, TimeInForce.GTC, balanceB.amount.divide(lastPrice, balanceA.asset.decimals, RoundingMode.DOWN).toString(), newLimit.toString()));
                }
            }

            if (isEmpty(lostLimitOrderId)){
                System.out.println("Set Limit: " + newLimit.toString());
                return Collections.singletonList(NewOrder.limitSell(symbol, TimeInForce.GTC, balanceB.amount.divide(lastPrice, balanceA.asset.decimals, RoundingMode.DOWN).toString(), newLimit.toString()));
            }
            /*
            return Collections.singletonList(
                    NewOrder.marketSell(
                            symbol,
                            balanceB.amount.divide(new BigDecimal(price.getPrice()),
                                    balanceA.asset.decimals,
                                    RoundingMode.DOWN).toString()));*/
        }

        return new ArrayList<>();
    }

    @Override
    public List<Object> update(@NotNull List<OrderSent> sent) {
        List<Object> result = new ArrayList<>();

        for (OrderSent orderSent : sent) {
            JsonObject event = process(orderSent.order, orderSent.response);

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
            BigDecimal price = toSpend.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            Account account = Binance.account();
            balanceA.amount = balanceA.amount.subtract(toSpend);
            balanceB.amount = Binance.balance(account, balanceB.asset);
            spent = spent.add(toSpend);

            dataProviderSleepTime.value = 3 * 1000;

            LogEvent logEvent = LogEvent.buy(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(spent),
                    balanceB.of(boughtPrice()),
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
            BigDecimal price = toGain.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice());
            BigDecimal profit = toGain.subtract(originalCost);
             Account account = Binance.account();
            balanceA.amount = Binance.balance(account, balanceA.asset);
            balanceB.amount = balanceB.amount.subtract(quantity);
            spent = spent.subtract(originalCost);

            dataProviderSleepTime.value = 60 * 1000;

            LogEvent logEvent = LogEvent.sell(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(toGain),
                    balanceA.of(profit),
                    balanceB.of(boughtPrice()),
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
                spent.divide(balanceB.amount, balanceA.asset.decimals, RoundingMode.DOWN) :
                BigDecimal.ZERO;
    }

    private Balance totalBalance(BigDecimal price) {
        return balanceA.of(balanceA.amount.add(balanceB.amount.multiply(price)));
    }
}
