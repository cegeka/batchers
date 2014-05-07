package be.cegeka.batchers.taxservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 07.05.2014
 * Time: 09:15
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SpecialEmployeesService {

    @Value("${stubwebservice.blacklistemployees}")
    private String blacklistEmployeesProperty;

    @Value("${stubwebservice.timeoutemployees}")
    private String timeoutEmployeesProperty;

    @Value("${stubwebservice.timeout}")
    private int timeout;

    private Set<String> blacklistedEmployees = new HashSet<String>();
    private Set<String> timeoutEmployees = new HashSet<String>();

    public boolean isEmployeeBlacklisted(String employeeId){
        return blacklistedEmployees.contains(employeeId);
    }

    public boolean isEmployeeTimeout(String employeeID) {
        return timeoutEmployees.contains(employeeID);
    }

    @PostConstruct
    public void parseSpecialEmployees()  {
        if(blacklistEmployeesProperty != null){
            blacklistedEmployees = new HashSet<String>(Arrays.asList(blacklistEmployeesProperty.split(",")));
        }

        if(timeoutEmployeesProperty != null){
            timeoutEmployees = new HashSet<String>(Arrays.asList(timeoutEmployeesProperty.split(",")));
        }
    }

    public void sleepIfNecessary(String employeeID) {
         if(isEmployeeTimeout(employeeID)){
             try {
                 Thread.sleep(timeout);
             } catch (InterruptedException e) {
             }
         }
    }

    public void setBlacklistedEmployees(Set<String> blacklistedEmployees) {
        this.blacklistedEmployees = blacklistedEmployees;
    }

    public void setTimeoutEmployees(Set<String> timeoutEmployees) {
        this.timeoutEmployees = timeoutEmployees;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
