package generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.xml.bind.Marshaller;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import util.ConfigUtil;
import exception.ClientCodeNotAssignedException;
import exception.GenerateException;

public abstract class AbstractGenerator {
	public static final Logger log = Logger.getLogger(AbstractGenerator.class);

	protected String clientCode = null;

	protected Configuration config = null;

	public abstract void generate() throws Exception;

	public void start() {
		try {
			init();

			generate();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private void init() throws Exception {
		if (StringUtils.isEmpty(clientCode))
			throw new ClientCodeNotAssignedException(
					"Client Code wasn't assigned in sub class.");

		// load configuration
		config = ConfigUtil.getConfig(clientCode);

		Velocity.init(config.getProperties("velocity.init"));
	}

	protected void write(Template template, Context context, String filePath)
			throws Exception {
		Writer writer = null;
		// -- create a file for the path
		File file = new File(filePath);

		// -- create all the directories
		log.debug("getParent: " + file.getParent());
		if (!file.getParentFile().exists()) {
			boolean succes = (new File(file.getParent())).mkdirs();

			// -- check if the creation of the directories worked
			if (!succes)
				throw new GenerateException(
						"Unable to create the directory to write the file to");
		}

		// -- wrap the file into a writer so we can put data in it
		writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
				file)));

		template.merge(context, writer);

		// -- write our content to the file
		writer.flush();
		writer.close();
	}

	protected void write(Marshaller marshaller, Object output, String filePath)
			throws Exception {
		File file = new File(filePath);
		// -- create all the directories
		log.debug("getParent: " + file.getParent());
		if (!file.getParentFile().exists()) {
			boolean succes = (new File(file.getParent())).mkdirs();

			// -- check if the creation of the directories worked
			if (!succes)
				throw new GenerateException(
						"Unable to create the directory to write the file to");
		}

		FileOutputStream fos = new FileOutputStream(filePath);

		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		// out.write(declaration);
		// out.write(docType);
		marshaller.marshal(output, out);
		out.close();
	}
}
