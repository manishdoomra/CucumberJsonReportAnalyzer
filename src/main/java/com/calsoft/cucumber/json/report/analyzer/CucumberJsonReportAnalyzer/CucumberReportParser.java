package com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

public interface CucumberReportParser {
	public Map<String,Map<String,List<String>>> parseAndAnalyzeCucumberJsonFile(String cucumbergeneratedJsonFile) throws FileNotFoundException, IOException, ParseException;
}
