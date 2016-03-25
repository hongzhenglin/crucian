package com.crucian.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期时间操作类
 * 
 * @author linhz
 * @author major 添加isToday等方法，统一方法，整理注释 默认日期时间格式化串为："yyyy-MM-dd HH:mm:ss"
 *         默认日期格式化串为"yyyy-MM-dd"
 */
public class DateUtil {

	static Logger log = LoggerFactory.getLogger(DateUtil.class);

	public static final int MILLI = 0; // 毫秒级别
	public static final int SECOND = 1; // 秒级别
	public static final int MINUTE = 2; // 分钟级别
	public static final int HOUR = 3; // 小时级别
	public static final int DAY = 4; // 天数级别
	public static final int WEEK = 5; // 星期级别
	public static final int MONTH = 6; // 月份级别
	public static final int YEAR = 7; // 年份级别

	public static final String YYYYMMDD = "yyyy-MM-dd";
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String pattern19ToDate = "yyyy-MM-dd HH24:mi:ss";

	private static final long MILLISECOND_IN_MINUTE = 1000 * 60; // 一分钟有多少毫秒
	private static final long MILLISECOND_IN_HOUR = 1000 * 60 * 60; // 一小时有多少毫秒
	private static final long MILLISECOND_IN_DAY = 1000 * 60 * 60 * 24; // 一天有多少毫秒
	private static final long MILLISECOND_IN_WEEK = 1000 * 60 * 60 * 24 * 7; // 一周有多少毫秒

	/*
	 * 取当月的第一天.
	 */
	@SuppressWarnings("static-access")
	public static Date getfirstDayOfMonth() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(YYYYMMDD);

		Calendar sCal = Calendar.getInstance();
		sCal.setTime(date);

		sCal.add(sCal.DAY_OF_MONTH, -(sCal.get(sCal.DAY_OF_MONTH) - 1));

		try {
			date = format.parse(format.format(sCal.getTime()));
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return date;
	}

	/*
	 * 取当月的最后一天.
	 */
	public static Date getlastDayOfMonth() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(YYYYMMDDHHMMSS);
		Calendar eCal = Calendar.getInstance();
		eCal.setTime(date);

		eCal.set(Calendar.DAY_OF_MONTH, 1);
		eCal.add(Calendar.MONTH, 1);
		eCal.add(Calendar.DATE, -1);

		try {
			date = format.parse(format.format(eCal.getTime()));
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return date;
	}

	/*
	 * 计算两时间相差的月数.
	 */
	public static int getMonthSpace(Date date) {

		SimpleDateFormat df = new SimpleDateFormat(YYYYMMDDHHMMSS);
		String sysTime = df.format(new Date());
		String creTime = df.format(date);
		;
		int inum = 0;
		int sysYear = Integer.parseInt(sysTime.substring(0, 4));
		int sysMonth = Integer.parseInt(sysTime.substring(5, 7));
		int creYear = Integer.parseInt(creTime.substring(0, 4));
		int creMonth = Integer.parseInt(creTime.substring(5, 7));

		if (sysYear == creYear) {
			inum = sysMonth - creMonth;
		} else {
			inum = (sysYear * 12 + sysMonth) - (creYear * 12 + creMonth);
		}
		return Math.abs(inum);
	}

	/**
	 * 计算两个时间点相差的秒数，分钟数，小时数，天数，星期数，月数，年数等。 例如：2008-4-11 10:26:00 和 2008-4-12
	 * 0:0:0 相差1天，相差13小时，相差14*60-26分钟。
	 * 
	 * @param type
	 *            :分钟类型、小时类型等
	 * @param cal1
	 * @param cal2
	 * @return 为正数，cal2大于cal1
	 */
	public static int calculate2Date(Calendar cal1, Calendar cal2, int type) {
		switch (type) {
		case SECOND:
			return (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000);
		case MINUTE:
			return calculateMinuteDiff(cal1, cal2);
		case HOUR:
			return calculateHourDiff(cal1, cal2);
		case DAY:
			return calculateDateDiff(cal1, cal2);
		case WEEK:
			return calculateWeekDiff(cal1, cal2);
		case MONTH:
			return (cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)) * 12
					+ (cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
		case YEAR:
			return cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
		default:
			throw new IllegalArgumentException("无效的计算时间类型。");
		}
	}

