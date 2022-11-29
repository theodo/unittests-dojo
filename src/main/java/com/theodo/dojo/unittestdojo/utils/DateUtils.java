package com.theodo.dojo.unittestdojo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final double MILLISECONDS_IN_ONE_DAY = 86400000D;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");

    public static int daysBetween(Date infDate, Date supDate) {
      double deltaTime = supDate.getTime() - infDate.getTime();
      return (int)round(deltaTime / MILLISECONDS_IN_ONE_DAY);
    }

    public static Date parseDate(String ddmmyyyy) {
      if (ddmmyyyy == null) {
        return null;
      }
      if ("null".equals(ddmmyyyy)) {
        return null;
      }
      try {
        synchronized (SIMPLE_DATE_FORMAT_DD_MM_YYYY) {
          return SIMPLE_DATE_FORMAT_DD_MM_YYYY.parse(ddmmyyyy);
        }
      }
      catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    private static long round(double num) {
      if (num >= 0) {
        return (long)(num + 0.5);
      }
      else {
        return -round(-num);
      }
    }

    public static boolean isStrictlyBefore(Date currentDate, Date beginDate) {
                return false;
    }
}
