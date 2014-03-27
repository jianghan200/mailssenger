package com.mailssenger.util;

/*
 * Time Tools
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	//parse sendDate in the sqlite db into unixtime
	public static long parseTime(String sendDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");

		Date date = null;
		try {
			date = sdf.parse(sendDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long unixtime = date.getTime() / 1000L;
//		System.out.println(unixtime);
		return unixtime;
	}
	
	
	public static long parseMailDateToChatMessageTime(String sendDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");

		Date date = null;
		try {
			date = sdf.parse(sendDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long unixtime = date.getTime();
		
		return unixtime;
	}
	
	public static String converTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long timeGap = currentSeconds - timestamp;// interval with current time
		String timeStr = null;
		if (timeGap > 7 * 24 * 60 * 60) {// more than one week
			timeStr = getStandardTime(timestamp);
		} else if (timeGap > 24 * 60 * 60) {// more than one day
			timeStr = timeGap / (24 * 60 * 60) + "dyas before";
		} else if (timeGap > 60 * 60) {// 1-24hours
			timeStr = timeGap / (60 * 60) + "hours befrore";
		} else if (timeGap > 60) {// 1-59mins
			timeStr = timeGap / 60 + "minutes befrore";
		} else {// 1-59second
			timeStr = "just now";
		}
		return timeStr;
	}

	public static String converTimeForMail(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long timeGap = currentSeconds - timestamp;// interval with current time
		String timeStr = null;
		if (timeGap > 7 * 24 * 60 * 60) {// more than one week
			timeStr = getStandardTime(timestamp);
		}else if (timeGap > 24 * 60 * 60) {// more than one day
			timeStr = timeGap / (24 * 60 * 60) + " days";
		} else if (timeGap > 60 * 60) {// 1-24hours
			timeStr = timeGap / (60 * 60) + " hours";
		} else if (timeGap > 60) {// 1-59mins
			timeStr = timeGap / 60 + " mins";
		} else {// 1-59second
			timeStr = "Just Now";
		}
		return timeStr;
	}

	public static String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		Date date = new Date(timestamp * 1000);
		sdf.format(date);
		return sdf.format(date);
	}
	

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "Today" + getHourAndMin(timesamp);
			break;
		case 1:
			result = "Yesterday" ;
//			+ getHourAndMin(timesamp)
			break;
//		case 2:
//			result = "前天 " + getHourAndMin(timesamp);
//			break;

		default:
			// result = temp + "天前 ";
			result = getTime(timesamp);
			break;
		}

		return result;
	}
}
