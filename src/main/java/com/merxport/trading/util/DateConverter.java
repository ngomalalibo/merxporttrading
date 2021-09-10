package com.merxport.trading.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


/**
 * Converts between dates and strings conveniently.
 */
public class DateConverter
{
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss a Z");
    
    public static String zonedDateTimeToString(ZonedDateTime zdt)
    {
        return zdt.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss a Z"));
    }
    
    public static ZonedDateTime stringToDateConverter(String date)
    {
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        localDateTime.atZone(TimeZone.getDefault().toZoneId());
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);
        return offsetDateTime.toZonedDateTime();
    }
    
    public static LocalDateTime stringToLocalDateTime(String str)
    {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(str, formatter);
    }
    
    public static Timestamp millisecToTimestampConv(long milli)
    {
        return new Timestamp(milli);
    }
}
