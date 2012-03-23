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

/**
 * The default parser used by ExcelTestNGRunner to parse an Excel file into
 * TestNG {@link #org XmlSuite}s. The default funtionality is to parse each
 * worksheet into a separate XMLSuite
 * 
 * <p>
 * This parser can be customized by using a parserMap, which specifies where to
 * find the Suite and Test data
 * 
 * @author <a href = "mailto:gaurav&#64;randomsync.net">Gaurav Gupta</a>
 * 
 */
public class ExcelSuiteParser implements IExcelFileParser {

	// class variables that can be modified for custom functionality
	public static String SUITE_NAME_STR = "Suite Name";
	public static String SUITE_PARAMS_STR = "Suite Parameters";
	public static String TEST_ID_STR = "Id";
	public static String TEST_NAME_STR = "Test Name";
	public static String TEST_DESC_STR = "Test Description";
	public static String TEST_PARAMS_STR = "Test Parameters";
	public static String TEST_CONFIG_STR = "Test Configuration";

	// formatter to format cell data
	private DataFormatter formatter = new DataFormatter();

	// this map can be used to customize the location of Suite/Test data
	private Map<ParserMapConstants, int[]> parserMap;

	public ExcelSuiteParser() {
	}

	public ExcelSuiteParser(Map<ParserMapConstants, int[]> parserMap) {
		this.parserMap = parserMap;
	}

	/**
	 * @param parserMap
	 *            - the Map that will be used to parse Excel file(s)
	 */
	public void setParserMap(Map<ParserMapConstants, int[]> parserMap) {
		this.parserMap = parserMap;
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
		fis.close();
		return suites;
	}

	/**
	 * this is the main parser method that parses the Excel file and returns the
	 * tests within the file. Each row is considered as a test case with
	 * specific columns (specified by {@link #parserMap}) as test
	 * 
	 * @return a list of test cases in the Excel file
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public List<ExcelTestCase> parseExcelTestCases(Sheet sheet)
			throws InvalidFormatException, IOException {

		List<ExcelTestCase> testCases = new ArrayList<ExcelTestCase>();

		// validate values and assign default if needed
		// int headerRow = getMapValue("headerRow", 0);
		int headerRow = getHeaderRow(sheet);
		// int testIdCol = getMapValue("testIdCol", 0);
		int testIdCol = 0;

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

	public ExcelTestCase getExcelTestCaseFromRow(Row row,
			DataFormatter formatter) {
		int testIdCol = getMapValue("testIdCol", 0);
		int testNameCol = getMapValue("testNameCol", 2);
		int testDescCol = getMapValue("testDescCol", 3);
		int testParamCol = getMapValue("testParamCol", 4);
		int testConfigCol = getMapValue("testConfigCol", 5);

		return new ExcelTestCase(formatter.formatCellValue(row
				.getCell(testIdCol)), // test id
				formatter.formatCellValue(row.getCell(testNameCol)), // test
																		// name
				formatter.formatCellValue(row.getCell(testDescCol)), // description
				formatter.formatCellValue(row.getCell(testParamCol)), // parameters
				formatter.formatCellValue(row.getCell(testConfigCol)) // configuration
		);

	}

	/**
	 * Returns the name of the Suite from the worksheet. If unable to find the
	 * suite name (for example, if the location is invalid), an empty string is
	 * returned
	 * 
	 * <p>
	 * If a custom parser map is defined, the value is returned from the cell
	 * pointed at by the appropriate key. If a custom map is not defined or if
	 * it doesn't contain the appropriate key, it returns the value from the
	 * cell adjacent to a cell containing {@link #SUITE_NAME_STR}.
	 * 
	 * @param sheet
	 *            The worksheet to be parsed
	 * @return Name of the Suite or blank string if not found
	 */
	public String getSuiteName(Sheet sheet) {
		Row row;
		String suiteName = "";
		// if parser map is specified, get the suite name from specified cell
		if (parserMap != null
				&& parserMap.containsKey(ParserMapConstants.SUITE_NAME_CELL)) {
			int[] loc = parserMap.get(ParserMapConstants.SUITE_NAME_CELL);
			int rownum, colnum;
			try {
				rownum = loc[0];
				colnum = loc[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				// if location is not specified correctly, return blank string
				return "";
			}
			// get the suite name from specified cell
			row = sheet.getRow(rownum);
			if (row != null) {
				suiteName = formatter.formatCellValue(row.getCell(colnum));
			}
			// if suite name is null, return ""
			return suiteName != null ? suiteName : "";
		}
		// else get suite name from cell next to the one containing
		// SUITE_NAME_STR
		else {
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				if (row != null) { // skip blank rows
					for (int j = 0; j <= row.getLastCellNum(); j++) {
						String cellValue = formatter.formatCellValue(row
								.getCell(j));
						if (SUITE_NAME_STR.equals(cellValue)) {
							suiteName = formatter.formatCellValue(row
									.getCell(j + 1));
							return suiteName;
						}
					}
				}
			}// end for
		}
		return suiteName;
	}

