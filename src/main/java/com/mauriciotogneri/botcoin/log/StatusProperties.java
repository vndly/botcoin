package com.mauriciotogneri.botcoin.log;

import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.complex.ComplexStrategy;
import com.mauriciotogneri.botcoin.momo.complex.ComplexStrategy.State;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Properties;

public class StatusProperties
{
    private final Symbol symbol;
    public String mode;
    public ComplexStrategy.State state;
    public BigDecimal boughtPrice;

    public static final String MODE_ON = "on";
    public static final String MODE_OFF = "off";
    public static final String MODE_SHUTDOWN = "shutdown";

    public StatusProperties(Symbol symbol)
    {
        this.symbol = symbol;
        load();
    }

    public void load()
    {
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream(String.format("output/%s/status.properties", symbol.name)));

            mode = properties.getProperty("MODE");
            state = State.valueOf(properties.getProperty("STATE"));
            boughtPrice = new BigDecimal(properties.getProperty("BOUGHT_PRICE"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isRunning()
    {
        return mode.equals(MODE_ON) || mode.equals(MODE_SHUTDOWN);
    }

    public boolean isShutdown()
    {
        return mode.equals(MODE_SHUTDOWN);
    }

    public void off()
    {
        mode = MODE_OFF;
    }
}