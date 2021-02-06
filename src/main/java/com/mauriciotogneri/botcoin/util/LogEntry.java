package com.mauriciotogneri.botcoin.util;

import com.mauriciotogneri.botcoin.provider.Data;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogEntry
{
    private final Data data;
    private final List<Object> events;

    public LogEntry(Data data, @NotNull List<Object> events)
    {
        this.data = data;
        this.events = events.isEmpty() ? null : events;
    }
}