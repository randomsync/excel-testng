package net.randomsync.testng.excel;

import java.util.Arrays;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;

public class ExcelTestCaseTest {

	@Test
	public void getParametersAsProperties() {
		String params = "query=testng\nquery2=selenium";
		ExcelTestCase tc = new ExcelTestCase(1, "Search",
				"Perform a search and validate results", params, null);
		Properties p = null;
		p = tc.getParametersAsProperties();
		Assert.assertEquals(p.getProperty("query"), "testng");
		Assert.assertEquals(p.getProperty("query2"), "selenium");

	}

	@Test
	public void getXmlClasses1() {
		ExcelTestCase tc = new ExcelTestCase(1, "Search",
				"Perform a search and validate results", null, null);
		Assert.assertEquals(tc.getXmlClasses().size(), 0);
		tc.setXmlClasses(Arrays.asList(new XmlClass(
				"net.randomsync.testng.excel.DummyTest")));
		Assert.assertEquals(tc.getXmlClasses().size(), 1);
	}

	@Test
	public void getXmlClasses2() {
		ExcelTestCase tc = new ExcelTestCase(1, "Search",
				"Perform a search and validate results", null, "classes=");
		Assert.assertEquals(tc.getXmlClasses().size(), 0);
	}

	@Test
	public void getXmlClasses3() {
		ExcelTestCase tc = new ExcelTestCase(1, "Search",
				"Perform a search and validate results", null,
				"classes=net.randomsync.testng.excel.DummyTest");
		Assert.assertEquals(tc.getXmlClasses().size(), 1);
		XmlSuite suite = new XmlSuite();
		System.out.println(tc.getTestAsXmlTest(suite));
		System.out.println(tc);
	}
}
