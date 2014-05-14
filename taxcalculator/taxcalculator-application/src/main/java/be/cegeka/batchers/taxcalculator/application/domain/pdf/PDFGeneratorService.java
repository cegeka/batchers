package be.cegeka.batchers.taxcalculator.application.domain.pdf;

import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

import static fr.opensagres.xdocreport.converter.ConverterTypeTo.PDF;
import static fr.opensagres.xdocreport.converter.ConverterTypeVia.XWPF;

@Service
public class PDFGeneratorService {

    public byte[] generatePdfAsByteArray(File template, Map<String, Object> contextMap) throws IOException, XDocReportException {


        final InputStream in = new FileInputStream(template);
        final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);

        final IContext context = report.createContext();
        context.putMap(contextMap);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final Options options = Options.getTo(PDF).via(XWPF);
        report.convert(context, options, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

}
