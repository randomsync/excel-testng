package net.randomsync.testng.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for ExcelSuiteParser, test Excel file in src/test folder is required
 * 
 * @author <a href = "mailto:gaurav&#64;randomsync.net">Gaurav Gupta</a>
 * 
 */
public class ExcelSuiteParserTest {
	FileInputStream fis;
	Sheet sheet;

	@BeforeClass
	public void setUp() throws Exception {
		Workbook wb = null;
		try {
			fis = new FileInputStream("src\\test\\tests.xlsx");
			wb = WorkbookFactory.create(fis);
		} catch (Exception e) {
			Assert.fail("Input Excel file not found");
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);

	}

	@Test(description = "Test getSuiteName without parser map")
	public void testGetSuiteName1() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		String name = parser.getSuiteName(sheet);
		Assert.assertEquals(name, "1.GoogleSearch.FF");
	}

	@Test(description = "Test getSuiteName with parser map - valid location")
	public void testGetSuiteName2() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.SUITE_NAME_CELL, new int[] { 1, 2 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		String name = parser.getSuiteName(sheet);
		Assert.assertEquals(name, "1.GoogleSearch.FF");
	}

	@Test(description = "Test getSuiteName with parser map - invalid location")
	public void testGetSuiteName3() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.SUITE_NAME_CELL, new int[] { 50, 2 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		String name = parser.getSuiteName(sheet);
		Assert.assertEquals(name, "");
	}

	@Test(description = "Test getSuiteName with parser map - incomplete map")
	public void testGetSuiteName4() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.SUITE_NAME_CELL, new int[] { 50 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		String name = parser.getSuiteName(sheet);
		Assert.assertEquals(name, "");
	}

	@Test(description = "Test getSuiteName without parser map - custom SUITE_NAME_STR")
	public void testGetSuiteName5() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		ExcelSuiteParser.SUITE_NAME_STR = "Suite Description";
		String name = parser.getSuiteName(sheet);
		Assert.assertEquals(name, "Google Search Tests on FF browser");
	}

	@Test(description = "Test getSuiteName without parser map - non-existent SUITE_NAME_STR")
	public void testGetSuiteName6() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		ExcelSuiteParser.SUITE_NAME_STR = "Nonexistent suite name";
		String name = parser.getSuiteName(sheet);
		Assert.assertEquals(name, "");
	}

	@Test(description = "Test getSuiteParams without parser map")
	public void testGetSuiteParams1() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		Map<String, String> params = new HashMap<String, String>(); 
		params = parser.getSuiteParams(sheet);
		Assert.assertEquals(params.size(), 3);
		Assert.assertEquals(params.get("browser"), "firefox");
	}

	@Test(description = "Test getSuiteParams with parser map - valid location")
	public void testGetSuiteParams2() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.SUITE_PARAMS_CELL, new int[] { 3, 2 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		Map<String, String> params = new HashMap<String, String>(); 
		params = parser.getSuiteParams(sheet);
		Assert.assertEquals(params.size(), 3);
		Assert.assertEquals(params.get("browser"), "firefox");
	}

	@Test(description = "Test getSuiteParams with parser map - invalid location")
	public void testGetSuiteParams3() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.SUITE_PARAMS_CELL, new int[] { 50, 2 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		Map<String, String> params = new HashMap<String, String>(); 
		params = parser.getSuiteParams(sheet);
		Assert.assertEquals(params.size(), 0);
	}

	@Test(description = "Test getSuiteParams with parser map - incomplete map")
	public void testGetSuiteParams4() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.SUITE_PARAMS_CELL, new int[] { 50 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		Map<String, String> params = new HashMap<String, String>(); 
		params = parser.getSuiteParams(sheet);
		Assert.assertEquals(params.size(), 0);
	}

	@Test(description = "Test getSuiteParams without parser map - custom SUITE_PARAMS_STR")
	public void testGetSuiteParams5() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		ExcelSuiteParser.SUITE_PARAMS_STR = "Suite Description";
		Map<String, String> params = new HashMap<String, String>(); 
		params = parser.getSuiteParams(sheet);
		Assert.assertEquals(params.size(), 1);
		Assert.assertEquals(params.get("Google"), "Search Tests on FF browser");
	}

	@Test(description = "Test getSuiteParams without parser map - non-existent SUITE_PARAMS_STR")
	public void testGetSuiteParams6() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		ExcelSuiteParser.SUITE_PARAMS_STR = "Non existent parameters";
		Map<String, String> params = new HashMap<String, String>(); 
		params = parser.getSuiteParams(sheet);
		Assert.assertEquals(params.size(), 0);
	}

	@Test(description = "Test getHeaderRow without parser map")
	public void testGetHeaderRow1() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		int headerRow = parser.getHeaderRow(sheet);
		Assert.assertEquals(headerRow, 7);
	}

	@Test(description = "Test getHeaderRow without parser map - custom TEST_ID_STR")
	public void testGetHeaderRow2() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		ExcelSuiteParser.TEST_ID_STR = "1.1";
		int headerRow = parser.getHeaderRow(sheet);
		Assert.assertEquals(headerRow, 8);
	}

	@Test(description = "Test getHeaderRow without parser map - non existent TEST_ID_STR")
	public void testGetHeaderRow3() {
		ExcelSuiteParser parser = new ExcelSuiteParser();
		ExcelSuiteParser.TEST_ID_STR = "nonexistent";
		int headerRow = parser.getHeaderRow(sheet);
		Assert.assertEquals(headerRow, 0);
	}

	@Test(description = "Test getHeaderRow with valid parser map")
	public void testGetHeaderRow4() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.HEADER_ROW, new int[] { 50 });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		int headerRow = parser.getHeaderRow(sheet);
		Assert.assertEquals(headerRow, 50);
	}

	@Test(description = "Test getHeaderRow with incomplete parser map")
	public void testGetHeaderRow5() {
		Map<ParserMapConstants, int[]> map = new HashMap<ParserMapConstants, int[]>();
		map.put(ParserMapConstants.HEADER_ROW, new int[] { });

		ExcelSuiteParser parser = new ExcelSuiteParser(map);
		int headerRow = parser.getHeaderRow(sheet);
		Assert.assertEquals(headerRow, 0);
	}

	@AfterClass
	public void tearDown() {
		try {
			fis.close();
		} catch (IOException e) {
			System.out
					.println("IOException when trying to close file input stream");
		}
	}

}
