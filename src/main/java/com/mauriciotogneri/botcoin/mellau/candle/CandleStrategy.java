package com.mauriciotogneri.botcoin.mellau.candle;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.DataProviderSleepTime;
import com.mauriciotogneri.botcoin.json.Json;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.mellau.candle.dto.LastPricesAverageDTO;
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

public class CandleStrategy implements Strategy<RequestDataDTO> {
    private BigDecimal spent = BigDecimal.ZERO;
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;
    private final DataProviderSleepTime dataProviderSleepTime;
    private BigDecimal stopLimit = BigDecimal.ZERO;

    public CandleStrategy(@NotNull Balance balanceA,
                          @NotNull Balance balanceB,
                          Symbol symbol,
                          DataProviderSleepTime dataProviderSleepTime) {
        this.symbol = symbol.name;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.dataProviderSleepTime = dataProviderSleepTime;
    }

    @Override
    public List<NewOrder> orders(@NotNull RequestDataDTO requestDataDTO) {
        /** GET DATA **/
        List<Candlestick> candlestickBars = requestDataDTO.candlestickBars;

        Candlestick lastCandlestick = candlestickBars.get(candlestickBars.size() - 1);

        TickerPrice lastTickerPrice = requestDataDTO.tickerPrice;

        BigDecimal lastPrice = new BigDecimal(requestDataDTO.tickerPrice.getPrice());
        /** GET DATA **/

        boolean haveBalanceA = balanceA.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_EUR_TO_TRADE;
        boolean haveBalanceB = balanceB.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_BTC_TO_TRADE;

        /** COMPUTE DATA **/
        // Get avg volume of last 4 candles
        Integer oldCandlestickVolumeAvg = getAvgVolumeLastFiveMin(candlestickBars);

        // Sell if price is smaller than bought price -0.5%
        boolean sellAtLost = 0 > new BigDecimal(lastTickerPrice.getPrice()).compareTo(boughtPrice().multiply(new BigDecimal("0.995")));

        // Sell if price is smaller than stopLimit but higher than boughtPrice
        boolean sellWithWin = 0 > new BigDecimal(lastTickerPrice.getPrice()).compareTo(stopLimit) &&
                                0 < new BigDecimal(lastTickerPrice.getPrice()).compareTo(boughtPrice().multiply(new BigDecimal("1.003")));

        // Check if need to upgrade the limitSell
        boolean needBiggerLimit = 0 > stopLimit.compareTo(new BigDecimal(lastTickerPrice.getPrice()).multiply(new BigDecimal("0.998")));

        // Check witch kind of candle is it
        boolean haveGoodVolume = Double.parseDouble(lastCandlestick.getVolume()) > oldCandlestickVolumeAvg * 20;
        boolean haveBigVolume = Double.parseDouble(lastCandlestick.getVolume()) > oldCandlestickVolumeAvg * 60;
        boolean isRedCandle = (Double.parseDouble(lastCandlestick.getClose()) * 1.004) < Double.parseDouble(lastCandlestick.getOpen());
        boolean isRedBigCandle = (Double.parseDouble(lastCandlestick.getClose()) * 1.008) < Double.parseDouble(lastCandlestick.getOpen());
        boolean isRedLowPrice = (Double.parseDouble(lastCandlestick.getLow()) * 1.003) < Double.parseDouble(lastCandlestick.getClose());
        boolean isRedBigLowPrice = (Double.parseDouble(lastCandlestick.getLow()) * 1.006) < Double.parseDouble(lastCandlestick.getClose());

        // Is a red candle that we can take advantage from
        boolean spotCandle = (haveBigVolume && isRedCandle && isRedLowPrice || (haveGoodVolume && (isRedBigCandle || isRedBigLowPrice)));

        // Check we are not at the half of next candle
        boolean actualPriceIsNearPastCloseCandlePrice = Double.parseDouble(lastTickerPrice.getPrice()) < Double.parseDouble(lastCandlestick.getClose()) * 1.0001;

        // Checks that last 4 candles didn't pump a 2% or more
        boolean comeFromPick = Double.parseDouble(candlestickBars.get(candlestickBars.size() - 4).getOpen()) < (Double.parseDouble(lastCandlestick.getOpen()) * 0.98);
        /** COMPUTE DATA **/

        /** AVERAGE STRATEGY **/
        LastPricesAverageDTO lastPricesAverageDTO = new LastPricesAverageDTO();
        lastPricesAverageDTO.getAverages(candlestickBars);
        boolean shortIsUp = 0 < lastPricesAverageDTO.avgShort.compareTo(lastPricesAverageDTO.avgLong);

        LastPricesAverageDTO oldPricesAverageDTO = new LastPricesAverageDTO();
        oldPricesAverageDTO.getOldAverages(candlestickBars);
        boolean shortWasUp = 0 < oldPricesAverageDTO.avgShort.compareTo(oldPricesAverageDTO.avgLong);
        /** AVERAGE STRATEGY **/

        System.out.println("---------------------- STATUS -------------------------");
        System.out.println("Last Price: " + lastPrice.toString());
        System.out.println("1: " + haveBalanceA + " - " + haveBalanceB);
        System.out.println("2: " + shortWasUp + " - " + shortIsUp);

        /** LOGIC **/
        if ((haveBalanceA && actualPriceIsNearPastCloseCandlePrice && !comeFromPick && spotCandle) ||
                (!shortWasUp && shortIsUp && haveBalanceA)) {
            System.out.println("---------------------- BUY -------------------------");
            System.out.println("Last Price: " + lastPrice.toString());

            stopLimit = lastPrice.multiply(new BigDecimal("0.998"));

            // TODO: this is wrong.... i need to change BNB to BTC and with the good quantity
            return Collections.singletonList(
                    NewOrder.marketSell(
                            symbol,
                            balanceA.amount.divide(new BigDecimal(lastTickerPrice.getPrice()),
                                    balanceB.asset.decimals,
                                    RoundingMode.DOWN).toString()));

        } else if (haveBalanceB && (sellAtLost || sellWithWin)) {
            System.out.println("---------------------- SELL -------------------------");
            System.out.println("Last Price: " + lastPrice.toString());

            // TODO: this is wrong.... i need to change BTC to BNB and with the good quantity
            return Collections.singletonList(
                    NewOrder.marketBuy(
                            symbol,
                            balanceB
                                    .amount
                                    .multiply(new BigDecimal("0.998"))
                                    .divide(lastPrice, balanceA.asset.step, RoundingMode.DOWN)
                                    .toString()));

        } else if (haveBalanceB &&  needBiggerLimit) {
            System.out.println("---------------------- INCREASE STOP LIMIT -------------------------");
            System.out.println("From: " + stopLimit + " To: " + lastPrice.multiply(new BigDecimal("0.995")).toString());
            stopLimit = lastPrice.multiply(new BigDecimal("0.995"));
        }
        /** LOGIC **/

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
            balanceA.amount = Binance.balance(account, balanceA.asset).multiply(new BigDecimal("0.15"));
            balanceB.amount = Binance.balance(account, balanceB.asset);
            spent = spent.add(toSpend);

            dataProviderSleepTime.value = 3 * 1000;

            LogEvent logEvent = LogEvent.buy(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(spent),
                    balanceA.of(price),
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
            balanceA.amount = Binance.balance(account, balanceA.asset).multiply(new BigDecimal("0.15"));
            balanceB.amount = Binance.balance(account, balanceB.asset);

            spent = spent.subtract(originalCost);

            dataProviderSleepTime.value = 60 * 1000;

            LogEvent logEvent = LogEvent.sell(
                    balanceB.of(quantity),
                    balanceA.of(price),
                    balanceA.of(toGain),
                    balanceA.of(profit),
                    balanceA.of(price),
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

    private Integer getAvgVolumeLastFiveMin(List<Candlestick> candlestickBars) {
        Integer avgVolumeLastFiveMin = 0;
        for (int y = 1; y <= ConfigConst.NUMBER_OF_CANDLES_TO_LOOK_BACK; y++) {
            avgVolumeLastFiveMin += (int) Double.parseDouble(candlestickBars.get(candlestickBars.size() - 1 -y).getVolume());
        }
        return avgVolumeLastFiveMin / ConfigConst.NUMBER_OF_CANDLES_TO_LOOK_BACK;
    }
}
