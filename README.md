
excel-testng: driving TestNG tests through MS Excel
---------------------------------------------------

__Introduction__

*(Please note: Even though I have been using this library for my own functional testing projects, I have just now begun to work on releasing it publically. So I'm still working on documentation and modifying the code for that purpose. Please consider this a __work in progress__ and feel free to hack the code meanwhile. If you have any questions, feel free to contact me)*



If you have used [TestNG](http://testng.org) for your testing projects, you know about the features it provides as a framework for test execution, assertions and test reporting. The input to TestNG is an [XML file](http://testng.org/doc/documentation-main.html#testng-xml) that specifies which tests/classes will be a part of test execution and the parameters to pass on to the test methods. The other way is through your own code by using the TestNG object and creating your own XML classes that are a part of [org.testng.xml](http://testng.org/javadocs/org/testng/xml/package-summary.html) package.

However, for end to end functional tests, it's much easier to specify the test runs in an Excel spreadsheet. This way, the same format can be used to document the test cases as well as to execute them. This project provides a way to do that. The test cases specified in Excel spreadsheet can be easily distributed to other team members for review, even those who may not need to know about how they will be executed. Once the test classes are created and tests are specified in Excel spreadsheet(s), they can be run simply like this:

    ExcelTestNGRunner runner = new ExcelTestNGRunner("input.xls"); //this can be a single file or a directory, in which case all spreadsheets in that directory are parsed
    runner.run();                                    //run the tests
	
The ExcelTestNGRunner parses the spreadsheet(s) (it uses [apache poi](http://poi.apache.org/), so you need to include it in the classpath), creates TestNG XmlSuite(s) using the test specifications and runs them using TestNG (which also needs to be included in the classpath), which takes care of execution and reporting. More information on how the tests need to be formatted in the spreadsheet is below. 

If your Excel test specifications are in a different format, there are 2 levels of customizations you can do with ExcelTestNGRunner on how to parse the input spreadsheet(s):

1. You can use the in-built parser but specify your own parser map, which tells the parser where it can find the test data (currently not fully implemented)
2. You can create your own parser by implementing IExcelFileParser interface. You need to parse the spreadsheet file and return a list of TestNG XmlSuites.

ExcelTestNGRunner also provides helper methods to customize the TestNG object it uses to execute tests. For example, you can specify any custom listeners using addTestNGListener() method. If you need to have more control, you can create your own TestNG object and then pass it to ExcelTestNGRunner. Please see javadocs for more details. 

__Excel Test Specification__

...coming soon

__Putting it Together__

...coming soon

__Limitations__

1. Since Excel file data cannot be as structured as XML data, there are some limitations when replicating the features of testng.xml input format. For example, in TestNG input XML, for each test you can specify the classes for test methods as well as the included and excluded methods for this run. When entered into a cell in a spreadsheet, that data can become cumbersome and void the whole point of keeping your tests in a easily readable format. This is something I'm looking to add in future. 

2. Also, there is no standard or well accepted format for writing test cases in an Excel file. Every company/project has its own way of specifying test cases. So hopefully, being able to specify a custom parser map or a parser will be helpful in cases where the included parser cannot meet the requirements, even though that means having to write the parsing logic from scratch (atleast in the latter case). One thing that can be done to make it simpler is to extend ExcelSuiteParser and override only the methods that are needed. You can also contact me if you have any suggestsions on making this easier. 

3. One thing that TestNG lacks currently (in my knowledge) is to specify dependencies through the input XML file. Even though not recommended, I find it sometimes unavoidable to have some UI tests dependent on others. One case for example, is when creating a user requires a lot of steps. So in this case, I want to validate user creation in 1 test and use that user Id in other dependent tests. I would like to specify these dependencies when running the tests, not in code and currently, it is not possible to do that.         
