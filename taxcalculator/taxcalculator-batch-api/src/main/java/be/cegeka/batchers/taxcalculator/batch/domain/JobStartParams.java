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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobStartParams that = (JobStartParams) o;

        if (month != that.month) return false;
        if (year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        return result;
    }
}
