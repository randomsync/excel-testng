package net.randomsync.testng.excel;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.testng.xml.*;

public class ExcelTestCase {
	public int testId;
	public String testName;
	public String testDescription;
	private String testData;

	public ExcelTestCase(int testId, String testName, String testDescription,
			String testData) {
		this.testId = testId;
		this.testName = testName;
		this.testDescription = testDescription;
		this.testData = testData;
	}

	/**
	 * @return the testData
	 */
	public String getTestData() {
		return testData;
	}

	/**
	 * @return the testData as Properties object When the ExcelTestCase is
	 *         initialized, the testData is in String format. To get the
	 *         testData as a Properties object, call this method
	 * @throws IOException
	 */
	public Properties getTestDataAsProperties() throws IOException {
		Properties propTestData = new Properties();
		propTestData.load(new StringReader(testData));
/*		for(String k : propTestData.stringPropertyNames()){
			propTestData.setProperty(k,RuntimeUtils.Eval(propTestData.getProperty(k)));
		}
*/		return propTestData;
	}

	/**
	 * @return the test as a TestNG XMLTest
	 * @throws IOException
	 */

	public XmlTest getTestAsXmlTest(String packageName) throws IOException {
		XmlTest xmltest = new XmlTest();
		xmltest.setName(this.testId + "." + this.testName); // set name like
															// "1.InvalidLogin"
		// add the parameters to this test case
		Properties props = this.getTestDataAsProperties();
		for (Enumeration<?> e = props.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			xmltest.addParameter(key, props.getProperty(key));
		}
		// add the test classes
		List<XmlClass> classes = new ArrayList<XmlClass>();
		classes.add(new XmlClass("com.experian.seleniumtests." + packageName
				+ ".tests." + this.testName));
		xmltest.setClasses(classes);
		return xmltest;
	}

	public XmlTest getTestAsXmlTest(String packageName, XmlSuite suite)
			throws IOException {
		XmlTest xmltest = new XmlTest(suite);
		xmltest.setName(this.testId + "." + this.testName); // set name like
															// "1.InvalidLogin"
		xmltest.setPreserveOrder("true");
		// add the parameters to this test case
		Properties props = this.getTestDataAsProperties();
		for (Enumeration<?> e = props.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			xmltest.addParameter(key, props.getProperty(key));
		}
		// add the test classes
		List<XmlClass> classes = new ArrayList<XmlClass>();
		classes.add(new XmlClass("com.experian.seleniumtests." + packageName
				+ ".tests." + this.testName));
		xmltest.setXmlClasses(classes);
		return xmltest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExcelTestCase [testId=" + testId + ", testName=" + testName
				+ ", testDescription=" + testDescription + ", testData="
				+ testData + "]";
	}

}
