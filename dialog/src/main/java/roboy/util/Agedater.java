package roboy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Agedater {
    private final Logger LOGGER = LogManager.getLogger();

    /**
     * A helper function to determine the age based on the birthdate
     *
     * Java >= 8 specific: java.time.LocalDate, java.time.Period, java.time.ZoneId
     *
     * @param datestring
     * @return timeSpans in form of HasMap - years:Integer, months:Integer and days:Integer
     */
    public HashMap<String, Integer> determineAge(String datestring) {
        HashMap<String, Integer> timeSpans = new HashMap<>();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date date = null;
        try {
            date = format.parse(datestring);
        } catch (ParseException e) {
            LOGGER.error("Error while parsing a date: " + datestring + ". " + e.getMessage());
        }
        if (date != null) {
            LocalDate birthdate = date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate();
            LOGGER.debug("The birthdate is " + birthdate.toString());
            LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
            Period age = Period.between(birthdate, now);
            timeSpans.put("years", age.getYears());
            timeSpans.put("months", age.getMonths());
            timeSpans.put("days", age.getDays());
            LOGGER.debug("The estimated age is: " +
                    timeSpans.get("years") + " years, or " +
                    timeSpans.get("months") + " months, or " +
                    timeSpans.get("days") + " days!");
        }
        return timeSpans;
    }
}
