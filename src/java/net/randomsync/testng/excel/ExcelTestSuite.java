package net.randomsync.testng.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelTestSuite {
	public String name;
	private List<ExcelTestCase> testCases;
	
	public ExcelTestSuite() {
		super();
		testCases = new ArrayList<ExcelTestCase>();
	}

	public ExcelTestSuite(String name) {
		super();
		this.name = name;
		testCases = new ArrayList<ExcelTestCase>();
	}

	public ExcelTestSuite(String name, List<ExcelTestCase> testCases) {
		super();
		this.name = name;
		this.testCases = testCases;
	}

	/**
	 * @return the testCases
	 */
	public List<ExcelTestCase> getTestCases() {
		return testCases;
	}


	/**
	 * @param testCases the testCases to set
	 */
	public void setTestCases(List<ExcelTestCase> testCases) {
		this.testCases = testCases;
	}


	public void addTestCase(ExcelTestCase testCase){
		this.testCases.add(testCase);
	}
	/**not implemented yet
	 * @param xlFilePath path of the excel file
	 * @param sheetName name of the worksheet 
	 * 
	 */
	public void populate(String xlFilePath, String sheetName){
		
	}

}
