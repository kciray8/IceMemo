package org.icememo

class Time {
    static long day = 24 * 60 * 60 * 1000
    static long offset = 0

    static long getMs() {
        return System.currentTimeMillis() + (offset * day);
    }

    static long getMsMinusDays(int days) {
        return (getMs() - (days * 1000 * 60 * 60 * 24));
    }
}
