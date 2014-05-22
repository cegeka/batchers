package be.cegeka.batchers.taxcalculator.batch.tasklet;

import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import be.cegeka.batchers.taxcalculator.batch.service.reporting.SumOfTaxes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobResultsTaskletTest {
    public static final double SUCCESS_SUM_MOCK = 192D;
    public static final double FAILED_SUM_MOCK = 133D;
    @InjectMocks
    JobResultsTasklet jobResultsTasklet;

    @Mock
    SumOfTaxes sumOfTaxesMock;
    @Mock
    PDFGeneratorService pdfGeneratorServiceMock;
    @Mock
    StepContribution stepContributionMock;
    @Mock
    ChunkContext chunkContextMock;
    @Mock
    ResourceLoader resourceLoader;

    @Captor
    ArgumentCaptor<Resource> resourceArgumentCaptor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> contextMapCaptor;

    @Test
    public void testExecuteCallsService() throws Exception {
        Resource mockResource = mock(Resource.class);
        when(resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "paycheck-template.docx")).thenReturn(mockResource);
        when(sumOfTaxesMock.getSuccessSum()).thenReturn(SUCCESS_SUM_MOCK);
        when(sumOfTaxesMock.getFailedSum()).thenReturn(FAILED_SUM_MOCK);


        jobResultsTasklet.execute(stepContributionMock, chunkContextMock);

        verify(pdfGeneratorServiceMock).generatePdfAsByteArray(resourceArgumentCaptor.capture(), contextMapCaptor.capture());

        Resource resourceValue = resourceArgumentCaptor.getValue();
        assertThat(resourceValue).isEqualTo(mockResource);
        Map<String, Object> contextMap = contextMapCaptor.getValue();
        assertThat(contextMap).contains(entry("success_sum", SUCCESS_SUM_MOCK), entry("failed_sum", FAILED_SUM_MOCK));
    }
}