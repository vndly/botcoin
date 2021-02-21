package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.market.Symbol;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Properties;

public class ConfigFile
{
    private final Symbol symbol;
    public String mode;
    public BigDecimal spent;

    public static final String MODE_RUNNING = "running";
    public static final String MODE_STOPPED = "stopped";
    public static final String MODE_SHUTDOWN = "shutdown";

    public ConfigFile(Symbol symbol)
    {
        this.symbol = symbol;
        load();
    }

    public void load()
    {
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream(path(symbol)));

            mode = properties.getProperty("MODE");
            spent = new BigDecimal(properties.getProperty("SPENT"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String path(@NotNull Symbol symbol)
    {
        return String.format("output/%s/config.properties", symbol.name);
    }

    public boolean isRunning()
    {
        return mode.equals(MODE_RUNNING) || mode.equals(MODE_SHUTDOWN);
    }

    public boolean isShutdown()
    {
        return mode.equals(MODE_SHUTDOWN);
    }

    public void stop()
    {
        Log log = new Log(path(symbol));
        log.write(String.format("MODE=%s%n", MODE_STOPPED));
        log.write(String.format("SPENT=%s", spent.toString()));
    }
}