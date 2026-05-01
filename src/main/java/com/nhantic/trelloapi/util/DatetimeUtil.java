package com.nhantic.trelloapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatetimeUtil {
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime parse(String date) {
        return LocalDateTime.parse(date, formatter);
    }

    public static String parse(LocalDateTime date) {
        return date.format(formatter);
    }
}
