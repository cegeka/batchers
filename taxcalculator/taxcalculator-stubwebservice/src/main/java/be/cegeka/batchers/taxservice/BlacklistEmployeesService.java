package be.cegeka.batchers.taxservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 07.05.2014
 * Time: 09:15
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BlacklistEmployeesService {

    @Value("${stubwebservice.blacklistemployees}")
    String blacklistEmployeesProperty;

    private Set<String> blacklistedEmployees = new HashSet<String>();



    public boolean isEmployeeBlacklisted(String employeeId){
        return blacklistedEmployees.contains(employeeId);
    }

    @PostConstruct
    public void parseBlacklistEmployees()  {
        if(blacklistEmployeesProperty != null){
            blacklistedEmployees = new HashSet<String>(Arrays.asList(blacklistEmployeesProperty.split(",")));
        }
    }
}
