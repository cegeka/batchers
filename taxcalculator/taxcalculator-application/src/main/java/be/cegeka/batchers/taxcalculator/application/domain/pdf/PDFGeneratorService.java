package be.cegeka.batchers.taxcalculator.application.domain.pdf;

import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static fr.opensagres.xdocreport.converter.ConverterTypeTo.PDF;
import static fr.opensagres.xdocreport.converter.ConverterTypeVia.XWPF;

@Service
public class PDFGeneratorService {

    private XDocReportRegistry xDocReportRegistry;

    @PostConstruct
    public void initialize() {
        xDocReportRegistry = XDocReportRegistry.getRegistry();
        xDocReportRegistry.initialize();
    }

    public synchronized byte[] generatePdfAsByteArray(Resource resource, Map<String, Object> contextMap) throws PDFGenerationException {
        try {
            final IXDocReport report = xDocReportRegistry.loadReport(resource.getInputStream(), TemplateEngineKind.Freemarker);

            final IContext context = report.createContext();
            context.putMap(contextMap);

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final Options options = Options.getTo(PDF).via(XWPF);
            report.convert(context, options, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException | XDocReportException e) {
            throw new PDFGenerationException(e);
        }
    }

}
