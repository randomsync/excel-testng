package net.randomsync.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/**
 * @author nt9a1gg
 * 
 */
public class RuntimeUtils {

	static final String userPfx = "tstusr";
	static final String dateFormat = "yyyyMMddHHmmss";

	/**
	 * Evaluates the param and returns the evaluated value. This method can be
	 * used to input dynamic values in the test data by preceding the value with
	 * "\fn" and then providing a function. If the param doesn't precede with
	 * "\fn", the value is returned as is.
	 * 
	 * Available functions:<br>
	 * - Decrypt("[encrypted string]") <br>
	 * - WritePropertyToFile("[filename]","[key]","[value]") <br>
	 * - ReadPropertyFromFile("[filename]","[key]","<defaultvalue>")<br>
	 * - GetDynamicString("<filename>","<key>","<prefix>","<dateformat>"):
	 * Returns a dynamic string and writes the value to the specified property
	 * file<br>
	 * 
	 * @param param
	 * @return the evaluated value of the param
	 */
	public static String Eval(String methodStr) {
		// param is the parameter to evaluate, it is supposed to be
		// formatted as: "\fnMethodName(method-params).
		try {
			if (methodStr.length() > 3
					&& methodStr.substring(0, 2).equalsIgnoreCase("\fn")) {
				String val = null;
				String methodName = getMethodName(methodStr.substring(2));
				String[] methodParams = getMethodParams(methodStr.substring(2));

				if (methodName.equalsIgnoreCase("Decrypt")) {
					if (methodParams != null && methodParams.length > 0) {
						val = CryptoUtils.decrypt(methodParams[0]);
					}
				} else if (methodName.equalsIgnoreCase("WritePropertyToFile")) {
					if (methodParams != null && methodParams.length >= 3) {
						val = writePropertyToFile(methodParams[0],
								methodParams[1], methodParams[2]);
					}
				} else if (methodName.equalsIgnoreCase("ReadPropertyFromFile")) {
					if (methodParams != null && methodParams.length >= 2) {
						// default value is specified
						if (methodParams.length > 2) {
							val = readPropertyFromFile(methodParams[0],
									methodParams[1], methodParams[2]);
						} else { // no default value, use blank string
							val = readPropertyFromFile(methodParams[0],
									methodParams[1], "");
						}
					}
				} else if (methodName.equalsIgnoreCase("GetDynamicString")) {
					if (methodParams == null || methodParams.length < 2) {
						val = getDynamicString(userPfx, dateFormat);
					} else if (methodParams.length < 4) {
						val = getDynamicString(methodParams[0],
								methodParams[1], methodParams[2], dateFormat);
					} else {
						val = getDynamicString(methodParams[0],
								methodParams[1], methodParams[2],
								methodParams[3]);

					}

				}
				return val;

			}
		} catch (Exception e) {
			// if there's any exception, print stack trace and return param
			// as-is
			e.printStackTrace();
		}
		return methodStr;
	}

	/**
	 * Writes the specified property key and value to the specified properties
	 * file
	 * 
	 * @param filename
	 * @param key
	 * @param value
	 * @return the written value
	 * @throws IOException
	 */
	public static String writePropertyToFile(String filename, String key,
			String value) throws IOException {
		// load the file into Properties, if file doesn't exist, create new
		// add the key/value to the properties
		// write the properties into the file
		FileReader reader = null;
		Properties props = new Properties();
		try {
			reader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			// reader = new FileReader(new File(filename));
			File f = new File(filename);
			f.createNewFile();
			reader = new FileReader(f);
		}
		props.load(reader);
		reader.close();
		// value can itself be a function in which case it's evaluated first
		String newval = Eval(value);
		props.setProperty(key, newval);
		FileWriter writer = new FileWriter(filename);
		props.store(writer, null);
		writer.close();
		return newval;
	}

	/**
	 * Reads the property with specified key from a properties file. If key is
	 * not found, defaultValue is returned
	 * 
	 * @param filename
	 * @param key
	 * @param defaultValue
	 * @return
	 * @throws IOException
	 */
	public static String readPropertyFromFile(String filename, String key,
			String defaultValue) throws IOException {
		// load the file into a new Properties object
		// if the file doesn't exist, throw the exception to the caller
		Properties props = new Properties();
		props.load(new FileReader(filename));
		// evaluate the value and default value if needed before returning it
		return Eval(props.getProperty(key, Eval(defaultValue)));
	}

	public static String getDynamicString(String prefix, String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
		// concat prefix with current time and return
		return prefix + sf.format(cal.getTime());
	}

	public static String getDynamicString(String filename, String key,
			String prefix, String dateFormat) throws IOException {

		// get the user Id
		String userId = getDynamicString(prefix, dateFormat);

		// if filename and key are not blank, write the userId in the file.
		// otherwise return the userId
		// use writePropertyToFile to write the userId to the properties file
		if (filename.length() > 0 && key.length() > 0) {
			return writePropertyToFile(filename, key, userId);
		} else {
			return userId;
		}

	}

	public static String concatBrowser(String browser, String param) {
		int i = param.indexOf("out.properties");

		if (i > 0) {
			return param.substring(0, i) + browser + "." + param.substring(i);
		} else
			return param;

	}

	private static String[] getMethodParams(String methodSig) {
		String[] params = null;
		// first get the complete parameters string within the opening and
		// closing brackets
		int indxB = methodSig.indexOf("(");
		int indxE = methodSig.lastIndexOf(")");
		if (indxB > 0 & indxE > indxB) {
			String paramStr = methodSig.substring(indxB + 1, indxE);
			String otherThanQuote = " [^\"] ";
			String quotedString = String.format(" \" %s* \" ", otherThanQuote);
			String regex = String.format("(?x) " + // enable comments, ignore
													// white spaces
					",                         " + // match a comma
					"(?=                       " + // start positive look ahead
					"  (                       " + // start group 1
					"    %s*                   " + // match 'otherThanQuote'
													// zero or more times
					"    %s                    " + // match 'quotedString'
					"  )*                      " + // end group 1 and repeat it
													// zero or more times
					"  %s*                     " + // match 'otherThanQuote'
					"  $                       " + // match the end of the
													// string
					")                         ", // stop positive look ahead
					otherThanQuote, quotedString, otherThanQuote);

			params = paramStr.split(regex); // params still may have
											// double-quotes around them
			for (int i = 0; i < params.length; i++) {
				String t = params[i].trim();
				if (t.startsWith("\"")) {
					t = t.substring(1, t.length());
				}
				if (t.endsWith("\"")) {
					t = t.substring(0, t.length() - 1);
				}
				params[i] = t;
				// System.out.println("> "+t);
			}
		}

		return params;
	}

	private static String getMethodName(String methodSig) {
		int indx = methodSig.indexOf("(");
		if (indx > 0)
			return methodSig.substring(0, indx);
		else
			return methodSig;
	}

}
