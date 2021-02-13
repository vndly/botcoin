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
    public Boolean enabled;
    public ComplexStrategy.State state;
    public BigDecimal boughtPrice;
    public Boolean shutdownAfterSell;

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

            enabled = Boolean.parseBoolean(properties.getProperty("ENABLED"));
            state = State.valueOf(properties.getProperty("STATE"));
            boughtPrice = new BigDecimal(properties.getProperty("BOUGHT_PRICE"));
            shutdownAfterSell = Boolean.parseBoolean(properties.getProperty("SHUTDOWN_AFTER_SELL"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}