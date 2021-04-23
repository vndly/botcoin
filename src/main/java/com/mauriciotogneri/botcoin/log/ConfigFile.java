package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.market.Symbol;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

public class ConfigFile
{
    private final String symbol;
    public String mode;
    public BigDecimal spent;
    public BigDecimal bought;

    public static final String MODE_RUNNING = "running";
    public static final String MODE_STOPPED = "stopped";
    public static final String MODE_SHUTDOWN = "shutdown";
    public static final String MODE_SELL = "sell";

    public ConfigFile(Symbol symbol)
    {
        this(symbol.name);
    }

    public ConfigFile(String symbol)
    {
        this.symbol = symbol;
        load();
    }

    public void load()
    {
        try
        {
            InputStream inputStream = new FileInputStream(path(symbol));
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            mode = properties.getProperty("MODE");
            spent = new BigDecimal(properties.getProperty("SPENT"));
            bought = new BigDecimal(properties.getProperty("BOUGHT"));
        }
        catch (Exception e)
        {
            Log.error(e);
        }
    }

    private String path(@NotNull String symbol)
    {
        return String.format("output/%s/config.properties", symbol);
    }

    public boolean isRunning()
    {
        return mode.equals(MODE_RUNNING) || mode.equals(MODE_SHUTDOWN) || mode.equals(MODE_SELL);
    }

    public boolean shouldSell()
    {
        return mode.equals(MODE_SELL);
    }

    public boolean shouldShutdown()
    {
        return mode.equals(MODE_SHUTDOWN);
    }

    public boolean shouldStop()
    {
        return mode.equals(MODE_SHUTDOWN) || mode.equals(MODE_SELL);
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

    public void reset()
    {
        this.mode = MODE_RUNNING;
        this.spent = new BigDecimal("0");
        this.bought = new BigDecimal("0");

        write();
    }

    public void write()
    {
        Log log = new Log(path(symbol));
        log.write(String.format("MODE=%s%n", mode));
        log.write(String.format("SPENT=%s%n", spent.setScale(8, RoundingMode.DOWN)));
        log.write(String.format("BOUGHT=%s", bought.setScale(8, RoundingMode.DOWN)));
        log.close();
    }
}