package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeJobPartitionerTest {
    @Mock
    private EmployeeService employeeService;
    @Mock
    private StepExecution stepExecution;

    @InjectMocks
    private EmployeeJobPartitioner employeeJobPartitioner;

    @Test
    public void given31Employees_whenPartitioningFor1NodeWith5PartitionsPerNode_thenDistributeCorrectly() throws Exception {
        List<Long> employeeIds = new ArrayList<>();
        for (long i = 1; i <= 31; i++) {
            employeeIds.add(i);
        }
        when(employeeService.getEmployeeIds(anyLong(), anyLong(), anyLong())).thenReturn(employeeIds);

        Map<String, ExecutionContext> partitions = employeeJobPartitioner.partition(1);

        assertThat(partitions).hasSize(5);

        assertThat(partitions.get("partition0").getLong("minValue")).isEqualTo(1L);
        assertThat(partitions.get("partition0").getLong("maxValue")).isEqualTo(7L);

        assertThat(partitions.get("partition1").getLong("minValue")).isEqualTo(8L);
        assertThat(partitions.get("partition1").getLong("maxValue")).isEqualTo(14L);

        assertThat(partitions.get("partition2").getLong("minValue")).isEqualTo(15L);
        assertThat(partitions.get("partition2").getLong("maxValue")).isEqualTo(21L);

        assertThat(partitions.get("partition3").getLong("minValue")).isEqualTo(22L);
        assertThat(partitions.get("partition3").getLong("maxValue")).isEqualTo(28L);

        assertThat(partitions.get("partition4").getLong("minValue")).isEqualTo(29L);
        assertThat(partitions.get("partition4").getLong("maxValue")).isEqualTo(31L);

    }

    @Test
    public void given100Employees_whenPartitioningFor1NodeWith5PartitionsPerNode_thenDistributeCorrectly() throws Exception {
        List<Long> employeeIds = new ArrayList<>();
        for (long i = 1; i <= 100; i++) {
            employeeIds.add(i);
        }
        when(employeeService.getEmployeeIds(anyLong(), anyLong(), anyLong())).thenReturn(employeeIds);

        Map<String, ExecutionContext> partitions = employeeJobPartitioner.partition(1);

        assertThat(partitions).hasSize(5);

        assertThat(partitions.get("partition0").getLong("minValue")).isEqualTo(1L);
        assertThat(partitions.get("partition0").getLong("maxValue")).isEqualTo(21L);

        assertThat(partitions.get("partition1").getLong("minValue")).isEqualTo(22L);
        assertThat(partitions.get("partition1").getLong("maxValue")).isEqualTo(42L);

        assertThat(partitions.get("partition2").getLong("minValue")).isEqualTo(43L);
        assertThat(partitions.get("partition2").getLong("maxValue")).isEqualTo(63L);

        assertThat(partitions.get("partition3").getLong("minValue")).isEqualTo(64L);
        assertThat(partitions.get("partition3").getLong("maxValue")).isEqualTo(84L);

        assertThat(partitions.get("partition4").getLong("minValue")).isEqualTo(85L);
        assertThat(partitions.get("partition4").getLong("maxValue")).isEqualTo(100L);
    }
}
