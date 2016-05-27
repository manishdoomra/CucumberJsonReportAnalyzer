package com.calsoft.report.generator.pdf;

import static com.calsoft.report.generator.pdf.Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParserImpl;
import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParser;


@Component
public class ApplicationClass {
	
	private String yamlFileName= "";
	
	public String getYamlFileName() {
		return yamlFileName;
	}

	public void setYamlFileName(String yamlFileName) {
		this.yamlFileName = yamlFileName;
	}

	public Map<String, List<String>> getYamlContent(){
		Map<String, List<String>> map = (Map<String, List<String>>) YamlParser.load(ClassLoader.getSystemResourceAsStream(this.yamlFileName));
		return map;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("spring.xml");
		ApplicationClass application = appContext.getBean("application", ApplicationClass.class);
		CucumberReportParser reportParser = appContext.getBean("cucumberReportAnalyzer", CucumberReportParserImpl.class);
		PDFGenerator pdfGenerator = appContext.getBean("pdfGenerator",PDFGeneratorImpl.class);
		Map<String, List<String>> map = application.getYamlContent();	
		Map<String,Map<String,List<String>>> analyzedreport = new HashMap<String, Map<String,List<String>>>();
		for(String s : map.get(JENKINS_JOBS_PROPERTY)){
			String fileName = map.get(BASE_PATH_PROPERTY) + File.separator + s;
			analyzedreport.putAll(reportParser.parseAndAnalyzeCucumberJsonFile(fileName));
		}
		String outputFileName = map.get(OUTPUT_PATH_PROPERTY) + File.separator + "JSON_REPORT_"+ new Date()+".pdf";
		pdfGenerator.writePdfFile(outputFileName, analyzedreport);
				
	}

}
