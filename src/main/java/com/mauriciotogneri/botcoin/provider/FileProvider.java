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
    private final float[] prices;

    public FileProvider(String path) throws Exception
    {
        this.prices = load(path);
    }

    public FileProvider(float[] prices)
    {
        this.prices = prices;
    }

    @NotNull
    private float[] load(String path) throws Exception
    {
        List<Float> list = new ArrayList<>();

        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            String[] parts = line.split(";");

            if (parts.length > 1)
            {
                float price = Float.parseFloat(parts[1]);
                list.add(price);
            }
        }

        fileReader.close();

        float[] result = new float[list.size()];

        for (int i = 0; i < list.size(); i++)
        {
            result[i] = list.get(i);
        }

        return result;
    }

    public float[] prices()
    {
        return prices;
    }

    @Override
    public boolean hasMorePrices()
    {
        return index < prices.length;
    }

    @Override
    public float price()
    {
        return prices[index++];
    }
}