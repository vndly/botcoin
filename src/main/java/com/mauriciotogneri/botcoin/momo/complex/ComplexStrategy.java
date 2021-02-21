package com.mauriciotogneri.botcoin.momo.complex;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.app.Botcoin;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.log.PriceFile;
import com.mauriciotogneri.botcoin.log.ProfitFile;
import com.mauriciotogneri.botcoin.log.ConfigProperties;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
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
    private final ComplexBuyStrategy buyStrategy;
    private final ComplexSellStrategy sellStrategy;
    private final ConfigProperties configProperties;
    private final ProfitFile profitFile;
    private final PriceFile priceFile;

    private BigDecimal allTimeHigh = BigDecimal.ZERO;
    private BigDecimal boughtPrice;
    private State state;

    public ComplexStrategy(@NotNull Symbol symbol,
                           Balance balanceA,
                           Balance balanceB,
                           BigDecimal minQuantity,
                           @NotNull ConfigProperties configProperties)
    {
        this.symbol = symbol;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.minQuantity = minQuantity;
        this.buyStrategy = new ComplexBuyStrategy(minQuantity);
        this.sellStrategy = new ComplexSellStrategy(minQuantity);
        this.configProperties = configProperties;
        this.boughtPrice = configProperties.boughtPrice;
        this.state = configProperties.state;
        this.profitFile = new ProfitFile(symbol);
        this.priceFile = new PriceFile(symbol);
    }

    @Override
    public List<NewOrder> orders(@NotNull Price price)
    {
        List<NewOrder> result = new ArrayList<>();
        configProperties.load();

        if (configProperties.isRunning())
        {
            if (state == State.BUYING)
            {
                if (price.value.compareTo(allTimeHigh) >= 0)
                {
                    allTimeHigh = price.value;
                    Log.console("[%s] New all time high: %s", symbol.name, allTimeHigh);
                }

                priceFile.save(state,
                               allTimeHigh,
                               boughtPrice,
                               price.value,
                               BigDecimal.ONE.subtract(price.value.divide(allTimeHigh, 10, RoundingMode.DOWN)));

                BigDecimal amount = buyStrategy.amount(symbol, price.value, allTimeHigh, balanceA, balanceB);

                if (amount.compareTo(minQuantity) > 0)
                {
                    result = Collections.singletonList(NewOrder.marketBuy(symbol.name, amount.toString()));
                }
            }
            else if (state == State.SELLING)
            {
                priceFile.save(state,
                               allTimeHigh,
                               boughtPrice,
                               price.value,
                               price.value.divide(boughtPrice, 10, RoundingMode.DOWN).subtract(BigDecimal.ONE));

                BigDecimal amount = sellStrategy.amount(symbol, price.value, boughtPrice, balanceA);

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
            state = State.SELLING;

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

            boughtPrice = price;

            LogEvent logEvent = LogEvent.buy(
                    balanceA.of(quantity),
                    balanceB.of(price),
                    balanceB.of(toSpend),
                    balanceB.of(boughtPrice),
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );

            logEvent.log(symbol);

            return logEvent;
        }
        else
        {
            return "ERROR";
        }
    }

    @NotNull
    private Object sell(@NotNull NewOrderResponse response)
    {
        if (response.getStatus() == OrderStatus.FILLED)
        {
            state = State.BUYING;
            allTimeHigh = BigDecimal.ZERO;

            BigDecimal quantity = new BigDecimal(response.getExecutedQty());
            BigDecimal toGain = new BigDecimal(response.getCummulativeQuoteQty());
            BigDecimal price = toGain.divide(quantity, balanceA.asset.decimals, RoundingMode.DOWN);

            BigDecimal originalCost = quantity.multiply(boughtPrice);
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

            if (configProperties.isShutdown())
            {
                configProperties.off();
            }

            LogEvent logEvent = LogEvent.sell(
                    balanceA.of(quantity),
                    balanceB.of(price),
                    balanceB.of(toGain),
                    balanceB.of(profit),
                    balanceB.of(boughtPrice),
                    balanceA,
                    balanceB,
                    totalBalance(price)
            );

            logEvent.log(symbol);

            return logEvent;
        }
        else
        {
            return "ERROR";
        }
    }

    private Balance totalBalance(BigDecimal price)
    {
        return balanceB.of(balanceB.amount.add(balanceB.amount.multiply(price)).setScale(balanceB.asset.decimals, RoundingMode.DOWN));
    }

    public enum State
    {
        BUYING,
        SELLING
    }
}