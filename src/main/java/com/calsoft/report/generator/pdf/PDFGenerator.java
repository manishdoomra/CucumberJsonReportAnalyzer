/**
 * 
 */
package com.calsoft.report.generator.pdf;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer.CucumberReportParser;
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
	      Map<String,Map<String,List<String>>> analyzedreport = CucumberReportParser.parseAndAnalyzeCucumberJsonFile("C:/Nexenta/cucumber_full_run_report.json");
	      addResultSummaryPage(document,analyzedreport);
	      
	      Paragraph chunk1 = new Paragraph("Passed Scenarios",catFont);
	      chunk1.setSpacingBefore(30);
	      chunk1.setAlignment(Element.ALIGN_CENTER);
	      chunk1.setSpacingAfter(20);
	      document.add(chunk1);
	      
	      addResultPassedOrFailedDetailedPage(document,analyzedreport,true);
	      
	      Paragraph chunk2 = new Paragraph("Failed Scenarios",catFont);
	      chunk2.setSpacingBefore(30);
	      chunk2.setAlignment(Element.ALIGN_CENTER);
	      chunk2.setSpacingAfter(20);
	      document.add(chunk2);
	      
	      addResultPassedOrFailedDetailedPage(document,analyzedreport,false);
	      
	      document.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  
	  private static void addResultSummaryPage(Document document, Map<String,Map<String,List<String>>> parsedJson) throws DocumentException{
		  PdfPTable summaryResultTable = new PdfPTable(4); // Sno., Section, Passed, Failed
		  PdfPCell sno = new PdfPCell(new Phrase("Sno.",smallBold));
		  PdfPCell sectionCell = new PdfPCell(new Phrase("Section",smallBold));
		  PdfPCell passed = new PdfPCell(new Phrase("Passed",smallBold));
		  PdfPCell failed = new PdfPCell(new Phrase("Failed",smallBold));
		  summaryResultTable.addCell(sno);
		  summaryResultTable.addCell(sectionCell);
		  summaryResultTable.addCell(passed);
		  summaryResultTable.addCell(failed);
		  
		  int count=1;
		  for(String section:parsedJson.keySet()){
			  PdfPCell snoCell = new PdfPCell(new Phrase(Integer.toString(count)));
			  PdfPCell cell = new PdfPCell(new Phrase(section));
			  PdfPCell passedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParser.STATUS.PASSED.getStatus()).size()),greenFont));
			  PdfPCell failedCell = new PdfPCell(new Phrase(Integer.toString(parsedJson.get(section).get(CucumberReportParser.STATUS.FAILED.getStatus()).size()),redFont));
			  summaryResultTable.addCell(snoCell);
			  summaryResultTable.addCell(cell);
			  summaryResultTable.addCell(passedCell);
			  summaryResultTable.addCell(failedCell);
			  count++;
		  }
		  
		  summaryResultTable.setHeaderRows(1);
		  document.add(summaryResultTable);
	  }
	  
	  private static void addResultPassedOrFailedDetailedPage(Document document, Map<String,Map<String,List<String>>> parsedJson, boolean isPassed) throws DocumentException{
		  PdfPTable passedDetailedResultTable = new PdfPTable(3); // Sno., Section, Passed Scenarios
		  float[] colWidths = {1f,1f,3f};
		  passedDetailedResultTable.setWidths(colWidths);
		  PdfPCell sno = new PdfPCell(new Phrase("Sno.",smallBold));
		  PdfPCell sectionCell = new PdfPCell(new Phrase("Section",smallBold));
		  String scenariosColumnName="";
		  if(isPassed){
			 scenariosColumnName =  "Passed Scenarios";
		  }else{
			  scenariosColumnName = "Failed Scenarios";
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
			  if(isPassed){
				status = CucumberReportParser.STATUS.PASSED.getStatus();  
			  }else{
				  status = CucumberReportParser.STATUS.FAILED.getStatus();
			  }
			  
			  for(String scenarios : parsedJson.get(section).get(status)){
				  PdfPCell scenarioCell = new PdfPCell(new Phrase(scenarios));
				  nestedTable.addCell(scenarioCell);
			  }
			  passedDetailedResultTable.addCell(snoCell);
			  passedDetailedResultTable.addCell(cell);
			  passedDetailedResultTable.addCell(nestedTable);
			  count++;
		  }
		  
		  passedDetailedResultTable.setHeaderRows(1);
		  document.add(passedDetailedResultTable);
	  }
	  

}