	public static long calculateTimeDiff(Date beginTime, Date endTime, int type) {
		Calendar calen1 = Calendar.getInstance();
		calen1.setTime(beginTime);
		Calendar calen2 = Calendar.getInstance();
		calen2.setTime(endTime);
		long dateDiff = calculate2Date(calen1, calen2, type);
		return dateDiff;
	}

	/**
	 * 计算相差分钟数 cal2-cal1>0
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	private static int calculateMinuteDiff(Calendar cal1, Calendar cal2) {
		int mil2 = cal2.get(Calendar.MILLISECOND);
		int mil1 = cal1.get(Calendar.MILLISECOND);
		int sec2 = cal2.get(Calendar.SECOND);
		int sec1 = cal1.get(Calendar.SECOND);

		cal2.set(Calendar.MILLISECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);
		cal2.set(Calendar.SECOND, 0);
		cal1.set(Calendar.SECOND, 0);

		int result = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / MILLISECOND_IN_MINUTE);

		cal2.set(Calendar.SECOND, sec2);
		cal1.set(Calendar.SECOND, sec1);
		cal2.set(Calendar.MILLISECOND, mil2);
		cal1.set(Calendar.MILLISECOND, mil1);

		return result;
	}

	/**
	 * 计算相差小时数 cal2-cal1>0
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	private static int calculateHourDiff(Calendar cal1, Calendar cal2) {
		int mil2 = cal2.get(Calendar.MILLISECOND);
		int mil1 = cal1.get(Calendar.MILLISECOND);
		int sec2 = cal2.get(Calendar.SECOND);
		int sec1 = cal1.get(Calendar.SECOND);
		int min2 = cal2.get(Calendar.MINUTE);
		int min1 = cal1.get(Calendar.MINUTE);

		cal2.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);
		cal1.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);

		int result = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / MILLISECOND_IN_HOUR);

		cal2.set(Calendar.MINUTE, min2);
		cal1.set(Calendar.MINUTE, min1);
		cal2.set(Calendar.SECOND, sec2);
		cal1.set(Calendar.SECOND, sec1);
		cal2.set(Calendar.MILLISECOND, mil2);
		cal1.set(Calendar.MILLISECOND, mil1);
		return result;
	}

	/**
	 * 计算相差天数 cal2-cal1>0
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	private static int calculateDateDiff(Calendar cal1, Calendar cal2) {
		int mil2 = cal2.get(Calendar.MILLISECOND);
		int mil1 = cal1.get(Calendar.MILLISECOND);
		int sec2 = cal2.get(Calendar.SECOND);
		int sec1 = cal1.get(Calendar.SECOND);
		int min2 = cal2.get(Calendar.MINUTE);
		int min1 = cal1.get(Calendar.MINUTE);
		int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
		int hour1 = cal1.get(Calendar.HOUR_OF_DAY);

		cal2.set(Calendar.HOUR_OF_DAY, 0);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		cal2.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);
		cal1.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);

		int result = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / MILLISECOND_IN_DAY);

		cal2.set(Calendar.HOUR_OF_DAY, hour2);
		cal1.set(Calendar.HOUR_OF_DAY, hour1);
		cal2.set(Calendar.MINUTE, min2);
		cal1.set(Calendar.MINUTE, min1);
		cal2.set(Calendar.SECOND, sec2);
		cal1.set(Calendar.SECOND, sec1);
		cal2.set(Calendar.MILLISECOND, mil2);
		cal1.set(Calendar.MILLISECOND, mil1);
		return result;
	}

	/**
	 * 计算相差星期数 cal2-cal1>0
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	private static int calculateWeekDiff(Calendar cal1, Calendar cal2) {
		int mil2 = cal2.get(Calendar.MILLISECOND);
		int mil1 = cal1.get(Calendar.MILLISECOND);
		int sec2 = cal2.get(Calendar.SECOND);
		int sec1 = cal1.get(Calendar.SECOND);
		int min2 = cal2.get(Calendar.MINUTE);
		int min1 = cal1.get(Calendar.MINUTE);
		int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
		int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
		int day2 = cal2.get(Calendar.DAY_OF_WEEK);
		int day1 = cal1.get(Calendar.DAY_OF_WEEK);

		cal2.set(Calendar.DAY_OF_WEEK, 0);
		cal1.set(Calendar.DAY_OF_WEEK, 0);
		cal2.set(Calendar.HOUR_OF_DAY, 0);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		cal2.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);
		cal1.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);

		int result = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / MILLISECOND_IN_WEEK);

		cal2.set(Calendar.DAY_OF_WEEK, day2);
		cal1.set(Calendar.DAY_OF_WEEK, day1);
		cal2.set(Calendar.HOUR_OF_DAY, hour2);
		cal1.set(Calendar.HOUR_OF_DAY, hour1);
		cal2.set(Calendar.MINUTE, min2);
		cal1.set(Calendar.MINUTE, min1);
		cal2.set(Calendar.SECOND, sec2);
		cal1.set(Calendar.SECOND, sec1);
		cal2.set(Calendar.MILLISECOND, mil2);
		cal1.set(Calendar.MILLISECOND, mil1);
		return result;
	}

	/**
	 * 将指定日期具体精确到type定义的阶段，如为day，则精确到天
	 * 
	 * @param type
	 * @param date
	 * @return
	 */
	public static Date getCurDate(int type, Date date) {
		switch (type) {
		case MINUTE:
			return getCurMinute(date);
		case HOUR:
			return getCurHour(date);
		case DAY:
			return getCurDay(date);
		case MONTH:
			return getCurMonth(date);
		default:
			throw new IllegalArgumentException("无效的计算时间类型。");
		}
	}

