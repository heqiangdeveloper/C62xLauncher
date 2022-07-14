package launcher.base.utils;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateKit {
    public static String getToday() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy.MM.dd  EE");
        return df.format(date);
    }
}
