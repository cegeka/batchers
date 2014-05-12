package be.cegeka.batchers.taxservice.stubwebservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class SpecialEmployeesService {

    @Value("${stubwebservice.timeout}")
    private int timeout;

    private Map<Long, Integer> blacklistedEmployees = new HashMap<>();
    private Map<Long, Integer> timeoutEmployees = new HashMap<>();

    @Value("${stubwebservice.blacklistemployees}")
    public void setBlacklistedEmployees(String blacklistedEmployees) {
        this.blacklistedEmployees = parseEmployeeIdWithFailureRateString(blacklistedEmployees);
    }

    @Value("${stubwebservice.timeoutemployees}")
    public void setTimeoutEmployees(String timeoutEmployees) {
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

    private Map<Long, Integer> parseEmployeeIdWithFailureRateString(String blacklistedEmployees) {
        List<String> employeeIdWithFailureRate = Arrays.asList(blacklistedEmployees.split(","));
        return employeeIdWithFailureRate.stream()
                .collect(toMap(employeeId, failureRate));
    }

    private Function<String, Integer> failureRate = employeeId -> Integer.valueOf(employeeId.split(":")[1]);
    private Function<String, Long> employeeId = employeeId -> Long.valueOf(employeeId.split(":")[0]);
}