	/**
	 * 获取该date的分钟，若date为空，则获取当前时间的分钟,比如传入的date是2007-12-26 10:33:21,
	 * 则返回的是2007-12-26 10:33:00
	 * 
	 * @param date
	 * @return
	 */
	private static Date getCurMinute(Date date) {
		Date curDate = null;
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
		String strCurDate = sdf.format(date);
		try {
			curDate = sdf.parse(strCurDate);
		} catch (ParseException e) {
			log.warn("解释日期失败", e);
		}
		return curDate;
	}

	/**
	 * 获取该date的小时，若date为空，则获取当前时间的小时,比如传入的date是2007-12-26 10:33:21,
	 * 则返回的是2007-12-26 10:00:00
	 * 
	 * @param date
	 * @return
	 */
	private static Date getCurHour(Date date) {
		Date curDate = null;
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH");
		String strCurDate = sdf.format(date);
		try {
			curDate = sdf.parse(strCurDate);
		} catch (ParseException e) {
			log.warn("解释日期失败", e);
		}
		return curDate;
	}

	/**
	 * 取得传入的日期精确到天的对象,比如传入的date是2007-12-26 10:33:21, 则返回的是2007-12-26 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getCurDay(Date date) {
		Date curDate = null;
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		String strCurDate = String.format("%1$tY-%1$tm-%1$td", date);
		try {
			curDate = sdf.parse(strCurDate);
		} catch (ParseException e) {
			log.warn("解释日期失败", e);
		}
		return curDate;
	}

	/**
	 * 取得传入的日期精确到月的对象,比如传入的date是2007-12-26 10:33:21, 则返回的是2007-12-01 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getCurMonth(Date date) {
		Date curMonth = null;
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM");
		String strCurDate = String.format("%1$tY-%1$tm", date);
		try {
			curMonth = sdf.parse(strCurDate);
		} catch (ParseException e) {
			log.warn("解释日期失败", e);
		}
		return curMonth;
	}

	/**
	 * 返回指定日期的年份、月份、日份、小时、分钟、秒、毫秒
	 * 
	 * @param type
	 *            ：包括年份、月份、日份、小时、分钟、秒、毫秒
	 * @param date
	 *            ：指定日期
	 * @return
	 */
	public static long getSingleTime(int type, Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		switch (type) {
		case MILLI:
			return c.getTimeInMillis();
		case MINUTE:
			return c.get(java.util.Calendar.MINUTE);
		case HOUR:
			return c.get(java.util.Calendar.HOUR_OF_DAY);
		case DAY:
			return c.get(java.util.Calendar.DAY_OF_MONTH);
		case MONTH:
			return c.get(java.util.Calendar.MONTH) + 1;
		case YEAR:
			return c.get(java.util.Calendar.YEAR);
		default:
			throw new IllegalArgumentException("无效的时间类型。");
		}
	}

