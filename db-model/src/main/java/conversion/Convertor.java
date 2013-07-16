package conversion;

import java.util.HashMap;
import java.util.Map;

public class Convertor {
	private static Map<String, Map> groups = new HashMap<String, Map>();

	/** The Map containing the preferred conversion type values. */
	private static final Map PREFERRED_ORACLETYPE_FOR_HIBERNATE = new HashMap();
	private static final Map PREFERRED_ORACLETYPE_FOR_JAVA = new HashMap();

	static {
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("CHAR", "char");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("DATE", "date");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("FLOAT", "double");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("LONG", "string");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("NUMBER", "big_decimal");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("VARCHAR2", "string");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("BLOB", "blob");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("CLOB", "clob");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("NCHAR", "char");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("NCLOB", "clob");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("NVARCHAR2", "string");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("RAW", "binary");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("LONG RAW", "binary");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("TIMESTAMP", "timestamp");
		PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("XMLTYPE", "clob");
		// PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("ROWID", "1111");
		// PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("UROWID", "1111");
		// PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("BFILE", "-13");
		// PREFERRED_ORACLETYPE_FOR_HIBERNATE.put("MLSLABEL", "1111");
		groups.put("PREFERRED_ORACLETYPE_FOR_HIBERNATE",
				PREFERRED_ORACLETYPE_FOR_HIBERNATE);

		PREFERRED_ORACLETYPE_FOR_JAVA.put("CHAR", "Character");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("DATE", "java.util.Date");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("FLOAT", "java.math.BigDecimal");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("LONG", "String");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("NUMBER", "Double");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("VARCHAR2", "String");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("BLOB", "java.sql.Blob");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("CLOB", "java.sql.Clob");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("NCHAR", "char");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("NCLOB", "java.sql.Clob");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("NVARCHAR2", "String");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("RAW", "java.lang.Byte[]");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("LONG RAW", "java.lang.Byte[]");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("TIMESTAMP", "java.sql.Timestamp");
		PREFERRED_ORACLETYPE_FOR_JAVA.put("XMLTYPE", "java.sql.Clob");

		groups.put("PREFERRED_ORACLETYPE_FOR_JAVA",
				PREFERRED_ORACLETYPE_FOR_JAVA);
	}

	// -- Translation
	// -------------------------------------------------------------

	public static String translateOracleTypeToHibernate(String oracleType) {
		return translateTo("PREFERRED_ORACLETYPE_FOR_HIBERNATE", oracleType);
	}

	public static String translateOracleTypeToJava(String oracleType) {
		return translateTo("PREFERRED_ORACLETYPE_FOR_JAVA", oracleType);
	}

	public static String translateTo(String target, String input) {
		Map group = getTargetGroup(target);

		if (group != null) {
			String result = (String) group.get(input);

			if (result == null) {
				return null;
			} else {
				return result;
			}

		} else {
			return input;
		}
	}

	private static Map getTargetGroup(String target) {
		if (groups != null)
			return groups.get(target);

		return null;
	}
}
