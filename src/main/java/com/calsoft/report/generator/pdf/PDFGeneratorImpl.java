/**
 * 
 */
package com.calsoft.report.generator.pdf;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParserImpl;
import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParserImpl.STATUS;
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
public class PDFGeneratorImpl implements PDFGenerator{
	
	  private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	  private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	  private static Font greenFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL,BaseColor.GREEN);
	  private static Font lightGrayFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL,BaseColor.GRAY);
	  private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	  
	  
	  public void writePdfFile(String outputFile, Map<String,Map<String,List<String>>> analyzedreport){
		  try {
		      	
		      Document document = new Document();
		      PdfWriter.getInstance(document, new FileOutputStream(outputFile));
		      document.open();
		      Paragraph chunk = new Paragraph("Test Run Result Summary",catFont);
		      chunk.setSpacingBefore(50);
		      chunk.setAlignment(Element.ALIGN_CENTER);
		      chunk.setSpacingAfter(20);
		      document.add(chunk);
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
		      
		      Paragraph chunk3 = new Paragraph("Yet to be Automated Scenarios",catFont);
		      chunk3.setSpacingBefore(30);
		      chunk3.setAlignment(Element.ALIGN_CENTER);
		      chunk3.setSpacingAfter(20);
		      document.add(chunk3);
		      
		      addResultPassedOrFailedDetailedPage(document,analyzedreport,STATUS.UNDEFINED);
		      	      
		      document.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }  
	  }

	  
	  private static void addResultSummaryPage(Document document, Map<String,Map<String,List<String>>> parsedJson) throws DocumentException{
		  PdfPTable summaryResultTable = new PdfPTable(5); // Sno., Section, Passed, Failed, Undefined
		  float[] colWidths = {0.5f,3f,1f,1f,1f};
		  summaryResultTable.setWidths(colWidths);
		  PdfPCell sno = new PdfPCell(new Phrase("Sno.",smallBold));
		  PdfPCell sectionCell = new PdfPCell(new Phrase("Section",smallBold));
		  PdfPCell passed = new PdfPCell(new Phrase("Passed",smallBold));
		  PdfPCell failed = new PdfPCell(new Phrase("Failed",smallBold));
		  PdfPCell undefined = new PdfPCell(new Phrase("Yet to be Automated",smallBold));		  
		  summaryResultTable.addCell(sno);
		  summaryResultTable.addCell(sectionCell);
		  summaryResultTable.addCell(passed);
		  summaryResultTable.addCell(failed);
		  summaryResultTable.addCell(undefined);		  
		  
		  int count=1;
		  int sumPassed = 0;
		  int sumFailed = 0;
		  int sumUndefined = 0;
		  int sumSkipped = 0;
		  for(String section:parsedJson.keySet()){
			  
			  PdfPCell snoCell = new PdfPCell(new Phrase(Integer.toString(count)));
			  PdfPCell cell = new PdfPCell(new Phrase(section));
			  
			  Integer passedScenariosForASection = parsedJson.get(section).get(CucumberReportParserImpl.STATUS.PASSED.getStatus()).size();
			  sumPassed += passedScenariosForASection;
			  PdfPCell passedCell = new PdfPCell(new Phrase(Integer.toString(passedScenariosForASection),greenFont));
			  
			  Integer failedScenariosForASection = parsedJson.get(section).get(CucumberReportParserImpl.STATUS.FAILED.getStatus()).size();
			  sumFailed += failedScenariosForASection;
			  PdfPCell failedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParserImpl.STATUS.FAILED.getStatus()).size()),redFont));
			  
			  Integer undefinedScenariosForASection = parsedJson.get(section).get(CucumberReportParserImpl.STATUS.UNDEFINED.getStatus()).size();
			  sumUndefined += undefinedScenariosForASection;
			  PdfPCell undefinedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParserImpl.STATUS.UNDEFINED.getStatus()).size()),lightGrayFont));
			  			  
			  summaryResultTable.addCell(snoCell);
			  summaryResultTable.addCell(cell);
			  summaryResultTable.addCell(passedCell);
			  summaryResultTable.addCell(failedCell);
			  summaryResultTable.addCell(undefinedCell);			  
			  count++;
		  }
		  
		  summaryResultTable.addCell(new Phrase(""));
		  summaryResultTable.addCell(new Phrase("Total : ",smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumPassed),smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumFailed),smallBold));
		  summaryResultTable.addCell(new Phrase(Integer.toString(sumUndefined),smallBold));	  
		  
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
			  scenariosColumnName = "Yet to be Automated Scenarios";
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
				status = CucumberReportParserImpl.STATUS.PASSED.getStatus();  
			  }else if(statusEnum.equals(STATUS.FAILED)){
				  status = CucumberReportParserImpl.STATUS.FAILED.getStatus();
			  }else if(statusEnum.equals(STATUS.UNDEFINED)){
				  status = CucumberReportParserImpl.STATUS.UNDEFINED.getStatus();
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
