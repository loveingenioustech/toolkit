package util;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import exception.WrongPathException;

public class ConfigUtil {

	public static Configuration getConfig(String clientCode)
			throws ConfigurationException, WrongPathException,
			UnsupportedEncodingException {
		Configuration output = null;

		/* Load the special client configuration. */
		PropertiesConfiguration commonProp = new PropertiesConfiguration(
				"common.properties");
		String configFilePath = ConfigUtil.getRootPath(commonProp)
				+ commonProp.getString("ConfigFilePath");
		
		// if (commonProp.containsKey("ConfigFileName")) {
		// configFileName = commonProp.getString("ConfigFileName");
		// }
		// if (StringUtils.isEmpty(configFileName))
		// configFileName = clientCode;

		String configFileName = commonProp.containsKey("ConfigFileName") ? commonProp
				.getString("ConfigFileName") : clientCode;

		String cofigFileSuffix = commonProp.containsKey("ConfigFileSuffix") ? commonProp
				.getString("ConfigFileSuffix") : Constants.SUFFIX_PROP;

		File configFile = ConfigUtil.getConfigFile(configFilePath,
				configFileName, cofigFileSuffix);

		if (cofigFileSuffix.equalsIgnoreCase(Constants.SUFFIX_XML)) {
			XMLConfiguration xmlConfiguration = new XMLConfiguration(configFile);
			xmlConfiguration.setExpressionEngine(new XPathExpressionEngine());
			xmlConfiguration.append(commonProp);

			output = xmlConfiguration;
		} else {
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
					configFile);
			propertiesConfiguration.append(commonProp);

			output = propertiesConfiguration;
		}
		
		return output;
	}

	/**
	 * Get the root path.
	 * 
	 * @param cofig
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getRootPath(Configuration cofig)
			throws UnsupportedEncodingException {
		String path = ConfigUtil.class.getProtectionDomain().getCodeSource()
				.getLocation().getFile();

		path = URLDecoder.decode(path, "UTF-8");

		path = new File(path).getParent();

		if (cofig.containsKey("RelativePath"))
			path += cofig.getString("RelativePath");

		if (!path.endsWith("/")) {
			path = path + "/";
		}

		path = path.replace(File.separatorChar, '/');

		return path;
	}

	/**
	 * Retrieve configuration file from the specified folder with the client
	 * code and suffix.
	 * 
	 */
	public static File getConfigFile(String configFilePath,
			final String fileName, final String configFileSuffix)
			throws WrongPathException {
		File configFile = new File(configFilePath);

		if (!configFile.isDirectory())
			throw new WrongPathException(
					"Configuration file path must be a folder.");

		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File pathname) {
				String sPattern = "^(?i)" + fileName + "\\." + configFileSuffix
						+ "$";
				Pattern oPattern = Pattern.compile(sPattern);
				String tmpName = pathname.getName();
				return (oPattern.matcher(tmpName).matches());
			}
		};

		File[] files = configFile.listFiles(fileFilter);
		if (files.length <= 0) {
			throw new WrongPathException(configFilePath + "does not exist");
		}

		if (files.length > 1)
			throw new WrongPathException(
					"Multi-Configuration files were found in the specified path.");

		return files[0];
	}

}
