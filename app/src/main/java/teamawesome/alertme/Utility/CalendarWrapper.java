package teamawesome.alertme.Utility;


import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarWrapper extends GregorianCalendar {

    int dayOfWeek;
    long todayInMillis;

    public CalendarWrapper() {
        GregorianCalendar c = new GregorianCalendar();
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        todayInMillis = c.getTimeInMillis();
    }


    public long getTodayInMillis() {
        return todayInMillis;
    }

    public long getNextSundayInMillis() {
        return getNextSpecificDayInMillis(Calendar.SUNDAY);
    }

    public long getNextMondayInMillis() {
        return getNextSpecificDayInMillis(Calendar.MONDAY);
    }

    public long getNextTuesdayInMillis() {
        return getNextSpecificDayInMillis(Calendar.TUESDAY);
    }

    public long getNextWednesdayInMillis() {
        return getNextSpecificDayInMillis(Calendar.WEDNESDAY);
    }

    public long getNextThursdayInMillis() {
        return getNextSpecificDayInMillis(Calendar.THURSDAY);
    }

    public long getNextFridayInMillis() {
        return getNextSpecificDayInMillis(Calendar.FRIDAY);
    }

    public long getNextSaturdayInMillis() {
        return getNextSpecificDayInMillis(Calendar.SATURDAY);
    }

    private long getNextSpecificDayInMillis(int day) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        if (dayOfWeek != day) {
            // the "day" param is also the difference between Saturday and specified day
            int daysToAdd = (Calendar.SATURDAY - dayOfWeek + day) % 7;
            c.add(Calendar.DAY_OF_YEAR, daysToAdd);
        } else {
            c.add(Calendar.DAY_OF_YEAR, 7);
        }

        return c.getTimeInMillis();
    }
}
