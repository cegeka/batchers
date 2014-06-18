package be.cegeka.batchers.taxcalculator.presentation.to;

import be.cegeka.batchers.taxcalculator.presentation.utils.JodaMoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class EmployeeTo {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Integer income;
  private Long errorCount;

  @JsonSerialize(using = JodaMoneySerializer.class)
  @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
          parameters = {@Parameter(name = "currencyCode", value = "EUR")})
  private Money taxTotal = Money.zero(CurrencyUnit.EUR);

  public EmployeeTo() {
  }

  public EmployeeTo(String firstName, String lastName, String email, Integer income, Money taxTotal, Long id, Long errorCount) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.income = income;
    this.taxTotal = taxTotal;
    this.id = id;
    this.errorCount = errorCount;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getIncome() {
    return income;
  }

  public void setIncome(Integer income) {
    this.income = income;
  }

  public Money getTaxTotal() {
    return taxTotal;
  }

  public void setTaxTotal(Money taxTotal) {
    this.taxTotal = taxTotal;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getErrorCount() {
    return errorCount;
  }

  public void setErrorCount(Long errorCount) {
    this.errorCount = errorCount;
  }
}
