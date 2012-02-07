package net.randomsync.testng.excel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.testng.ITestNGListener;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

public class ExcelTestNGRunner {
	private String source;
	private TestNG testng;

	public ExcelTestNGRunner() {
	}

	public ExcelTestNGRunner(String source) {
		this.source = source;
		this.testng = new TestNG();
	}

	/**
	 * @param xlFile
	 *            - the xlFile to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	public void setTestng(TestNG testng) {
		this.testng = testng;
	}

	public void run() {
		File srcFile = new File(source);
		File[] filesList = null; // if source is a directory, this will hold the
									// list of excel files
		// make sure the file source exists
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("The source for the Excel "
					+ "file(s) cannot be found.");
		}
		// if source is a folder, get all excel files within it
		if (srcFile.isDirectory()) {
			filesList = srcFile.listFiles(new ExcelFileNameFilter());
		} else {
			filesList = new File[] { srcFile };
		}
		if (this.testng == null) {
			this.testng = new TestNG();
		}
		// parse each file into an XmlSuite
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		for (File file : filesList) {
			ExcelSuiteParser parser = new ExcelSuiteParser(file);
			XmlSuite suite = null;
			try {
				suite = parser.getXmlSuite();
			} catch (Exception e) {
				// any issues with parsing, skip this suite and continue
				e.printStackTrace();
				continue;
			}
			suites.add(suite);
		}
		testng.setXmlSuites(suites);
		testng.run();
	}

	// helper methods to specify testng configuration
	public void setSuiteThreadPoolSize(int suiteThreadPoolSize) {
		if (testng != null) {
			testng.setSuiteThreadPoolSize(suiteThreadPoolSize);
		}
	}

	public void addListener(ITestNGListener listener) {
		if (testng != null) {
			testng.addListener(listener);
		}
	}

	public void setVerbose(int verbose) {
		if (testng != null) {
			testng.setVerbose(verbose);
		}
	}

	public void setPreserveOrder(boolean order) {
		if (testng != null) {
			testng.setPreserveOrder(order);
		}
	}

	public void setThreadCount(int threadCount) {
		if (testng != null) {
			testng.setThreadCount(threadCount);
		}
	}

	class ExcelFileNameFilter implements FilenameFilter {

		@Override
		public boolean accept(File file, String name) {
			if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
				return true;
			}
			return false;
		}

	}

}
