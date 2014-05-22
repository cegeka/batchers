package be.cegeka.batchers.taxcalculator.batch.tasklet;

import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import be.cegeka.batchers.taxcalculator.batch.service.reporting.SumOfTaxes;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

@Component
public class JobResultsTasklet implements Tasklet {
    public static final String JOB_RESULT_PDF_DOCX = ResourceUtils.CLASSPATH_URL_PREFIX + "paycheck-template.docx";
    @Autowired
    SumOfTaxes sumOfTaxes;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        Resource resource = resourceLoader.getResource(JOB_RESULT_PDF_DOCX);

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("success_sum", sumOfTaxes.getSuccessSum());
        contextMap.put("failed_sum", sumOfTaxes.getFailedSum());
        contextMap.put("period", sumOfTaxes.getFailedSum());
        contextMap.put("name", sumOfTaxes.getFailedSum());
        contextMap.put("monthly_income", sumOfTaxes.getFailedSum());
        contextMap.put("monthly_tax", sumOfTaxes.getFailedSum());
        contextMap.put("tax_total", sumOfTaxes.getFailedSum());
        contextMap.put("employee_id", sumOfTaxes.getFailedSum());
        pdfGeneratorService.generatePdfAsByteArray(resource, contextMap);
        return FINISHED;
    }
}
