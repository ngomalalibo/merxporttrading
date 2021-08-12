package com.merxport.trading.util;

import java.util.UUID;

public class GenerateCommodityCode
{
    /**
     * This is used to generate a human readable tracking code used to refer to and track packages. It parses the generated UUID and extracts a readable tracking code.
     * However the uniqueness of the code cannot be ascertained as such the mongo generated primary key is a better choice within the server to deal with packages.
     */
    public static String generateCommodityCode()
    {
        String raw = UUID.randomUUID().toString();
        String[] sect = raw.split("-");
        StringBuilder dd = new StringBuilder();
        for (int i = 0; i < sect.length; i++)
        {
            dd.append(sect[i].charAt(2));
        }
        return dd.toString().toUpperCase();
    }
}
