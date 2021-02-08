package com.mauriciotogneri.botcoin.mellau.basic;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.mellau.LogEvent;
import com.mauriciotogneri.botcoin.mellau.basic.dto.LastPricesAverageDTO;
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

public class CrossStrategy implements Strategy<List<Candlestick>>
{
    private BigDecimal spent = BigDecimal.ZERO;
    private final String symbol;
    private final Balance balanceA;
    private final Balance balanceB;

    public CrossStrategy(@NotNull Balance balanceA,
                         @NotNull Balance balanceB)
    {
        this.symbol = String.format("%s%s", balanceB.currency.symbol, balanceA.currency.symbol);
        this.balanceA = balanceA;
        this.balanceB = balanceB;
    }

    @Override
    public List<NewOrder> orders(@NotNull List<Candlestick> price) {
        // TODO: use info
        //  private Long openTime;
        //  private String open;
        //  private String high;
        //  private String low;
        //  private String close;
        //  private String volume;
        //  private Long closeTime;
        //  private String quoteAssetVolume;
        //  private Long numberOfTrades;
        //  private String takerBuyBaseAssetVolume;
        //  private String takerBuyQuoteAssetVolume;

        // TODO: example:
        //  if there is a lot of volume + numberOfTrades and price is going down is a signal will go down.
        //  IF close is equal than high or really close means is going up if close is lower equal low or really close means is going down


        /**
         *
         * What is a hihgt volume? 150/200 is really big
         * what is a gight going down?  0.4% is remarcable
         *  Theory1:
         *  when there is a big red candle, where low is lower than close by 0.3% or more and there is massive volume, then it will go up next tick
         *
         * take in care that if the going dow is really big and not that much volume is good as avarage going dow and avarage volume
         * the same aplies to hihgt volume and not so much going down
         *
         *  Aproach, bigger is the bolume and % of going down more clear is it that there will be a going up, so more you buy
         *
         * Atention, there is need to create a secure point to make sure to don't sell when loosing
         *
         * --------------------------This is the way--------------------------
         * Wait for the big candles (volume 250+ arround 450+ is nice and - 0.6% down)
         * i'v seen a 1% going down and volume 460
         * i'v seen a 2% going down and volume 1100 and then going up a 1%
         * Important to notice, closing price have to be arround the half between open and low!!         *
         * making sure that will go up
         *
         *
         *
         *
         *
         * What to maybe do: get last candle and then as soon as you decide to buy next candle take last price and if it's hihgt enough sell
         *
         * It's is possible that you have to wait the hight volume after the buy?
         *
         */
        BigDecimal lastPrice = new BigDecimal(price.get(price.size() - 1).getClose());
        LastPricesAverageDTO lastPricesAverageDTO = new LastPricesAverageDTO();
        lastPricesAverageDTO.getAverages(price);
        boolean shortIsUp = 0 < lastPricesAverageDTO.avgShort.compareTo(lastPricesAverageDTO.avgLong);

        LastPricesAverageDTO oldPricesAverageDTO = new LastPricesAverageDTO();
        oldPricesAverageDTO.getAverages(price);
        boolean shortWasUp = 0 < oldPricesAverageDTO.avgShort.compareTo(oldPricesAverageDTO.avgLong);

        if (!shortWasUp && shortIsUp && balanceA.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_EUR_TO_TRADE){
            return Collections.singletonList(Binance.buyMarketOrder(symbol, balanceA.amount.multiply(lastPrice)));
        } else if (shortWasUp && !shortIsUp && balanceB.amount.compareTo(BigDecimal.ZERO) > ConfigConst.MIN_BTC_TO_TRADE){
            // TODO: only sell if price > than paid price * 1.001 or pice is 50%
            return Collections.singletonList(Binance.sellMarketOrder(symbol, balanceB.amount.divide(lastPrice, balanceA.currency.decimals, RoundingMode.DOWN)));
            // TODO: Save bought price
        }



        if (price.get(price.size() - 1).getVolume() )
        return new ArrayList<>();
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
            return buyPostProcess(order, response);
        }
        else if (order.getSide() == OrderSide.SELL)
        {
            return sellPostProcess(order, response);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @NotNull
    private JsonObject buyPostProcess(NewOrder order, @NotNull NewOrderResponse response)
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
            balanceB.amount = balanceB.amount.add(quantity);
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
        }
        else
        {
            json.addProperty("custom", "error");
        }

        return json;
    }

    @NotNull
    private JsonObject sellPostProcess(NewOrder order, @NotNull NewOrderResponse response)
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

            balanceA.amount = balanceA.amount.add(toGain);
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
