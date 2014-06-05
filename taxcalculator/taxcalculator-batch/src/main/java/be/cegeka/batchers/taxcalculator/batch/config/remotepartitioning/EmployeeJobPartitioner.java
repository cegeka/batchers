package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationRepository;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@StepScope
public class EmployeeJobPartitioner implements Partitioner {
    private static final int PARTITIONS_PER_NODE = 5;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @Value("#{jobParameters[year]}")
    private long year;

    @Value("#{jobParameters[month]}")
    private long month;

    @Autowired
    private StepExecution stepExecution;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int partitionCount = gridSize * PARTITIONS_PER_NODE;

        List<Long> employeeIds = taxCalculationRepository.getUnprocessedEmployeeIds(year, month, stepExecution.getJobExecutionId());
        int size = employeeIds.size();
        int targetSize = size / partitionCount + 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        for (int index = 0, partitionNumber = 0; index < size; index += targetSize, partitionNumber++) {
            ExecutionContext value = new ExecutionContext();
            value.putLong("minValue", employeeIds.get(index));
            value.putLong("maxValue", employeeIds.get(Math.min(index + targetSize - 1, size - 1)));
            result.put("partition" + partitionNumber, value);
        }

        return result;
    }
}
