package be.cegeka.batchers.taxcalculator.application.domain;

public class EmployeeTestFixture {

    public static Employee anEmployee() {
        return new EmployeeBuilder()
                .withFirstName("firstame")
                .withLastName("lastName")
                .withEmailAddress("cucu@mailinator.com")
                .build();
    }
}
