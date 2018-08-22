package com.frequentis.maritime.mcsr.domain.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public final class EntityUtils {

    private EntityUtils() {
    }

    public static String getCurrentUTCTimeISO8601() {
	TimeZone tz = TimeZone.getTimeZone("UTC");
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	df.setTimeZone(tz);
	return df.format(new Date());
    }

}