	/**
	 * Returns the Suite parameters from the worksheet as a Map. The suite
	 * parameters are required to be in a single cell in java {@link Properties}
	 * format. If the parameters are not found or are invalid, an empty map is
	 * returned.
	 * 
	 * <p>
	 * If a custom parser map is defined, the parameters map is returned from
	 * the cell pointed at by the appropriate key. If a custom map is not
	 * defined or if it doesn't contain the appropriate key, it returns the map
	 * from the cell adjacent to a cell containing {@link #SUITE_PARAMS_STR}.
	 * 
	 * @param sheet
	 *            The worksheet to be parsed
	 * @return A map containing the suite parameters
	 */
	public Map<String, String> getSuiteParams(Sheet sheet) {
		Row row;
		String rawParams = null;
		Map<String, String> params = new HashMap<String, String>();
		// if parser map is specified, get the parameters from specified cell
		if (parserMap != null
				&& parserMap.containsKey(ParserMapConstants.SUITE_PARAMS_CELL)) {
			int[] loc = parserMap.get(ParserMapConstants.SUITE_PARAMS_CELL);
			int rownum, colnum;
			try {
				rownum = loc[0];
				colnum = loc[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				// if location is not specified correctly, return empty map
				return params;
			}
			// else get the parameters from specified cell
			row = sheet.getRow(rownum);
			if (row != null) {
				rawParams = formatter.formatCellValue(row.getCell(colnum));
			}
		}
		// else get the parameters from cell next to the one containing
		// SUITE_PARAMS_STR
		else {
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				if (row != null) { // skip blank rows
					for (int j = 0; j <= row.getLastCellNum(); j++) {
						String cellValue = formatter.formatCellValue(row
								.getCell(j));
						if (SUITE_PARAMS_STR.equals(cellValue)) {
							rawParams = formatter.formatCellValue(row
									.getCell(j + 1));
							break;
						}
					}
				}
				if (rawParams != null)
					break;
			}
		}
		if (rawParams != null) {
			Properties p = new Properties();
			try {
				p.load(new StringReader(rawParams));
			} catch (IOException ioe) {
				// if there are any issues with reading params, return empty map
				return params;
			}
			for (Enumeration<?> e = p.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				params.put(key, p.getProperty(key));
			}
		}
		return params;
	}

	/**
	 * Returns the header row location from the worksheet. Header row is the row
	 * that contains headers for test specifications and below which, all rows
	 * are required to be test case specifications.
	 * 
	 * <p>
	 * If a custom parser map is defined, the header row location is returned as
	 * specified in the map. If a custom map is not defined or if it doesn't
	 * contain the appropriate key, it searches for the header row by looking
	 * for {@link #TEST_ID_STR} in the 1st column.
	 * 
	 * @param sheet
	 *            The worksheet to be parsed
	 * @return the location of header row, or 0 if not found
	 */
	public int getHeaderRow(Sheet sheet) {
		Row row;
		int headerRow = 0;
		// if parser map is specified, try to get header row location from there
		if (parserMap != null
				&& parserMap.containsKey(ParserMapConstants.HEADER_ROW)) {
			int[] loc = parserMap.get(ParserMapConstants.HEADER_ROW);
			try {
				headerRow = loc[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				// if location is not specified correctly, return 0
				return 0;
			}
		}
		// else look for the row containing TEST_ID_STR in 1st column
		else {
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				if (row != null) { // skip blank rows
					String id = formatter.formatCellValue(row.getCell(0));
					if (TEST_ID_STR.equals(id)) {
						return i;
					}
				}
			}
		}
		return headerRow;
	}

	private int getMapValue(String key, int defaultVal) {
		int[] value;
		value = this.parserMap.get(key);
		return value != null ? value[0] : defaultVal;
	}

}