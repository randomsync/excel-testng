package net.randomsync.testng.excel;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;;

public class ExcelTestCase {
	public int id;
	public String name;
	public String description;
	private String parameters;
	private String configuration;
	private List<XmlClass> xmlClasses;

	// private Map<XmlClass, List<XmlInclude>> methods;

	public ExcelTestCase(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.description = desc;
	}

	public ExcelTestCase(int id, String name, String desc, String params,
			String config) {
		this(id, name, desc);
		this.parameters = params;
		this.configuration = config;
	}

	/**
	 * @return the test parameters as Properties object. When ExcelTestCase is
	 *         initialized, the parameters can be in String format. To get the
	 *         testData as a Properties object, use this method
	 */
	public Properties getParametersAsProperties() {
		if (this.parameters == null)
			return new Properties();
		Properties params = new Properties();
		try {
			params.load(new StringReader(parameters));
		} catch (IOException e) {
			// do nothing in this case, return empty Properties object
		}
		return params;
	}

	public Properties getConfigurationAsProperties() {
		if (this.configuration == null)
			return new Properties();
		Properties cfg = new Properties();
		try {
			cfg.load(new StringReader(configuration));
		} catch (IOException e) {
			// do nothing in this case, return empty Properties object
		}
		return cfg;
	}

	public void setXmlClasses(List<XmlClass> xmlClasses) {
		this.xmlClasses = xmlClasses;

	}

	/**
	 * Return the test classes that are a part of this Test. If classes haven't
	 * been set yet, this method will set them by parsing the classes property
	 * from test configuration
	 * 
	 * @return the List of XmlClass that are a part of this Test
	 */
	public List<XmlClass> getXmlClasses() {
		if (this.xmlClasses != null) {
			return this.xmlClasses;
		}
		Properties cfg = this.getConfigurationAsProperties();
		if (!cfg.containsKey("classes")
				|| cfg.getProperty("classes").trim().isEmpty()) {
			return new ArrayList<XmlClass>(); // return an empty list
		}
		List<XmlClass> xmlClasses = new ArrayList<XmlClass>();
		String[] classes = cfg.getProperty("classes").split(",");
		for (int i = 0; i < classes.length; i++) {
			XmlClass cls = new XmlClass(classes[i]);
			xmlClasses.add(i, cls);
		}
		this.xmlClasses = xmlClasses;
		return this.xmlClasses;

	}

	/**
	 * Returns this test as a TestNG XmlTest
	 * 
	 * @param suite
	 * @return
	 */
	public XmlTest getTestAsXmlTest(XmlSuite suite) {
		XmlTest xmltest = new XmlTest(suite);
		xmltest.setName(this.id + "." + this.name); // set name like
													// "<id>.<name>"
		// add parameters to this test case
		Properties params = this.getParametersAsProperties();
		for (Enumeration<?> e = params.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			xmltest.addParameter(key, params.getProperty(key));
		}
		// add test classes
		xmltest.setXmlClasses(this.getXmlClasses());
		return xmltest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExcelTestCase [Id: " + id + ", Name: " + name
				+ ", Description: " + description + ", Configuration: "
				+ configuration + ", Parameters: " + parameters + "]";
	}

}
