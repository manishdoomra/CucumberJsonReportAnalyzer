/**
 * 
 */
package com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author manish.doomra
 *
 */
public class CucumberReportParser {
	
	public enum STATUS {
		//Enum Constants
		PASSED("passed"),
		FAILED("failed"),
		UNDEFINED("undefined"),
		SKIPPED("skipped");
		
		private String status;
		
		private STATUS(String status){
			this.status = status;
		}
		
		public String getStatus(){
			return this.status;
		}		
	}
			
	
	public static Map<String,Map<String,List<String>>> parseAndAnalyzeCucumberJsonFile(String cucumbergeneratedJsonFile) throws FileNotFoundException, IOException, ParseException{
		
		Map<String,Map<String,List<String>>> resultOfParsing = new HashMap<String, Map<String,List<String>>>(); //<Section, <Passed/Failed, List[tests]>>;
		JSONParser jsonParser = new JSONParser();
		Object cucumberJsonFileContent = jsonParser.parse(new FileReader(cucumbergeneratedJsonFile));
		JSONArray cucumberJsonFileContentJSONArray = (JSONArray)cucumberJsonFileContent;
		JSONObject firstElementInCucumberJsonFileContentJSONArray = (JSONObject) cucumberJsonFileContentJSONArray.get(0);  // It will always be 0th element
		JSONArray elementsInJSONFile = (JSONArray)firstElementInCucumberJsonFileContentJSONArray.get("elements");
				
		// Browse through JsonFile elements[Tests/Scenarios] and prepare the list of passed ones and failed ones
		for(int i=0;i<elementsInJSONFile.size();i++) {
			JSONObject scenarioJsonObject = (JSONObject) elementsInJSONFile.get(i);
			String scenarioName = (String)scenarioJsonObject.get("name");
			JSONArray tagsArray = (JSONArray)scenarioJsonObject.get("tags");
			
			//Browse through tags and fetch the section name
			JSONObject tag = (JSONObject)tagsArray.get(2); // 3rd tag is always section in 4.1 F-TAF framework
			String sectionName  = (String)tag.get("name");
			
			if(resultOfParsing.get(sectionName)==null) { // First time data-structure initialization for a section
				Map<String,List<String>> passedFailedScenariosForASection = new HashMap<String, List<String>>();
				List<String> passedScenariosListForASection = new ArrayList<String>();
				List<String> failedScenariosListForASection = new ArrayList<String>();
				passedFailedScenariosForASection.put(STATUS.PASSED.getStatus(), passedScenariosListForASection);
				passedFailedScenariosForASection.put(STATUS.FAILED.getStatus(), failedScenariosListForASection);
				resultOfParsing.put(sectionName, passedFailedScenariosForASection);
			}
			
			if(allStepsPassedOrNot(scenarioJsonObject)){ // If all steps are passed for a scenario
				resultOfParsing.get(sectionName).get(STATUS.PASSED.getStatus()).add(scenarioName);			
			}else{
				resultOfParsing.get(sectionName).get(STATUS.FAILED.getStatus()).add(scenarioName);
			}		
			
		}
		
		return resultOfParsing;		
	}
	
	/**
	 * 
	 * @return true if all steps of a scenario passed otherwise false
	 */
	private static boolean allStepsPassedOrNot(JSONObject scenarioJsonObject){
		boolean allStepsPassed = true;
		JSONArray stepsInAScenario = (JSONArray)scenarioJsonObject.get("steps");
		if(stepsInAScenario == null){
			return false;
		}
		//Iterate through all steps and if any of step is failed/skipped return false for that scenario
		for(int i=0;i<stepsInAScenario.size();i++){
			JSONObject step = (JSONObject)stepsInAScenario.get(i);
			JSONObject stepResult = (JSONObject) step.get("result");
			String stepResultStatus =(String) stepResult.get("status");
			STATUS status = STATUS.valueOf(stepResultStatus.toUpperCase(Locale.ENGLISH)); 
			switch(status){
				case PASSED: 
							 break;
				case FAILED:
							 allStepsPassed = false;
							 break;
				case UNDEFINED:
							 allStepsPassed = false;
							 break;
				case SKIPPED:
							allStepsPassed = false;
							break;
				default:
						throw new RuntimeException("The step result status is not [passed, failed, undefined] : "+stepResultStatus);
			}
		}
		return allStepsPassed;
	}
	
	
	public static void main(String[] args){
		try {
			Map<String,Map<String,List<String>>> parsedJson = parseAndAnalyzeCucumberJsonFile("C:/Nexenta/cucumber_full_run_report.json");
			System.out.println(parsedJson.get("@Pools").get(STATUS.FAILED.getStatus()).size());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
