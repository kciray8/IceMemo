package org.icememo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {
    public static String findOne(String regExp, String str) {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}