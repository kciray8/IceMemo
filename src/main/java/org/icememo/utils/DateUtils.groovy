package org.icememo.utils

import org.icememo.Time

import java.text.SimpleDateFormat

public class DateUtils {
    public static long WEEK_MS = 1000 * 60 * 60 * 24 * 7;
    public static long DAY_MS = 1000 * 60 * 60 * 24;

    public static String getNiceDate(long ms) {
        Date date = new Date(ms);
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(date);
    }

    public static String getUniqueFileName() {
        return new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss_SSS").format(new Date(System.currentTimeMillis()));
    }

    public static long getDayId(long ms) {
        long dayId;

        if (dayId == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            calendar.setTimeInMillis(ms);
            calendar.add(Calendar.HOUR, -4);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);

            calendar.add(Calendar.HOUR, 4);

            dayId = calendar.getTimeInMillis();
        }

        return dayId;
    }

    public static Long getJarBuildTime() {
        try {

            Class cl = DateUtils
            String rn = cl.getName().replace('.', '/') + ".class";
            JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(rn).openConnection();
            return j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
        } catch (e) {

        }
        return Time.ms
    }

    public static long weekFirstMs() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTimeInMillis()
    }

    public static long weekLastMs() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        return cal.getTimeInMillis() - 1;
    }
}

