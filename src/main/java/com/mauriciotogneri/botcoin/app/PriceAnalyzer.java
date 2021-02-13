package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.FilePriceProvider;
import com.mauriciotogneri.botcoin.provider.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceAnalyzer
{
    public static void main(String[] args)
    {
        String symbol = "ETHBTC";
        FilePriceProvider dataProvider = new FilePriceProvider(String.format("input/prices_%s_ONE_MINUTE.csv", symbol));
        Price[] prices = dataProvider.prices();

        BigDecimal sumPercentageDown = BigDecimal.ZERO;
        int sumTicks = 0;
        int count = 0;

        boolean searchingAllTimeHigh = true;
        int tempTicks = 0;
        BigDecimal allTimeHigh = BigDecimal.ZERO;
        BigDecimal lowestPrice = BigDecimal.ZERO;

        for (Price value : prices)
        {
            BigDecimal price = value.value;

            if (searchingAllTimeHigh)
            {
                if (price.compareTo(allTimeHigh) >= 0)
                {
                    allTimeHigh = price;
                }
                else
                {
                    searchingAllTimeHigh = false;
                    lowestPrice = allTimeHigh;
                    tempTicks = 0;
                }
            }
            else
            {
                tempTicks++;

                if (price.compareTo(allTimeHigh) <= 0)
                {
                    if (price.compareTo(lowestPrice) <= 0)
                    {
                        lowestPrice = price;
                    }
                }
                else
                {
                    BigDecimal percentageDown = BigDecimal.ONE.subtract(lowestPrice.divide(allTimeHigh, 10, RoundingMode.DOWN));

                    if (percentageDown.compareTo(new BigDecimal("0.01")) >= 0)
                    {
                        sumPercentageDown = sumPercentageDown.add(percentageDown);
                        sumTicks += tempTicks;
                        count++;
                    }

                    searchingAllTimeHigh = true;
                    allTimeHigh = BigDecimal.ZERO;
                }
            }
        }

        System.out.printf("PERCENTAGE DOWN: %s%n", sumPercentageDown.divide(new BigDecimal(count), 2, BigDecimal.ROUND_DOWN).toString());
        System.out.printf("TICKS: %s%n", sumTicks / count);
    }

    public static class Result
    {
        public final BigDecimal percentageDown;
        public final Integer ticks;

        public Result(BigDecimal percentageDown, Integer ticks)
        {
            this.percentageDown = percentageDown;
            this.ticks = ticks;
        }

        public boolean isValid()
        {
            return (percentageDown != null) && (ticks != null);
        }
    }
}