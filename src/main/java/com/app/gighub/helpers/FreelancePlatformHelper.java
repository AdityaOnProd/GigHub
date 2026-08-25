package com.app.gighub.helpers;

import java.time.LocalDateTime;

public class FreelancePlatformHelper {

    public static LocalDateTime getCurrentMySQLDate() {
        return LocalDateTime.now();
    }

    public static String nl2br(String str) {
        return str.replaceAll("(\\r\\n|\\n)", "<br />");
    }
}
