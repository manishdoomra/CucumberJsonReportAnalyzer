/**
 * 
 */
package com.calsoft.report.generator.pdf;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParser;
import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParser.STATUS;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author manish.doomra
 *
 */
public class PDFGenerator {
	
	  private static String FILE = "c:/temp/FirstPdf.pdf";
	  private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	      Font.BOLD);
	  private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	      Font.NORMAL, BaseColor.RED);
	  private static Font greenFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	      Font.NORMAL,BaseColor.GREEN);
	  private static Font lightGrayFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
		      Font.NORMAL,BaseColor.GRAY);
	  private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	      Font.BOLD);

	  public static void main(String[] args) {
	    try {
	      Document document = new Document();
	      PdfWriter.getInstance(document, new FileOutputStream(FILE));
	      document.open();
	      Paragraph chunk = new Paragraph("Test Run Result Summary",catFont);
	      chunk.setSpacingBefore(50);
	      chunk.setAlignment(Element.ALIGN_CENTER);
	      chunk.setSpacingAfter(20);
	      document.add(chunk);
	      Map<String,Map<String,List<String>>> analyzedreport = CucumberReportParser.parseAndAnalyzeCucumberJsonFile("C:/Manish/Nexenta/source/json_reports/cucumber_report_full_run.json");
	      addResultSummaryPage(document,analyzedreport);
	      
	      Paragraph chunk1 = new Paragraph("Passed Scenarios",catFont);
	      chunk1.setSpacingBefore(30);
	      chunk1.setAlignment(Element.ALIGN_CENTER);
	      chunk1.setSpacingAfter(20);
	      document.add(chunk1);
	      
	      addResultPassedOrFailedDetailedPage(document,analyzedreport,STATUS.PASSED);
	      
	      Paragraph chunk2 = new Paragraph("Failed Scenarios",catFont);
	      chunk2.setSpacingBefore(30);
	      chunk2.setAlignment(Element.ALIGN_CENTER);
	      chunk2.setSpacingAfter(20);
	      document.add(chunk2);
	      
	      addResultPassedOrFailedDetailedPage(document,analyzedreport,STATUS.FAILED);
	      
	      Paragraph chunk3 = new Paragraph("Undefined Scenarios",catFont);
	      chunk3.setSpacingBefore(30);
	      chunk3.setAlignment(Element.ALIGN_CENTER);
	      chunk3.setSpacingAfter(20);
	      document.add(chunk3);
	      
	      addResultPassedOrFailedDetailedPage(document,analyzedreport,STATUS.UNDEFINED);
	      
	      Paragraph chunk4 = new Paragraph("Skipped Scenarios",catFont);
	      chunk4.setSpacingBefore(30);
	      chunk4.setAlignment(Element.ALIGN_CENTER);
	      chunk4.setSpacingAfter(20);
	      document.add(chunk4);
	      
	      addResultPassedOrFailedDetailedPage(document,analyzedreport,STATUS.SKIPPED);	      
	      
	      document.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  
	  private static void addResultSummaryPage(Document document, Map<String,Map<String,List<String>>> parsedJson) throws DocumentException{
		  PdfPTable summaryResultTable = new PdfPTable(6); // Sno., Section, Passed, Failed, Undefined, Skipped
		  float[] colWidths = {0.5f,3f,1f,1f,1f,1f};
		  summaryResultTable.setWidths(colWidths);
		  PdfPCell sno = new PdfPCell(new Phrase("Sno.",smallBold));
		  PdfPCell sectionCell = new PdfPCell(new Phrase("Section",smallBold));
		  PdfPCell passed = new PdfPCell(new Phrase("Passed",smallBold));
		  PdfPCell failed = new PdfPCell(new Phrase("Failed",smallBold));
		  PdfPCell undefined = new PdfPCell(new Phrase("Undefined",smallBold));
		  PdfPCell skipped = new PdfPCell(new Phrase("Skipped",smallBold));
		  summaryResultTable.addCell(sno);
		  summaryResultTable.addCell(sectionCell);
		  summaryResultTable.addCell(passed);
		  summaryResultTable.addCell(failed);
		  summaryResultTable.addCell(undefined);
		  summaryResultTable.addCell(skipped);
		  
		  int count=1;
		  int sumPassed = 0;
		  int sumFailed = 0;
		  int sumUndefined = 0;
		  int sumSkipped = 0;
		  for(String section:parsedJson.keySet()){
			  
			  PdfPCell snoCell = new PdfPCell(new Phrase(Integer.toString(count)));
			  PdfPCell cell = new PdfPCell(new Phrase(section));
			  
			  Integer passedScenariosForASection = parsedJson.get(section).get(CucumberReportParser.STATUS.PASSED.getStatus()).size();
			  sumPassed += passedScenariosForASection;
			  PdfPCell passedCell = new PdfPCell(new Phrase(Integer.toString(passedScenariosForASection),greenFont));
			  
			  Integer failedScenariosForASection = parsedJson.get(section).get(CucumberReportParser.STATUS.FAILED.getStatus()).size();
			  sumFailed += failedScenariosForASection;
			  PdfPCell failedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParser.STATUS.FAILED.getStatus()).size()),redFont));
			  
			  Integer undefinedScenariosForASection = parsedJson.get(section).get(CucumberReportParser.STATUS.UNDEFINED.getStatus()).size();
			  sumUndefined += undefinedScenariosForASection;
			  PdfPCell undefinedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParser.STATUS.UNDEFINED.getStatus()).size()),lightGrayFont));
			  
			  Integer skippedScenariosForASection = parsedJson.get(section).get(CucumberReportParser.STATUS.SKIPPED.getStatus()).size();
			  sumSkipped += skippedScenariosForASection;
			  PdfPCell skippedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParser.STATUS.SKIPPED.getStatus()).size()),lightGrayFont));
			  
			  summaryResultTable.addCell(snoCell);
			  summaryResultTable.addCell(cell);
			  summaryResultTable.addCell(passedCell);
			  summaryResultTable.addCell(failedCell);
			  summaryResultTable.addCell(undefinedCell);
			  summaryResultTable.addCell(skippedCell);
			  count++;
		  }
		  
		  summaryResultTable.addCell(new Phrase(""));
		  summaryResultTable.addCell(new Phrase("Total : ",smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumPassed),smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumFailed),smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumUndefined),smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumSkipped),smallBold));
		  
		  summaryResultTable.setHeaderRows(1);
		  document.add(summaryResultTable);
	  }
	  
	  private static void addResultPassedOrFailedDetailedPage(Document document, Map<String,Map<String,List<String>>> parsedJson, STATUS statusEnum) throws DocumentException{
		  PdfPTable passedDetailedResultTable = new PdfPTable(3); // Sno., Section, Scenarios
		  float[] colWidths = {0.5f,1f,3f};
		  passedDetailedResultTable.setWidths(colWidths);
		  PdfPCell sno = new PdfPCell(new Phrase("Sno.",smallBold));
		  PdfPCell sectionCell = new PdfPCell(new Phrase("Section",smallBold));
		  String scenariosColumnName="";
		  if(statusEnum.equals(STATUS.PASSED)){
			 scenariosColumnName =  "Passed Scenarios";
		  }else if(statusEnum.equals(STATUS.FAILED)){
			  scenariosColumnName = "Failed Scenarios";
		  }else if(statusEnum.equals(STATUS.UNDEFINED)){
			  scenariosColumnName = "Undefined Scenarios";
		  }else if(statusEnum.equals(STATUS.SKIPPED)){
			  scenariosColumnName = "Skipped Scenarios";
		  }
		  PdfPCell scenariosCell = new PdfPCell(new Phrase(scenariosColumnName,smallBold));
		  
		  passedDetailedResultTable.addCell(sno);
		  passedDetailedResultTable.addCell(sectionCell);
		  passedDetailedResultTable.addCell(scenariosCell);
		  
		  int count=1;
		  for(String section:parsedJson.keySet()){
			  PdfPCell snoCell = new PdfPCell(new Phrase(Integer.toString(count)));
			  PdfPCell cell = new PdfPCell(new Phrase(section));
			  PdfPTable nestedTable = new PdfPTable(1);
			  String status = "";
			  if(statusEnum.equals(STATUS.PASSED)){
				status = CucumberReportParser.STATUS.PASSED.getStatus();  
			  }else if(statusEnum.equals(STATUS.FAILED)){
				  status = CucumberReportParser.STATUS.FAILED.getStatus();
			  }else if(statusEnum.equals(STATUS.UNDEFINED)){
				  status = CucumberReportParser.STATUS.UNDEFINED.getStatus();
			  }else if(statusEnum.equals(STATUS.SKIPPED)){
				  status = CucumberReportParser.STATUS.SKIPPED.getStatus();
			  }
			  
			  for(String scenarios : parsedJson.get(section).get(status)){
				  if(scenarios==null||scenarios.isEmpty()){
					  continue;
				  }
				  PdfPCell scenarioCell = new PdfPCell(new Phrase(scenarios));
				  nestedTable.addCell(scenarioCell);
			  }
			  if(parsedJson.get(section).get(status).size()>0){
				  passedDetailedResultTable.addCell(snoCell);			  
				  passedDetailedResultTable.addCell(cell);
				  passedDetailedResultTable.addCell(nestedTable);
				  count++;
			  }
		  }
		  
		  passedDetailedResultTable.setHeaderRows(1);
		  document.add(passedDetailedResultTable);
	  }
	  

}
