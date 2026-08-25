package net.vatri.freelanceplatform.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FreelancePlatformHelper {

    public static LocalDateTime getCurrentMySQLDate() {
        return LocalDateTime.now();
    }

    public static String nl2br(String str) {
        return str.replaceAll("(\\r\\n|\\n)", "<br />");
    }
}
