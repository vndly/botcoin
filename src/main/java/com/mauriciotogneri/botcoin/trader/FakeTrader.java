package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FakeTrader implements Trader
{
    public static BigDecimal LAST_PRICE;

    @Override
    public List<OrderSent> process(@NotNull List<NewOrder> orders)
    {
        Random random = new Random();
        List<OrderSent> sent = new ArrayList<>();

        for (NewOrder order : orders)
        {
            NewOrderResponse response = new NewOrderResponse();
            response.setType(order.getType());
            response.setSide(order.getSide());
            response.setTimeInForce(order.getTimeInForce());
            response.setSymbol(order.getSymbol());
            response.setPrice(order.getPrice());
            response.setOrigQty(order.getQuantity());
            response.setExecutedQty(order.getQuantity());
            response.setCummulativeQuoteQty(new BigDecimal(order.getQuantity()).multiply(LAST_PRICE).toString());

            response.setTransactTime(System.currentTimeMillis());
            response.setOrderId((long) Math.abs(random.nextInt()));
            response.setClientOrderId(UUID.randomUUID().toString());
            response.setStatus(OrderStatus.FILLED);

            sent.add(new OrderSent(order, response));
        }

        return sent;
    }
}