package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.FilePriceProvider;
import com.mauriciotogneri.botcoin.provider.Price;

import org.jetbrains.annotations.NotNull;

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

        for (int i = 0; i < (prices.length - 1); i++)
        {
            Result result = analyze(prices, i);

            if (result.isValid())
            {
                sumPercentageDown = sumPercentageDown.add(result.percentageDown);
                sumTicks += result.ticks;
                count++;
            }
        }

        System.out.printf("PERCENTAGE DOWN: %s%n", sumPercentageDown.divide(new BigDecimal(count), 2, BigDecimal.ROUND_DOWN).toString());
        System.out.printf("TICKS: %s%n", sumTicks / (double) count);
    }

    @NotNull
    private static Result analyze(@NotNull Price[] prices, int index)
    {
        BigDecimal startPrice = prices[index].value;
        BigDecimal lowestPrice = startPrice;

        for (int i = index; i < prices.length; i++)
        {
            BigDecimal price = prices[i].value;

            if (price.compareTo(lowestPrice) < 0)
            {
                lowestPrice = price;
            }
            else if (price.compareTo(startPrice) > 0)
            {
                break;
            }
        }

        BigDecimal status = new BigDecimal(String.valueOf((index * 100) / (double) prices.length));
        System.out.printf("STATUS: %s%%%n", status.setScale(0, RoundingMode.DOWN));

        return new Result(new BigDecimal("1"), 12);
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