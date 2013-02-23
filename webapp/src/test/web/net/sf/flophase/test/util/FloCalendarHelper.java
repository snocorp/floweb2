package net.sf.flophase.test.util;

import java.util.Calendar;

/**
 * This class is a helper class for manipulating dates and calendars.
 */
public class FloCalendarHelper {
	public static final String ARIANOW_DATE_FORMAT = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)";
	
	/**
	 * Sets the given {@link Calendar} to midnight.
	 * @param cal The calendar
	 */
	public void setToMidnight(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
}
