package com.calsoft.report.generator.pdf;

import java.util.List;
import java.util.Map;

public interface PDFGenerator {
	
	public void writePdfFile(String outputFile, Map<String,Map<String,List<String>>> analyzedreport);

}
