package com.mauriciotogneri.botcoin.util;

import com.google.gson.JsonObject;
import com.mauriciotogneri.botcoin.trader.OrderSent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LogEntry
{
    public final Object data;
    public final List<JsonObject> events;

    public LogEntry(Object data, @NotNull List<OrderSent> sent, List<Object> events)
    {
        this.data = data;
        this.events = events(sent, events);
    }

    @Nullable
    private List<JsonObject> events(@NotNull List<OrderSent> sent, List<Object> events)
    {
        List<JsonObject> list = new ArrayList<>();

        for (int i = 0; i < sent.size(); i++)
        {
            OrderSent orderSent = sent.get(i);
            Object event = events.get(i);

            JsonObject json = new JsonObject();
            json.add("order", Json.toJsonObject(orderSent.order));
            json.add("response", Json.toJsonObject(orderSent.response));
            json.add("custom", Json.toJsonObject(event));

            list.add(json);
        }

        return list.isEmpty() ? null : list;
    }

    public boolean hasEvents()
    {
        return (events != null);
    }
}