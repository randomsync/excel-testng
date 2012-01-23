package net.randomsync.testng.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.randomsync.utils.RuntimeUtils;

import org.testng.xml.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelReader {

	static int headerRow = 0; // the row number which has headers, test data
								// is assumed to start 1 row below the headers
	static String testId = "Test Id";
	static String testName = "Test Name";
	static String testDesc = "Test Description";
	static String testData = "Test Data";

	public static XmlSuite getXmlSuite(String xlFilePath, String sheetName) {
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setName(sheetName);
		// need to send XmlSuite as parameter so we can use XmlTest(suite)
		// instead
		// of test.setSuite(suite) as the latter is throwing an exception
		List<XmlTest> testCases = getXmlTests(xlFilePath, sheetName, xmlSuite);
		xmlSuite.setTests(testCases);
		return xmlSuite;
	}

	public static XmlSuite getXmlSuite(String xlFilePath, String sheetName,
			String browserName) {
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setName(sheetName + "." + browserName);
		// need to send XmlSuite as parameter so we can use XmlTest(suite)
		// instead
		// of test.setSuite(suite) as the latter is throwing an exception
		List<XmlTest> testCases = getXmlTests(xlFilePath, sheetName, xmlSuite);
		xmlSuite.setTests(testCases);
		return xmlSuite;
	}

	public static List<XmlTest> getXmlTests(String xlFilePath,
			String sheetName, XmlSuite suite) {

		// default values
		int testIdCol = 0, testNameCol = 1, testDescCol = 2, testDataCol = 4;
		List<XmlTest> testCases = new ArrayList<XmlTest>();

		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			// 1st get the header columns
			Cell[] header = sheet.getRow(headerRow);
			for (int i = 0; i < header.length; i++) {
				if (header[i].getContents().equals(testId)) {
					testIdCol = header[i].getColumn();
				} else if (header[i].getContents().equals(testName)) {
					testNameCol = header[i].getColumn();
				} else if (header[i].getContents().equals(testDesc)) {
					testDescCol = header[i].getColumn();
				} else if (header[i].getContents().equals(testData)) {
					testDataCol = header[i].getColumn();
				}
			}
			// test data is assumed to start 1 row below header row
			for (int i = headerRow + 1; i < sheet.getRows(); i++) {
				Cell[] curRow = sheet.getRow(i);
				try {
					if (curRow[testIdCol].getContents().length() > 0) {
						ExcelTestCase testCase = new ExcelTestCase(
								Integer.parseInt(curRow[testIdCol]
										.getContents()),
								curRow[testNameCol].getContents(),
								curRow[testDescCol].getContents(),
								curRow[testDataCol].getContents());
						testCases.add(testCase.getTestAsXmlTest(sheetName,
								suite));
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// this exception can be thrown if there's a blank of
					// incomplete row
					// do nothing in this case and move on to next row
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return testCases;

	}

	public static ArrayList<ExcelTestCase> getExcelTestCases(String xlFilePath,
			String sheetName) {

		// default values
		int testIdCol = 0, testNameCol = 1, testDescCol = 2, testDataCol = 4;
		ArrayList<ExcelTestCase> testCases = new ArrayList<ExcelTestCase>();

		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			// 1st get the header columns
			Cell[] header = sheet.getRow(headerRow);
			for (int i = 0; i < header.length; i++) {
				if (header[i].getContents().equals(testId)) {
					testIdCol = header[i].getColumn();
				} else if (header[i].getContents().equals(testName)) {
					testNameCol = header[i].getColumn();
				} else if (header[i].getContents().equals(testDesc)) {
					testDescCol = header[i].getColumn();
				} else if (header[i].getContents().equals(testData)) {
					testDataCol = header[i].getColumn();
				}
			}

			// test data is assumed to start 1 row below header row
			for (int i = headerRow + 1; i < sheet.getRows(); i++) {
				Cell[] curRow = sheet.getRow(i);
				// TODO: can potentially throw ArrayIndexOutOfBoundsExceptions,
				// need to handle it
				if (curRow[testIdCol].getContents().length() > 0) {
					ExcelTestCase testCase = new ExcelTestCase(
							Integer.parseInt(curRow[testIdCol].getContents()),
							curRow[testNameCol].getContents(),
							curRow[testDescCol].getContents(),
							curRow[testDataCol].getContents());
					testCases.add(testCase);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (testCases);

	}

	public static HashMap<String, String> getProperties(String xlFilePath,
			String sheetName) {
		// default values
		int nameCol = 0, valueCol = 1;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			// properties data is assumed to start at row 1 (header row is 0)
			for (int i = 1; i < sheet.getRows(); i++) {
				Cell[] curRow = sheet.getRow(i);
				try {
					if (curRow[nameCol].getContents().length() > 0) {
						map.put(curRow[nameCol].getContents(), RuntimeUtils
								.Eval(curRow[valueCol].getContents()));
					}

				} catch (ArrayIndexOutOfBoundsException e) {
					// this exception can be thrown if there's a blank of
					// incomplete row
					// do nothing in this case and move on to next row
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}