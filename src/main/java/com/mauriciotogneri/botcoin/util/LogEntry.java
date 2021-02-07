package com.mauriciotogneri.botcoin.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogEntry
{
    public final Object data;
    public final List<Object> events;

    public LogEntry(Object data, @NotNull List<Object> events)
    {
        this.data = data;
        this.events = events.isEmpty() ? null : events;
    }

    public boolean hasEvents()
    {
        return (events != null);
    }
}