package be.cegeka.batchers.taxcalculator.batch.config;

import org.springframework.batch.core.JobExecution;

public class EmployeeJobExecutionListener implements org.springframework.batch.core.JobExecutionListener {
    private SumOfTaxes sumOfTaxes;

    public EmployeeJobExecutionListener(SumOfTaxes sumOfTaxes) {
        this.sumOfTaxes = sumOfTaxes;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("\n\n\nbefore employee job execution\n\n\n");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("\n\n\nafter employee job execution\n\n\n");
        System.out.println("\n\n\n sum of taxes = " + sumOfTaxes.getSum());
    }
}
