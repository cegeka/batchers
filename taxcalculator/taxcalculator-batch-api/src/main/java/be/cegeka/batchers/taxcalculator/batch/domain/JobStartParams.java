package be.cegeka.batchers.taxcalculator.batch.domain;

public class JobStartParams {

    public static final String MONTH = "month";
    public static final String YEAR = "year";

    private int year;
    private int month;

    public JobStartParams(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public JobStartParams(Long year, Long month) {
        this.year = year.intValue();
        this.month = month.intValue();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
