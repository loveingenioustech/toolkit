package util;

public class CommonUtil {

	public static Object toSpaceSplit(String input) {

		String regex = "(.)([A-Z]+)";
		String replacement = "$1 $2";

		return input.replaceAll(regex, replacement);
	}

}
