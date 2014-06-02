package be.cegeka.batchers.taxcalculator.batch.api.events;

public class JobStartRequest {

    private String name;
    private int year;
    private int month;

    public JobStartRequest(String name, int year, int month) {
        this.name = name;
        this.year = year;
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