	public static Date addMinutes(Date date, long... minutes) {
		if (minutes == null) {
			return date;
		}
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		for (long minute : minutes) {
			c.add(Calendar.MINUTE, (int) minute);
		}

		return c.getTime();
	}

	/**
	 * 计算两个日期相差的分钟数 date-subDate
	 * 
	 * @param date
	 * @param subDate
	 * @return 两个时间相差的分钟数。
	 */
	public static long subDate(Date date, Date subDate) {
		long sub = 0;
		if (date == null || subDate == null) {
			throw new IllegalArgumentException();
		}
		sub = date.getTime() - subDate.getTime();
		return sub / (60 * 1000);
	}

	/**
	 * 计算两个日期相差的分钟数 date-subDate
	 * 
	 * @param date
	 * @param subDate
	 * @return
	 */
	public static long subDateCeil(Date date, Date subDate) {
		long sub = 0;
		if (date == null || subDate == null) {
			throw new IllegalArgumentException();
		}
		sub = date.getTime() - subDate.getTime();
		return Double.valueOf(Math.ceil(sub * 1.0 / (60 * 1000))).longValue();
	}

	/**
	 * String类型变为Date类型
	 * 
	 * @param s
	 * @return
	 */
	public static Date convertStrToDate(String s) {
		try {
			DateFormat dateformat = DateFormat.getDateInstance();
			Date date = dateformat.parse(s);
			return date;
		} catch (Exception exception) {
			exception.printStackTrace();
			Calendar cal = Calendar.getInstance();
			cal.set(1900, 0, 1);
			return cal.getTime();
		}
	}

