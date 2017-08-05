package org.icememo.desktop

import org.icememo.utils.DesktopApi

import java.util.prefs.Preferences

class OS {
    public static Preferences prefs = Preferences.userNodeForPackage(Main);

    public static String MAIN_FOLDER = "mainFolder"

    public static void openURL(String url) {

        DesktopApi.browse(new URI(url))
        /*
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(url));
        } else {

        }*/
    }
}
