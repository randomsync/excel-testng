package net.randomsync.testng.excel;

import java.io.File;

import org.testng.TestNG;

public class ExcelTestNGRunner {
	private String source;
	private TestNG testng;

	public ExcelTestNGRunner() {
	}

	public ExcelTestNGRunner(String source) {
		this.source = source;
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
		// TODO Auto-generated method stub

	}

}
