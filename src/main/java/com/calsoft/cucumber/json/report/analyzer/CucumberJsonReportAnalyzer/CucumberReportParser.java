/**
 * 
 */
package com.calsoft.cucumber.json.report.analyzer.CucumberJsonReportAnalyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		UNDEFINED("undefined");
		
		private String status;
		
		private STATUS(String status){
			this.status = status;
		}
		
		public String getStatus(){
			return this.status;
		}		
	}
			
	
	private static Map<String,List<String>> parseAndAnalyzeCucumberJsonFile(String cucumbergeneratedJsonFile) throws FileNotFoundException, IOException, ParseException{
		Map<String,List<String>> resultOfParsing = new HashMap<String, List<String>>(); //Section, <Passed/Failed, List[tests]>;		
		JSONParser jsonParser = new JSONParser();
		Object cucumberJsonFileContent = jsonParser.parse(new FileReader(cucumbergeneratedJsonFile));
		JSONArray cucumberJsonFileContentJSONArray = (JSONArray)cucumberJsonFileContent;
		JSONObject firstElementInCucumberJsonFileContentJSONArray = (JSONObject) cucumberJsonFileContentJSONArray.get(0);  // It will always be 0th element
		JSONArray elementsInJSONFile = (JSONArray)firstElementInCucumberJsonFileContentJSONArray.get("elements");
		
		List<String> failedScenarios = new ArrayList<String>();
		List<String> passedScenarios = new ArrayList<String>();
		
		// Browse through JsonFile elements[Tests/Scenarios] and prepare the list of passed ones and failed ones
		for(int i=0;i<elementsInJSONFile.size();i++){
			JSONObject scenarioJsonObject = (JSONObject) elementsInJSONFile.get(i);
			String scenarioName = (String)scenarioJsonObject.get("name");
			System.out.println("\n\n"+scenarioName);
			JSONArray tagsArray = (JSONArray)scenarioJsonObject.get("tags");
			//Browse through tags and fetch the section name
			for(int j=0;j<tagsArray.size();j++){
				JSONObject tag = (JSONObject)tagsArray.get(j);
				if(j==2){
					System.out.println(tag.get("name"));
				}
			}
			if(allStepsPassedOrNot(scenarioJsonObject)){
				passedScenarios.add(scenarioName);
			}else{
				failedScenarios.add(scenarioName);
			}
		}
		
		resultOfParsing.put(STATUS.PASSED.getStatus(), passedScenarios);
		System.out.println("Passed Scenarios : "+passedScenarios.size());
		resultOfParsing.put(STATUS.FAILED.getStatus(), failedScenarios);
		System.out.println("Failed Scenarios : "+failedScenarios.size());
		return resultOfParsing;		
	}
	
	/**
	 * 
	 * @return true if all steps of a scenario passed otherwise false
	 */
	private static boolean allStepsPassedOrNot(JSONObject scenarioJsonObject){
		boolean allStepsPassed = true;
		JSONArray stepsInAScenario = (JSONArray)scenarioJsonObject.get("steps");
		//Iterate through all steps and if any of step is failed/skipped return false for that scenario
		for(int i=0;i<stepsInAScenario.size();i++){
			JSONObject step = (JSONObject)stepsInAScenario.get(i);
			JSONObject stepResult = (JSONObject) step.get("result");
			String stepResultStatus =(String) stepResult.get("status");
			STATUS status = STATUS.valueOf(stepResultStatus.toUpperCase(Locale.ENGLISH)); 
			switch(status){
				case PASSED: 
							 System.out.println("Step passed");
							 break;
				case FAILED:
							 System.out.println("Step failed"); 
							 allStepsPassed = false;
							 break;
				case UNDEFINED:
							 System.out.println("Step undefined"); 
							 allStepsPassed = false;
							 break;
				default:
						throw new RuntimeException("The step result status is not [passed, failed, undefined] : "+stepResultStatus);
			}
		}
		return allStepsPassed;
	}
	
	
	public static void analyze(String jsonFile){
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(jsonFile));			
			JSONArray jsonArray = (JSONArray)obj;
			System.out.println(jsonArray.size());
			
			JSONObject jsonObj = (JSONObject) jsonArray.get(0);
			JSONArray jsonArray1 = (JSONArray)jsonObj.get("elements");
			System.out.println(jsonArray1);
			JSONObject objJson = (JSONObject)jsonArray1.get(0);
			System.out.println(objJson.get("name"));
			JSONArray jsonSteps = (JSONArray)objJson.get("steps");
			System.out.println(jsonSteps);
			Iterator steps = jsonSteps.iterator();
			while(steps.hasNext()){
				JSONObject array = (JSONObject)steps.next();
				JSONObject aa = (JSONObject)array.get("result");
				System.out.println(aa.get("status"));	
			}
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
	
	public static void main(String[] args){
		try {
			Map<String,List<String>> parsedJson = parseAndAnalyzeCucumberJsonFile("C:/Nexenta/cucumber_report_datasetgroups_pools.json");
			
			
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
