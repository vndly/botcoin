package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FakeTrader implements Trader
{
    @Override
    public Map<NewOrder, NewOrderResponse> process(@NotNull List<NewOrder> orders)
    {
        Random random = new Random();
        Map<NewOrder, NewOrderResponse> responses = new HashMap<>();

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
            response.setCummulativeQuoteQty(
                    String.format("%.2f", Double.parseDouble(order.getQuantity()) * Double.parseDouble(order.getPrice()))
            );

            response.setTransactTime(System.currentTimeMillis());
            response.setOrderId((long) Math.abs(random.nextInt()));
            response.setClientOrderId(UUID.randomUUID().toString());
            response.setStatus(OrderStatus.FILLED);

            responses.put(order, response);
        }

        return responses;
    }
}