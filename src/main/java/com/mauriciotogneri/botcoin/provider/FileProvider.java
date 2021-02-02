package com.mauriciotogneri.botcoin.provider;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileProvider implements PriceProvider
{
    private int index = 0;
    private final Price[] prices;

    public FileProvider(String path) throws Exception
    {
        this.prices = load(path);
    }

    @NotNull
    private Price[] load(String path) throws Exception
    {
        List<Price> list = new ArrayList<>();

        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            String[] parts = line.split(";");

            if (parts.length > 1)
            {
                long timestamp = Long.parseLong(parts[0]);
                double price = Double.parseDouble(parts[1]);
                list.add(new Price(timestamp, price));
            }
        }

        fileReader.close();

        Price[] result = new Price[list.size()];

        for (int i = 0; i < list.size(); i++)
        {
            result[i] = list.get(i);
        }

        return result;
    }

    public void reset()
    {
        index = 0;
    }

    public Price[] prices()
    {
        return prices;
    }

    @Override
    public boolean hasPrice()
    {
        return (index < prices.length);
    }

    @Override
    public Price price()
    {
        return prices[index++];
    }
}