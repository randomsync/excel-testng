package net.randomsync.testng.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.xml.*;

public class ExcelSuiteParser {

	private File xlSource;
	private Map<String, Integer> excelTestDataMap;
	private File testClassesMap;

	/**
	 * Default constructor, sets the source file and initializes the test data
	 * map
	 * 
	 * @param xlSource
	 *            - the source Excel file
	 */
	public ExcelSuiteParser(File xlSource) {
		this.xlSource = xlSource;
		excelTestDataMap = new HashMap<String, Integer>();
		excelTestDataMap.put("headerRow", 0);
		excelTestDataMap.put("testIdCol", 0);
		excelTestDataMap.put("testNameCol", 1);
		excelTestDataMap.put("testDescCol", 2);
		excelTestDataMap.put("testParamCol", 3);
		excelTestDataMap.put("testConfigCol", 4);
	}

	public ExcelSuiteParser(File xlSource, Map<String, Integer> excelTestDataMap) {
		this.xlSource = xlSource;
		this.excelTestDataMap = excelTestDataMap;
	}

	/**
	 * Parses the Excel file into a TestNG XmlSuite and returns the suite. If
	 * there are multiple worksheets, all of them are parsed into a single
	 * XmlSuite and returned.
	 * 
	 * @param xlSource
	 *            - source Excel file
	 * @return a TestNG XmlSuite with all tests from the Excel file
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public XmlSuite getXmlSuite() throws InvalidFormatException, IOException {
		String name = xlSource.getName();
		name = name.substring(0, name.lastIndexOf("."));

		ExcelTestSuite suite = new ExcelTestSuite(name);
		suite.setTestCases(parseExcelTestCases(xlSource));

		return suite.getSuiteAsXmlSuite();
	}

	/**
	 * this is the main parser method that parses the Excel file and returns the
	 * tests within the file. Each row is considered as a test case with
	 * specific columns (specified by {@link #excelTestDataMap}) as test
	 * 
	 * @return a list of test cases in the Excel file
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public List<ExcelTestCase> parseExcelTestCases(File src)
			throws InvalidFormatException, IOException {
		List<ExcelTestCase> testCases = new ArrayList<ExcelTestCase>();

		FileInputStream fis = new FileInputStream(src);
		Workbook wb = WorkbookFactory.create(fis);

		// validate values and assign default if needed
		int headerRow = getMapValue("headerRow", 0);

		/*
		 * parse each sheet starting from headerRow
		 */
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(i);
			Row row = null;
			for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
				row = sheet.getRow(j);
				testCases.add(getExcelTestCaseFromRow(row));
			}
		}
		return testCases;
	}

	/**
	 * @param excelTestDataMap
	 *            - the Map that will be used to parse Excel file(s)
	 */
	public void setExcelTestDataMap(Map<String, Integer> excelTestDataMap) {
		this.excelTestDataMap = excelTestDataMap;
	}

	private ExcelTestCase getExcelTestCaseFromRow(Row row) {
		int testIdCol = getMapValue("testIdCol", 0);
		int testNameCol = getMapValue("testNameCol", 1);
		int testDescCol = getMapValue("testDescCol", 2);
		int testParamCol = getMapValue("testParamCol", 3);
		int testConfigCol = getMapValue("testConfigCol", 4);

		return new ExcelTestCase(Integer.parseInt(row.getCell(testIdCol)
				.getStringCellValue()), // test id
				row.getCell(testNameCol).getStringCellValue(), // test name
				row.getCell(testDescCol).getStringCellValue(), // description
				row.getCell(testParamCol).getStringCellValue(), // parameters
				row.getCell(testConfigCol).getStringCellValue()); // configuration

	}

	private int getMapValue(String key, int defaultVal) {
		int value;
		try {
			value = this.excelTestDataMap.get(key);
		} catch (NullPointerException npe) {
			return defaultVal;
		}
		return value;
	}

}