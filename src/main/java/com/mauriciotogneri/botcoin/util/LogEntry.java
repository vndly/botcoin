package com.mauriciotogneri.botcoin.util;

import com.mauriciotogneri.botcoin.provider.Data;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogEntry
{
    public final Data data;
    public final List<Object> events;

    public LogEntry(Data data, @NotNull List<Object> events)
    {
        this.data = data;
        this.events = events.isEmpty() ? null : events;
    }

    public boolean hasEvents()
    {
        return (events != null);
    }
}