package com.mauriciotogneri.botcoin.log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

public class ParamsFile
{
    public BigDecimal minPercentageDown;
    public BigDecimal minPercentageUp;
    public BigDecimal minQuantityMultiplier;
    public BigDecimal notionalValueMultiplier;
    
    public void load()
    {
        try
        {
            InputStream inputStream = new FileInputStream("params.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            minPercentageDown = new BigDecimal(properties.getProperty("MIN_PERCENTAGE_DOWN"));
            minPercentageUp = new BigDecimal(properties.getProperty("MIN_PERCENTAGE_UP"));
            minQuantityMultiplier = new BigDecimal(properties.getProperty("MIN_QUANTITY_MULTIPLIER"));
            notionalValueMultiplier = new BigDecimal(properties.getProperty("NOTIONAL_VALUE_MULTIPLIER"));
        }
        catch (Exception e)
        {
            Log.error(e);
        }
    }
}