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
    public BigDecimal bought;

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
            bought = new BigDecimal(properties.getProperty("BOUGHT"));
        }
        catch (Exception e)
        {
            Log.error(e);
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
        mode = MODE_STOPPED;

        write();
    }

    public void update(BigDecimal spent, BigDecimal bought)
    {
        this.spent = spent;
        this.bought = bought;

        write();
    }

    public void write()
    {
        Log log = new Log(path(symbol));
        log.write(String.format("MODE=%s%n", mode));
        log.write(String.format("SPENT=%s%n", spent.toString()));
        log.write(String.format("BOUGHT=%s", bought.toString()));
    }
}