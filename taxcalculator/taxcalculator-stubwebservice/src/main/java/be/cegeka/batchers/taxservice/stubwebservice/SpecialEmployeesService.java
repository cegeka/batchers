package be.cegeka.batchers.taxservice.stubwebservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

@Service
public class SpecialEmployeesService {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialEmployeesService.class);

    private Map<Long, Integer> blacklistedEmployees = new HashMap<>();
    private Map<Long, Integer> timeoutEmployees = new HashMap<>();

    private String blacklistedEmployeesBackup;
    private String timeoutEmployeesBackup;

    private Function<String, Integer> failureRate = employeeId -> Integer.valueOf(employeeId.split(":")[1]);
    private Function<String, Long> employeeId = employeeId -> Long.valueOf(employeeId.split(":")[0]);

    @Value("${stubwebservice.timeout}")
    private int timeout;

    @Value("${stubwebservice.blacklistemployees}")
    public void setBlacklistedEmployees(String blacklistedEmployees) {
        this.blacklistedEmployeesBackup = blacklistedEmployees;
        this.blacklistedEmployees = parseEmployeeIdWithFailureRateString(blacklistedEmployees);
    }

    @Value("${stubwebservice.timeoutemployees}")
    public void setTimeoutEmployees(String timeoutEmployees) {
        this.timeoutEmployeesBackup = timeoutEmployees;
        this.timeoutEmployees = parseEmployeeIdWithFailureRateString(timeoutEmployees);
    }

    public boolean isEmployeeBlacklisted(Long employeeId) {
        return hasEmployeeRemainingRetriesAndDecreaseRetries(blacklistedEmployees, employeeId);
    }

    public void sleepIfNecessary(Long employeeId) {
        if (isEmployeeTimeout(employeeId)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                LOG.error("I can't get no sleep", e);
            }
        }
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    boolean isEmployeeTimeout(Long employeeId) {
        return hasEmployeeRemainingRetriesAndDecreaseRetries(timeoutEmployees, employeeId);
    }

    private boolean hasEmployeeRemainingRetriesAndDecreaseRetries(Map<Long, Integer> containerMap, Long employeeId) {
        Integer retryCounter = containerMap.getOrDefault(employeeId, 0);

        if (retryCounter < 1) {
            return false;
        } else {
            containerMap.put(employeeId, --retryCounter);
            return true;
        }
    }

    private Map<Long, Integer> parseEmployeeIdWithFailureRateString(String employeeIdWithFailureRateString) {
        if (employeeIdWithFailureRateString == null) {
            return new HashMap<>();
        }

        return asList(employeeIdWithFailureRateString.split(","))
                .stream()
                .collect(toMap(employeeId, failureRate));
    }

    public void reset() {
        setBlacklistedEmployees(blacklistedEmployeesBackup);
        setTimeoutEmployees(timeoutEmployeesBackup);
    }
}
