package ru.nedovizin.homeaccountancy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Period {
    private LocalDate mDate;
    private final DateTimeFormatter mDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter mDateTimeFormatterPeriod = DateTimeFormatter.ofPattern("MM.yyyy");

    public Period() {
        mDate = LocalDate.now();
    }

    public Period(String periodLine) {
        mDate = LocalDate.parse("01." + periodLine, mDateTimeFormatter);
    }

    public Period(int month, int year) {
        mDate = LocalDate.of(year, month, 1);
    }

    public Period(LocalDate date) {
        mDate = date;
    }

    public String getFormatLine() {
        return mDate.format(mDateTimeFormatterPeriod);
    }

    public LocalDate getDate() {
        return mDate;
    }

    public Period getNext() {
        return new Period(mDate.plusMonths(1));
    }

    public Period getPrev() {
        return new Period(mDate.minusMonths(1));
    }

    public int getYear() {
        return mDate.getYear();
    }

    public int getMonth() {
        return mDate.getMonthValue();
    }
}
