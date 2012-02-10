package net.randomsync.testng.excel;

import java.io.File;

import org.testng.xml.XmlSuite;

public interface IExcelFileParser {
	
	XmlSuite parse(File file, boolean loadClasses);

}
