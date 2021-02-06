package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FakeTrader implements Trader
{
    @Override
    public List<NewOrderResponse> process(@NotNull List<NewOrder> orders)
    {
        Random random = new Random();
        List<NewOrderResponse> responses = new ArrayList<>();

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
                    String.valueOf(
                            Double.parseDouble(order.getQuantity()) * Double.parseDouble(order.getPrice())
                    )
            );

            response.setTransactTime(System.currentTimeMillis());
            response.setOrderId(random.nextLong());
            response.setClientOrderId(UUID.randomUUID().toString());
            response.setStatus(OrderStatus.FILLED);

            responses.add(response);
        }

        return responses;
    }
}