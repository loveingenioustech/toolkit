package util;

public final class Constants {

	private Constants() {
		throw new AssertionError();
	}

	public static final String SUFFIX_PROP = "properties";
	public static final String SUFFIX_XML = "xml";

	
	
	// Database
	public static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:XE";
	public static final String ORACLE_USER = "CA";
	public static final String ORACLE_PWD = "CA";
	public static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

	public static final String T_JDBC = "jdbc";

	// Profile Attributes
	public static final String ATTR_FIRST_NAME = "FIRST_NAME";
	public static final String ATTR_LAST_NAME = "LAST_NAME";
	public static final String ATTR_DESCRIPTION = "DESCRIPTION";
	
	// Algorithm
	public static final String AES = "AES";	

	// Common
	public static final String USERNAME_PARAM = "username";
	public static final String PASSWORD_PARAM = "password";
	public static final String USERBEAN_ATTR = "userbean";
	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");
	public static final String START_SEPARATOR = LINE_SEPARATOR
			+ "=======================" + LINE_SEPARATOR;;
	public static final String END_SEPARATOR = LINE_SEPARATOR
			+ "---------------------------------------------------------"
			+ LINE_SEPARATOR;
	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	public static final String PATH_SEPARATOR = System
			.getProperty("path.separator");
	public static final String EMPTY_STRING = "";
	public static final String SPACE = " ";
	public static final String TAB = "\t";
	public static final String SINGLE_QUOTE = "'";
	public static final String PERIOD = ".";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	
}
