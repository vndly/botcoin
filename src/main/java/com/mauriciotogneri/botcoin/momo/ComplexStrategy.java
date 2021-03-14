package com.mauriciotogneri.botcoin.momo;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.app.Botcoin;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.json.Json;
import com.mauriciotogneri.botcoin.log.ConfigFile;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.log.ProfitFile;
import com.mauriciotogneri.botcoin.log.StatusFile;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
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
    private final ConfigFile configFile;
    private final ProfitFile profitFile;
    private final StatusFile statusFile;

    private BigDecimal allTimeHigh = BigDecimal.ZERO;
    private BigDecimal amountSpent;
    private BigDecimal amountBought;
    private BigDecimal sellHighLimit = BigDecimal.ZERO;
    private BigDecimal sellLowLimit = BigDecimal.ZERO;
    private BigDecimal lastBoughtPrice;

    private final BigDecimal MIN_PERCENTAGE_DOWN = new BigDecimal("0.01");
    private final BigDecimal MIN_PERCENTAGE_UP = new BigDecimal("0.01");

    public ComplexStrategy(@NotNull Symbol symbol,
                           Balance balanceA,
                           Balance balanceB,
                           BigDecimal minQuantity,
                           @NotNull ConfigFile configFile)
    {
        this.symbol = symbol;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.minQuantity = minQuantity;
        this.configFile = configFile;
        this.amountSpent = configFile.spent;
        this.amountBought = configFile.bought;
        this.lastBoughtPrice = new BigDecimal(Integer.MAX_VALUE);
        this.profitFile = new ProfitFile(symbol);
        this.statusFile = new StatusFile(symbol);
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        FakeTrader.LAST_PRICE = price.value;

        List<NewOrder> result = new ArrayList<>();
        configFile.load();

        if (configFile.isRunning())
        {
            if (price.value.compareTo(allTimeHigh) >= 0)
            {
                allTimeHigh = price.value;
                Log.console("[%s] New all time high: %s", symbol.name, allTimeHigh);
            }

            BigDecimal boughtPrice = boughtPrice();
            BigDecimal limit = (amountSpent.compareTo(BigDecimal.ZERO) == 0) ? allTimeHigh : lastBoughtPrice;

            if (price.value.compareTo(limit) < 0)
            {
                BigDecimal percentageDown = percentageDiff(price.value, limit);

                statusFile.save(allTimeHigh,
                                boughtPrice,
                                price.value,
                                percentageDown.negate(),
                                balanceA,
                                balanceB);

                BigDecimal amount = buyAmount(
                        price.value,
                        limit,
                        percentageDown);

                if (amount.compareTo(minQuantity) > 0)
                {
                    result = Collections.singletonList(NewOrder.marketBuy(symbol.name, amount.toString()));
                }
            }
            else if ((amountBought.compareTo(BigDecimal.ZERO) > 0) && (price.value.compareTo(boughtPrice) > 0))
            {
                BigDecimal percentageUp = percentageDiff(price.value, boughtPrice);

                statusFile.save(allTimeHigh,
                                boughtPrice,
                                price.value,
                                percentageUp,
                                balanceA,
                                balanceB);

                BigDecimal amount = sellAmount(
                        price.value,
                        boughtPrice,
                        percentageUp);

                if (amount.compareTo(minQuantity) > 0)
                {
                    result = Collections.singletonList(NewOrder.marketSell(symbol.name, amount.toString()));
                }
            }
        }
        else
        {
            Log.console("[%s] Shutting down market", symbol.name);

            result = null;
        }

        return result;
    }

    private BigDecimal buyAmount(BigDecimal price,
                                 BigDecimal limit,
                                 BigDecimal percentageDown)
    {
        BigDecimal result = BigDecimal.ZERO;

        if (price.compareTo(limit) < 0)
        {
            Log.console("[%s] Price diff: %s/%s (-%s%%)", symbol.name, price, limit, percentageDown.multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());

            if (percentageDown.compareTo(MIN_PERCENTAGE_DOWN) >= 0)
            {
                return minQuantity.multiply(new BigDecimal("10"));
            }
        }

        return result;
    }

    private BigDecimal sellAmount(BigDecimal price,
                                  BigDecimal boughtPrice,
                                  BigDecimal percentageUp)
    {
        BigDecimal result = BigDecimal.ZERO;

        if ((price.compareTo(boughtPrice) > 0) && (boughtPrice.compareTo(BigDecimal.ZERO) > 0))
        {
            Log.console("[%s] Price diff: %s/%s (+%s%%)", symbol.name, price, boughtPrice, percentageUp.multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());

            boolean firstSellLimit = (sellHighLimit.compareTo(BigDecimal.ZERO) == 0) && (percentageUp.compareTo(MIN_PERCENTAGE_UP) >= 0);
            boolean newSellLimit = (sellHighLimit.compareTo(BigDecimal.ZERO) > 0) && (price.compareTo(sellHighLimit) > 0);

            if (firstSellLimit || newSellLimit)
            {
                sellHighLimit = price;
                sellLowLimit = sellHighLimit.subtract(sellHighLimit.multiply(MIN_PERCENTAGE_UP.divide(new BigDecimal("2"), balanceB.asset.decimals, RoundingMode.DOWN)));

                Log.console("[%s] New sell limit: %s/%s", symbol.name, sellHighLimit, sellLowLimit);
            }
            else if ((sellLowLimit.compareTo(BigDecimal.ZERO) > 0) && (price.compareTo(sellLowLimit) <= 0))
            {
                return balanceA.amount.setScale(balanceA.asset.step, RoundingMode.DOWN);
            }
        }

        return result;
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
            return buy(response);
        }
        else if (order.getSide() == OrderSide.SELL)
        {
            return sell(response);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @NotNull
    private Object buy(@NotNull NewOrderResponse response)
    {
        if (response.getStatus() == OrderStatus.FILLED)
        {
            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toSpend = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toSpend.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            if (Botcoin.TEST_MODE)
            {
                balanceA.amount = balanceA.amount.add(quantity);
                balanceB.amount = balanceB.amount.subtract(toSpend);
            }
            else
            {
                Account account = Binance.account();
                balanceA.amount = Binance.balance(account, balanceA);
                balanceB.amount = Binance.balance(account, balanceB);
            }

            amountSpent = amountSpent.add(toSpend);
            amountBought = amountBought.add(quantity);
            lastBoughtPrice = price;

            configFile.update(amountSpent, amountBought);

            LogEvent logEvent = LogEvent.buy(
                    balanceA.of(quantity),
                    balanceB.of(price),
                    balanceB.of(toSpend),
                    balanceB.of(boughtPrice()),
                    balanceA,
                    balanceB
            );

            logEvent.log(symbol);

            return logEvent;
        }
        else
        {
            Log.error(String.format("ORDER NOT FILLED: %s", Json.toJsonString(response)));

            return "ERROR";
        }
    }

    @NotNull
    private Object sell(@NotNull NewOrderResponse response)
    {
        if (response.getStatus() == OrderStatus.FILLED)
        {
            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toGain = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toGain.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice());
            BigDecimal profit = toGain.subtract(originalCost);

            if (Botcoin.TEST_MODE)
            {
                balanceA.amount = balanceA.amount.subtract(quantity);
                balanceB.amount = balanceB.amount.add(toGain);
            }
            else
            {
                Account account = Binance.account();
                balanceA.amount = Binance.balance(account, balanceA);
                balanceB.amount = Binance.balance(account, balanceB);
            }

            profitFile.save(profit);

            if (configFile.isShutdown())
            {
                configFile.stop();
            }

            LogEvent logEvent = LogEvent.sell(
                    balanceA.of(quantity),
                    balanceB.of(price),
                    balanceB.of(toGain),
                    balanceB.of(profit),
                    balanceB.of(boughtPrice()),
                    balanceA,
                    balanceB
            );

            logEvent.log(symbol);

            allTimeHigh = BigDecimal.ZERO;
            amountSpent = BigDecimal.ZERO;
            amountBought = BigDecimal.ZERO;
            lastBoughtPrice = new BigDecimal(Integer.MAX_VALUE);
            sellHighLimit = BigDecimal.ZERO;
            sellLowLimit = BigDecimal.ZERO;

            configFile.update(amountSpent, amountBought);

            return logEvent;
        }
        else
        {
            Log.error(String.format("ORDER NOT FILLED: %s", Json.toJsonString(response)));

            return "ERROR";
        }
    }

    private BigDecimal percentageDiff(BigDecimal a, BigDecimal b)
    {
        if (a.compareTo(b) < 0)
        {
            return BigDecimal.ONE.subtract(a.divide(b, 10, RoundingMode.DOWN));
        }
        else
        {
            return a.divide(b, 10, RoundingMode.DOWN).subtract(BigDecimal.ONE);
        }
    }

    private BigDecimal boughtPrice()
    {
        return (amountBought.compareTo(BigDecimal.ZERO) > 0) ?
                amountSpent.divide(amountBought, balanceB.asset.decimals, RoundingMode.DOWN) :
                BigDecimal.ZERO;
    }
}