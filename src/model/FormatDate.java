package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDate {

    public static String getDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
