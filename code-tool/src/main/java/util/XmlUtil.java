package util;

import java.io.File;

import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;

public class XmlUtil {

	public static Document parse(String filePath) throws Exception {
		SAXBuilder saxBuilder = new SAXBuilder();
		return saxBuilder.build(new File(filePath));
	}

}
