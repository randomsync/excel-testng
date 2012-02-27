package net.randomsync.testng.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.xml.*;

public class ExcelSuiteParser implements IExcelFileParser {

	public static final String SUITE_NAME_STR = "Suite Name";
	public static final String SUITE_PARAMS_STR = "Suite Parameters";
	public static final String TEST_ID_STR = "Id";
	public static final String TEST_NAME_STR = "Test Name";
	public static final String TEST_DESC_STR = "Test Description";
	public static final String TEST_PARAMS_STR = "Test Parameters";
	public static final String TEST_CONFIG_STR = "Test Configuration";

	// formatter to format cell data
	public DataFormatter formatter = new DataFormatter();

	private Map<String, Integer> excelTestDataMap;

	/**
	 * Default constructor, sets the source file and initializes the test data
	 * map
	 * 
	 * @param xlSource
	 *            - the source Excel file
	 */
	public ExcelSuiteParser() {
		excelTestDataMap = new HashMap<String, Integer>();
		excelTestDataMap.put("headerRow", 9);
		excelTestDataMap.put("testIdCol", 0);
		excelTestDataMap.put("testNameCol", 2);
		excelTestDataMap.put("testDescCol", 3);
		excelTestDataMap.put("testParamCol", 4);
		excelTestDataMap.put("testConfigCol", 5);
	}

	public ExcelSuiteParser(Map<String, Integer> excelTestDataMap) {
		this.excelTestDataMap = excelTestDataMap;
	}

	/**
	 * Parses the Excel file into a TestNG XmlSuite and returns the suite. If
	 * there are multiple worksheets, each worksheet is parsed into a separate
	 * XmlSuite
	 * 
	 * @param file
	 *            source Excel file
	 * @param loadClasses
	 *            whether to load test classes
	 * @return a List of TestNG XmlSuite with all tests from the Excel file
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public List<XmlSuite> getXmlSuites(File file, boolean loadClasses)
			throws InvalidFormatException, IOException {

		List<XmlSuite> suites = new ArrayList<XmlSuite>();

		FileInputStream fis = new FileInputStream(file);
		Workbook wb = WorkbookFactory.create(fis);

		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(i);
			String name = getSuiteName(sheet);
			ExcelTestSuite suite = new ExcelTestSuite(name);
			suite.setSuiteParams(getSuiteParams(sheet));
			suite.setTestCases(parseExcelTestCases(sheet));
			suites.add(suite.getSuiteAsXmlSuite(loadClasses));
		}

		return suites;
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
	public List<ExcelTestCase> parseExcelTestCases(Sheet sheet)
			throws InvalidFormatException, IOException {

		List<ExcelTestCase> testCases = new ArrayList<ExcelTestCase>();

		// validate values and assign default if needed
		//int headerRow = getMapValue("headerRow", 0);
		int headerRow = getHeaderRow(sheet);
		int testIdCol = getMapValue("testIdCol", 0);

		/*
		 * parse the sheet starting from headerRow. Each row is a test case and
		 * needs to be added if test id is not blank
		 */
		Row row = null;
		for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
			row = sheet.getRow(j);
			if (row != null) {
				if (!formatter.formatCellValue(row.getCell(testIdCol))
						.isEmpty()) {
					ExcelTestCase tc = null;
					try {
						tc = getExcelTestCaseFromRow(row, formatter);
						// TODO evaluate formulae

					} catch (Exception e) {
						// TODO add specific exception handler
						e.printStackTrace();
						// skip the current row and continue
						continue;
					}
					testCases.add(tc);
				}
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

	public ExcelTestCase getExcelTestCaseFromRow(Row row,
			DataFormatter formatter) {
		int testIdCol = getMapValue("testIdCol", 0);
		int testNameCol = getMapValue("testNameCol", 1);
		int testDescCol = getMapValue("testDescCol", 2);
		int testParamCol = getMapValue("testParamCol", 3);
		int testConfigCol = getMapValue("testConfigCol", 4);

		return new ExcelTestCase(formatter.formatCellValue(row
				.getCell(testIdCol)), // test id
				formatter.formatCellValue(row.getCell(testNameCol)), // test
																		// name
				formatter.formatCellValue(row.getCell(testDescCol)), // description
				formatter.formatCellValue(row.getCell(testParamCol)), // parameters
				formatter.formatCellValue(row.getCell(testConfigCol)) // configuration
		);

	}

	public String getSuiteName(Sheet sheet) {
		// parse the worksheet and look for first cell containing text
		// "Suite Name". the next cell is supposed to have the actual name of
		// the suite. Returns blank string if not found
		// TODO add custom parser functionality
		Row row;
		String suiteName = "";
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row != null){	//skip blank rows
				for (int j = 0; j <= row.getLastCellNum(); j++) {
					String cellValue = formatter.formatCellValue(row.getCell(j));
					if (SUITE_NAME_STR.equals(cellValue)) {
						suiteName = formatter.formatCellValue(row.getCell(j + 1));
						return suiteName;
					}

				}
			}
		}
		return suiteName;
	}

	public Map<String, String> getSuiteParams(Sheet sheet) throws IOException {
		// parse the worksheet and look for first cell containing text
		// "Suite Parameters". the next cell is supposed to have the actual
		// suite parameters. Returns empty hashmap if not found
		// TODO add custom parser functionality
		Row row;
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row != null){	//skip blank rows
				for (int j = 0; j <= row.getLastCellNum(); j++) {
					String cellValue = formatter
							.formatCellValue(row.getCell(j));
					if (SUITE_PARAMS_STR.equals(cellValue)) {
						Properties p = new Properties();
						p.load(new StringReader(formatter.formatCellValue(row
								.getCell(j + 1))));
						for (Enumeration<?> e = p.keys(); e.hasMoreElements();) {
							String key = (String) e.nextElement();
							params.put(key, p.getProperty(key));
						}
						return params;
					}
				}
			}
		}
		return params;
	}

	public int getHeaderRow(Sheet sheet) {
		// parse the worksheet and look for first cell in first column
		// containing text TEST_ID_STR. this will be the header row. returns 0
		// if not found
		// TODO add custom parser functionality
		Row row;
		int headerRow = 0;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row != null){	//skip blank rows
				String id = formatter.formatCellValue(row.getCell(0));
				if (TEST_ID_STR.equals(id)) {
					headerRow = i;
				}
			}
		}
		return headerRow;

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