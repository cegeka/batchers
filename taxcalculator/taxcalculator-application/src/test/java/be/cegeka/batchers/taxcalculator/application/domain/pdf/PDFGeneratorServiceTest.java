package be.cegeka.batchers.taxcalculator.application.domain.pdf;

import fr.opensagres.xdocreport.core.XDocReportException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;


public class PDFGeneratorServiceTest {

    private PDFGeneratorService pdfGeneratorService;

    @Before
    public void initPdfGeneratorService() throws InterruptedException {
        pdfGeneratorService = new PDFGeneratorService();
        pdfGeneratorService.initialize();
        waitForXDocReportToInitialize();
    }

    @Test
    public void given_aWordTemplate_whenConvertingToPdf_thenTheSamePdfIsGenerated() throws IOException, XDocReportException, PDFGenerationException {
        Map<String, Object> context = new HashMap<>();
        context.put("test", "cegeka-batchers");

        byte[] actual = pdfGeneratorService.generatePdfAsByteArray(new ClassPathResource("test_template.docx"), context);

        assertThat(PDDocument.load(new ByteArrayInputStream(actual)))
                .containsText("cegeka-batchers is working");
    }

    @Test
    public void pdfGeneratorServiceIsThreadSafe() throws InterruptedException, ExecutionException {
        Set<Future<byte[]>> results = doPdfGenerationInMultipleThreads(10, pdfGeneratorService);

        getTheResultsWhichThrowsAnExceptionInMultipleThreads(results);
        assertThat("no exception has been thrown").isNotEmpty();
    }

    private Set<Future<byte[]>> doPdfGenerationInMultipleThreads(int numberOfSimultaneousThreads, PDFGeneratorService pdfGeneratorService) throws InterruptedException {
        CountDownLatch startCountDownLatch = new CountDownLatch(1);
        CountDownLatch endCountDownLatch = new CountDownLatch(numberOfSimultaneousThreads);
        Set<Future<byte[]>> results = createCallsToPdfGeneratorServiceMultiThreaded(pdfGeneratorService, numberOfSimultaneousThreads, startCountDownLatch, endCountDownLatch);
        startToMultithread(startCountDownLatch);
        waitForAllThreadsToFinish(endCountDownLatch);
        return results;
    }

    private void getTheResultsWhichThrowsAnExceptionInMultipleThreads(Set<Future<byte[]>> results) throws InterruptedException, ExecutionException {
        for (Future<byte[]> result : results) {
            result.get();
        }
    }

    private void waitForAllThreadsToFinish(CountDownLatch endCountDownLatch) throws InterruptedException {
        endCountDownLatch.await();
    }

    private void startToMultithread(CountDownLatch startCountDownLatch) {
        startCountDownLatch.countDown();
    }

    private Set<Future<byte[]>> createCallsToPdfGeneratorServiceMultiThreaded(PDFGeneratorService pdfGeneratorService, int numberOfSimultaneousThreads, CountDownLatch startCountDownLatch, CountDownLatch endCountDownLatch) {
        Set<Future<byte[]>> results = new HashSet<>();

        ExecutorService pool = Executors.newFixedThreadPool(numberOfSimultaneousThreads);
        for (int i = 0; i < numberOfSimultaneousThreads; i++) {
            Callable<byte[]> pdfGeneratorServiceForMultipleThreads = createPdfGeneratorServiceForMultipleThreads(pdfGeneratorService, startCountDownLatch, endCountDownLatch);
            results.add(pool.submit(pdfGeneratorServiceForMultipleThreads));
        }
        return results;
    }

    private Callable<byte[]> createPdfGeneratorServiceForMultipleThreads(final PDFGeneratorService pdfGeneratorService, CountDownLatch startCountDownLatch, CountDownLatch endCountDownLatch) {
        return new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                try {
                    startCountDownLatch.await();

                    Map<String, Object> context = new HashMap<>();
                    context.put("test", "cegeka-batchers");

                    return pdfGeneratorService.generatePdfAsByteArray(new ClassPathResource("test_template.docx"), context);
                } finally {
                    endCountDownLatch.countDown();
                }
            }
        };
    }

    private void waitForXDocReportToInitialize() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(2, TimeUnit.SECONDS);
    }
}
