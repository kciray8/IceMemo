package org.icememo.utils


class JarUtils {
    private static Properties main = null

    public static boolean isDemo() {
        if (main == null) {
            main = new Properties();
            String propFileName = "/config/main.properties";
            InputStream inputStream = JarUtils.class.getResourceAsStream(propFileName);
            main.load((InputStream) inputStream);
        }

        String demo = main.getProperty("demo");
        if (demo == "true") {
            return true
        } else {
            return false
        }
    }
}