	/**
	 * 按照指定格式将String类型变为Date类型
	 * 
	 * @param s
	 * @param format
	 *            ：指定格式
	 * @return
	 */
	public static Date convertStrToDate(String s, String format) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
		try {
			Date date = simpledateformat.parse(s);
			return date;
		} catch (Exception exception) {
			exception.printStackTrace();
			Calendar cal = Calendar.getInstance();
			cal.set(1900, 0, 1);
			return cal.getTime();
		}
	}

	/**
	 * 按照指定格式将Date类型变为String类型
	 * 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String convertDateToStr(Date d, String format) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
		String s;
		try {
			s = simpledateformat.format(d).toString();
			return s;
		} catch (Exception e) {
			s = "1900-01-01";
		}
		return s;
	}

	/**
	 * 将格式为YYYY-MI-DD hh:mi:ss字符串转换成日期型
	 * 
	 * @param date
	 * @param flag
	 *            :如果flag为true则时间为 YYYY-MI-DD 0:0:0，为false则为23:59:59
	 * @return
	 */
	public static Date stringToDate(String date) {
		if (date == null)
			return null;
		String[] temp = date.split("-");
		if (temp.length != 3)
			return null;
		Calendar instance = Calendar.getInstance();
		String[] temp1 = temp[2].split(" ");
		String dd = temp1[0];
		String time = temp1[1];
		String[] temp2 = time.split(":");
		instance.set(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]) - 1,
				Integer.parseInt(dd), Integer.parseInt(temp2[0]),
				Integer.parseInt(temp2[1]), Integer.parseInt(temp2[2]));
		return instance.getTime();
	}

	/**
	 * 将格式为YYYY-MI-DD字符串转换成日期型
	 * 
	 * @param date
	 * @param flag
	 *            :如果flag为true则时间为 YYYY-MI-DD 0:0:0，为false则为23:59:59
	 * @return
	 */
	public static Date stringToDate(String date, boolean flag) {
		if (date == null)
			return null;
		String[] temp = date.split("-");
		if (temp.length != 3)
			return null;
		Calendar instance = Calendar.getInstance();
		if (flag) {
			instance.set(Integer.parseInt(temp[0]),
					Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[2]),
					0, 0, 0);
		} else {
			instance.set(Integer.parseInt(temp[0]),
					Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[2]),
					23, 59, 59);
		}
		return instance.getTime();
	}

	/**
	 * 将字符串转换为日期型
	 * 
	 * @param year
	 * @param month
	 * @param flag
	 *            :为true则是当前月的时间， 为false是下个月的时间
	 * @return
	 */
	public static Date stringToMonth(String year, String month, boolean flag) {
		if (year == null || month == null)
			return null;
		Calendar instance = Calendar.getInstance();
		if (flag) {
			instance.set(Integer.parseInt(year), Integer.parseInt(month) - 1,
					1, 0, 0, 0);
		} else {
			instance.set(Integer.parseInt(year), Integer.parseInt(month), 1, 0,
					0, 0);
		}
		return instance.getTime();
	}

	/**
	 * 两个字符串格式的日期之间的比较.
	 * 
	 * @param strDate1
	 * @param strDate2
	 * @param format
	 * @return
	 */
	public static boolean compareDate(String strDate1, String strDate2,
			String format) {
		Date date1 = convertStrToDate(strDate1, format);
		Date date2 = convertStrToDate(strDate2, format);
		if (date1.getTime() >= date2.getTime())
			return true;
		return false;
	}

	/**
	 * 两个日期之间的比较.
	 * 
	 * @param date1
	 * @param date2
	 * @param format
	 * @return
	 */
	public static int compareDate(Date date1, Date date2, String format) {
		String dateStr1 = convertDateToStr(date1, format);
		String dateStr2 = convertDateToStr(date2, format);
		return dateStr1.compareTo(dateStr2);
	}

	/**
	 * 
	 * 比较两个日期是否同一天.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		int flag = compareDate(date1, date2, "yy-MM-dd");
		return flag == 0 ? true : false;
	}

	/**
	 * 
	 * 比较两个日期是否同一月.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameMonth(Date date1, Date date2) {
		int flag = compareDate(date1, date2, "yy-MM");
		return flag == 0 ? true : false;
	}

	/**
	 * 比较一个日期是否在指定日期之间.
	 * 
	 * @param date
	 * @param startDate
	 *            ：起始日期
	 * @param endDate
	 *            ：结束日期
	 * @return:返回0，则在指定日期区间之内
	 */
	public static int compareDate(Date date, Date startDate, Date endDate) {
		if (date == null || startDate == null || endDate == null)
			return -2;
		if (date.getTime() >= startDate.getTime()
				&& date.getTime() <= endDate.getTime())
			return 0;
		else if (date.getTime() > endDate.getTime())
			return 1;
		else
			return -1;
	}

	/**
	 * 返回今天前Num天的日期。
	 * 
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public static String getDateBeforeToday(int num) {
		long longtimes = System.currentTimeMillis() - num * MILLISECOND_IN_DAY;
		Date date = new Date(longtimes);
		return convertDateToStr(date, "yyyy-MM-dd");
	}

	/**
	 * 返回指定日期的前Num天的日期。
	 * 
	 * @param day
	 *            ：指定日期
	 * @param num
	 * @return
	 */
	public static String getDateBeforeTheDay(String day, int num) {
		long longtimes = convertStrToDate(day).getTime() - num
				* MILLISECOND_IN_DAY;
		Date date = new Date(longtimes);
		return convertDateToStr(date, YYYYMMDD);
	}

	/**
	 * 两个日期之间的比较。
	 * 
	 * @param strDate1
	 * @param strDate2
	 * @param format
	 * @return
	 */
	public static boolean compareDateFormat(String strDate1, String strDate2,
			String format) {
		Date date1 = convertStrToDate(strDate1, format);
		Date date2 = convertStrToDate(strDate2, format);
		if (date1.getTime() > date2.getTime()) {
			return true;
		}
		return false;
	}

	public static Date dayStr2Date(String dateStr) {
		return str2Date(dateStr, YYYYMMDD);
	}

	public static Date str2Date(String dateStr) {
		return str2Date(dateStr, pattern19ToDate);
	}

	/**
	 * 时间串转化为Date。
	 * 
	 * @param dateStr
	 *            dateFormat时间格式的字符串
	 * @param dateFormat
	 *            时间格式
	 * @return Date
	 */
	public static Date str2Date(String dateStr, String dateFormat) {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}

		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		try {
			return df.parse(dateStr);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Date转化为YYYYMMDDHH24MISS格式的字符串。
	 * 
	 * @param date
	 *            Date
	 * @return YYYYMMDDHH24MISS格式的字符串
	 */
	public static String date2Str(Date date) {
		return date2Str(date, YYYYMMDDHHMMSS);
	}

	public static String getCurDate() {
		return date2Str(new Date(), YYYYMMDDHHMMSS);
	}

	/**
	 * 获取当前时间
	 * 
	 * @param dateFormat
	 *            时间格式
	 * @return 返回指定时间格式的字符串
	 */
	public static String getCurDate(String dateFormat) {
		return date2Str(new Date(), dateFormat);
	}

	/**
	 * Date转化为dateFormat时间格式的字符串
	 * 
	 * @param date
	 *            Date
	 * @param dateFormat
	 *            时间格式
	 * @return dateFormat时间格式的字符串
	 */
	public static String date2Str(Date date, String dateFormat) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
	}

	/**
	 * 返回Date对象的年份。
	 * 
	 * @param date
	 *            日期
	 * @return 返回年份
	 */
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	/**
	 * 返回Date对象的月份。
	 * 
	 * @param date
	 *            日期
	 * @return 返回月份
	 */
	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}

	/**
	 * 返回Date对象的日份。
	 * 
	 * @param date
	 *            日
	 * @return 返回日期
	 */
	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 返回Date对象的小时。
	 * 
	 * @param date
	 *            日期
	 * @return 返回小时
	 */
	public static int getHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 返回Date对象的分钟。
	 * 
	 * @param date
	 *            日期
	 * @return 返回分钟
	 */
	public static int getMinute(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MINUTE);
	}

	/**
	 * 返回秒。
	 * 
	 * @param date
	 *            日期
	 * @return 返回秒钟
	 */
	public static int getSecond(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.SECOND);
	}

	/**
	 * 返回毫秒。
	 * 
	 * @param date
	 *            日期
	 * @return 返回毫秒
	 */
	public static long getMillis(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}

	/**
	 * 日期加月。
	 * 
	 * @param date
	 *            日期
	 * @param month
	 *            加的月数
	 * @return 返回相加后的日期
	 */

	public static Date addMonth(Date date, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	/**
	 * 日期相加。
	 * 
	 * @param date
	 *            日期
	 * @param day
	 *            加的天数
	 * @return 返回相加后的日期
	 */
	public static Date addDays(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
		return c.getTime();
	}

	/**
	 * 日期加小时。
	 * 
	 * @param date
	 *            日期
	 * @param hours
	 *            小时数
	 * @return 返回相加后的日期
	 */
	public static Date addHours(Date date, int hours) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getMillis(date) + ((long) hours) * 3600 * 1000);
		return c.getTime();
	}

	/**
	 * 日期加分钟。
	 * 
	 * @param date
	 * @param minutes
	 *            分钟
	 * @return 返回相加后的日期
	 */
	public static Date addMinutes(Date date, long minutes) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getMillis(date) + ((long) minutes) * 60 * 1000);
		return c.getTime();
	}

	/**
	 * 日期加分钟。
	 * 
	 * @param date
	 * @param minutes
	 *            分钟
	 * @return 返回相加后的日期
	 */
	public static Date add(Date date, long minutes) {
		if (date == null) {
			throw new IllegalArgumentException();
		}

		return addMinutes(date, minutes);
	}

	/**
	 * 两个时间点相差的秒数。
	 * 
	 * @param minuend
	 * @param subtrahend
	 * @return 两个时间点相差的秒数(minuend - subtrahend)
	 */
	public static long sub(Date minuend, Date subtrahend) {
		long subResult = 0;
		if (minuend == null || subtrahend == null) {
			throw new IllegalArgumentException();
		}
		subResult = minuend.getTime() - subtrahend.getTime();
		subResult = subResult / 1000;
		return subResult;
	}

	/**
	 * 两个时间点相差的天数。
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return 两个时间点相差的天数(beginDate - endDate)
	 */
	public static long between(Date beginDate, Date endDate) {
		Calendar calBegin = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calBegin.setTime(beginDate);
		calEnd.setTime(endDate);
		calBegin.clear(14);
		calEnd.clear(14);
		long millisecs = calBegin.getTime().getTime()
				- calEnd.getTime().getTime();
		long remainder = millisecs % 0x5265c00L;
		return (millisecs - remainder) / 0x5265c00L;
	}

	/**
	 * 格式化Date对象输出字符串的函数。
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String 为date按指定格式format格式化的string串
	 */
	public static String formatDate(Date date, String format) {
		return formatDateByFormat(date, format);
	}

	/**
	 * 格式化Date对象输出字符串的函数。
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String 为date按指定格式format格式化的string串
	 */
	public static String dateToString(Date date, String format) {
		return formatDateByFormat(date, format);
	}

	/**
	 * 以指定的格式来格式化日期。
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String 输出格式
	 * @return String String 为date按指定格式format格式化的string串
	 */
	public static String formatDateByFormat(Date date, String format) {
		String result = "";
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				log.info("date:" + date);
			}
		}
		return result;
	}

	/**
	 * 以指定的格式和日期字符返回日期。
	 * 
	 * @param str
	 *            String 需要格式化的日期串
	 * @param format
	 *            String 输出格式
	 * @return String String 为按指定格式format格式化的Date
	 */
	public static Date formatDate(String str, String format) {
		return stringToDate(str, format);
	}

	/**
	 * 以指定的格式和日期字符返回日期。
	 * 
	 * @param str
	 *            String 需要格式化的日期串
	 * @param format
	 *            String 输出格式
	 * @return Date 为按指定格式format格式化的Date,失败返回null
	 */
	public static Date stringToDate(String str, String format) {
		if (str != null) {
			DateFormat dateFormat = new SimpleDateFormat(format);
			try {
				return dateFormat.parse(str);
			} catch (ParseException e) {
				return null;
			}
		}

		return null;
	}

	/**
	 * 判断"yyyyMMdd"格式日期字符串是否为今天。
	 * 
	 * @param checkDate
	 *            String "yyyyMMdd"格式的日期字符串
	 * @return 若checkDate为今天，则返回true否者为false
	 */
	public static boolean isToday(String checkDate) {
		Date date = DateUtil.stringToDate(checkDate, YYYYMMDD);
		DateUtil.formatDate(date, YYYYMMDD);
		return DateUtil.formatDate(new Date(), YYYYMMDD).equals(
				DateUtil.formatDate(date, YYYYMMDD));
	}

	/**
	 * 把Date类型转换为Timestamp。
	 * 
	 * @param date
	 *            Date 需要被转换的Date类型日期
	 * 
	 * @return Timestamp类型日期
	 */
	public static Timestamp dateToTimestamp(Date date) {
		Timestamp tmResult = null;
		if (date != null)
			tmResult = new Timestamp(date.getTime());
		return tmResult;
	}
	
	public static Timestamp now() {
		return dateToTimestamp(new Date());
	}
}
