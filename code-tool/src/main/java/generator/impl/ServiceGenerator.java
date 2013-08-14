package generator.impl;

import exception.GenerateException;
import generator.AbstractGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import reflect.ServiceMethod;
import util.XmlUtil;

public class ServiceGenerator extends AbstractGenerator {

	public static void main(String[] args) {
		ServiceGenerator serviceGenerator = new ServiceGenerator();
		serviceGenerator.configFileName = "service";

		serviceGenerator.start();
	}

	@Override
	public void generate() throws Exception {
		setUp();

		createService();

		createSerivceImpl();

		updateServiceConf();
	}

	private void updateServiceConf() throws Exception {
		log.info("Start updateServiceConf ...");

		String outputPath = config.getString("resource.basepath");
		String serviceConfigFolder = config.getString("service.config.folder");
		String serviceConfigFileName = config
				.getString("service.config.fileName");
		String serviceImplPackage = config.getString("serviceimpl.package")
				.replaceAll("/", ".");

		String serviceConfigFilePath = outputPath + "/" + serviceConfigFolder
				+ "/" + serviceConfigFileName;

		File xmlFile = new File(serviceConfigFilePath);

		// Get the JDOM document
		Document doc = XmlUtil.parse(serviceConfigFilePath);

		Element rootElement = doc.getRootElement();

		String[] daos = config.getStringArray("daos");
		String daoInterfaceName;
		String entityName;

		for (int i = 0; i < daos.length; i++) {
			daoInterfaceName = daos[i];
			entityName = daoInterfaceName.replace("DAO", "");

			log.info("Config service for DAO: " + daoInterfaceName);

			Element bean = new Element("bean");
			bean.setAttribute("id", entityName + "Service").setAttribute(
					"class",
					serviceImplPackage + "." + entityName + "ServiceImpl");

			Element property = new Element("property");
			property.setAttribute("name",
					StringUtils.uncapitalize(daoInterfaceName)).setAttribute(
					"ref", daoInterfaceName);

			bean.addContent(property);

			rootElement.addContent(bean);
		}

		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		xmlOutputter.output(doc, new FileWriter(xmlFile));

		log.info("End updateServiceConf ...");

	}

	private void createSerivceImpl() throws Exception {
		log.info("Start createSerivceImpl ...");
		VelocityContext context = new VelocityContext();

		String outputPath = config.getString("java.basepath");

		String servicePackage = config.getString("service.package");
		String servicePackageName = servicePackage.replaceAll("/", ".");
		String serviceImplPackage = config.getString("serviceimpl.package");
		String serviceImplPackageName = serviceImplPackage.replaceAll("/", ".");

		String daoPackage = config.getString("dao.package");
		String daoPackageName = daoPackage.replaceAll("/", ".");

		String[] daos = config.getStringArray("daos");
		String daoInterfaceName;

		for (int i = 0; i < daos.length; i++) {
			daoInterfaceName = daos[i];
			log.info("Create Serivce Impl for DAO: " + daoInterfaceName);

			Class daoClass = Class.forName(daoPackageName + "."
					+ daoInterfaceName);

			List<ServiceMethod> methodList = new ArrayList<ServiceMethod>();
			ServiceMethod serviceMethod = null;

			for (Method method : daoClass.getMethods()) {
				serviceMethod = new ServiceMethod();
				serviceMethod.setName(method.getName());
				serviceMethod.setReturnType(method.getReturnType()
						.getSimpleName());

				if (method.getParameterTypes() != null) {
					Class[] parameterTypes = method.getParameterTypes();

					if (parameterTypes.length == 1) {
						serviceMethod.setParameter(parameterTypes[0]
								.getSimpleName() + " request");
						serviceMethod.setPassPara("request");
						serviceMethod.setRawPara(parameterTypes[0].getName());
					} else if (parameterTypes.length == 3) {
						serviceMethod
								.setParameter(parameterTypes[0].getSimpleName()
										+ " request, final int pageNumber, final int pageSize");
						serviceMethod
								.setPassPara("request, pageNumber, pageSize");
						serviceMethod.setRawPara(parameterTypes[0].getName()
								+ ", int, int");

					}
				}

				methodList.add(serviceMethod);
			}

			// Set up velocity
			context.put("servicePackage", servicePackageName);
			context.put("serviceImplPackage", serviceImplPackageName);
			context.put("daoPackageName", daoPackageName);
			context.put("daoInterfaceName", daoInterfaceName);
			context.put("methodList", methodList);

			String dtoName = daoInterfaceName.replaceAll("DAO", "");
			String request = config.getString("request.package").replaceAll(
					"/", ".")
					+ "." + dtoName + "Request";
			String response = config.getString("response.package").replaceAll(
					"/", ".")
					+ "." + dtoName + "Response";
			String serviceName = dtoName + "Service";
			String daoObjectName = StringUtils.uncapitalize(daoInterfaceName);

			context.put("request", request);
			context.put("response", response);
			context.put("serviceName", serviceName);
			context.put("daoObjectName", daoObjectName);

			Template template = null;

			try {
				template = Velocity.getTemplate(config
						.getString("velocity.serviceimpl"));
			} catch (ResourceNotFoundException rnfe) {
				// couldn't find the template
				throw new GenerateException("couldn't find the template", rnfe);
			} catch (ParseErrorException pee) {
				// syntax error : problem parsing the template
				throw new GenerateException(
						"syntax error : problem parsing the template", pee);
			} catch (MethodInvocationException mie) {
				// something invoked in the template
				// threw an exception
				throw new GenerateException(
						"something invoked in the template", mie);
			}

			Writer writer = null;
			// -- create a file for the path
			File file = new File(outputPath + "/" + serviceImplPackage + "/"
					+ serviceName + "Impl.java");

			// -- create all the directories
			if (!file.getParentFile().exists()) {
				boolean succes = (new File(file.getParent())).mkdirs();

				// -- check if the creation of the directories worked
				if (!succes)
					throw new GenerateException(
							"Unable to create the directory to write the file to");
			}

			// -- wrap the file into a writer so we can put data in it
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file)));

			template.merge(context, writer);

			// -- write our content to the file
			writer.flush();
			writer.close();

		}

		log.info("End createSerivceImpl ...");
	}

	private void createService() throws Exception {
		log.info("Start createService ...");

		VelocityContext context = new VelocityContext();

		String outputPath = config.getString("java.basepath");

		String servicePackage = config.getString("service.package");
		String servicePackageName = servicePackage.replaceAll("/", ".");
		String servicImplPackage = config.getString("serviceimpl.package");
		String servicImplPackageName = servicImplPackage.replaceAll("/", ".");

		String daoPackage = config.getString("dao.package");
		String daoPackageName = daoPackage.replaceAll("/", ".");

		String[] daos = config.getStringArray("daos");
		String daoInterfaceName;

		for (int i = 0; i < daos.length; i++) {
			daoInterfaceName = daos[i];
			log.info("Create Serivce for DAO: " + daoInterfaceName);

			Class daoClass = Class.forName(daoPackageName + "."
					+ daoInterfaceName);

			List<ServiceMethod> methodList = new ArrayList<ServiceMethod>();
			ServiceMethod serviceMethod = null;

			for (Method method : daoClass.getMethods()) {
				serviceMethod = new ServiceMethod();
				serviceMethod.setName(method.getName());
				serviceMethod.setReturnType(method.getReturnType()
						.getSimpleName());

				if (method.getParameterTypes() != null) {
					Class[] parameterTypes = method.getParameterTypes();

					if (parameterTypes.length == 1) {
						serviceMethod.setParameter(parameterTypes[0]
								.getSimpleName() + " request");
					} else if (parameterTypes.length == 3) {
						serviceMethod
								.setParameter(parameterTypes[0].getSimpleName()
										+ " request, final int pageNumber, final int pageSize");

					}
				}

				methodList.add(serviceMethod);
			}

			// Set up velocity
			context.put("servicePackage", servicePackageName);
			context.put("servicImplPackage", servicImplPackageName);
			context.put("daoInterfaceName", daoInterfaceName);
			context.put("methodList", methodList);

			String dtoName = daoInterfaceName.replaceAll("DAO", "");
			String request = config.getString("request.package").replaceAll(
					"/", ".")
					+ "." + dtoName + "Request";
			String response = config.getString("response.package").replaceAll(
					"/", ".")
					+ "." + dtoName + "Response";
			String serviceName = dtoName + "Service";
			context.put("request", request);
			context.put("response", response);
			context.put("serviceName", serviceName);

			Template template = null;

			try {
				template = Velocity.getTemplate(config
						.getString("velocity.service"));
			} catch (ResourceNotFoundException rnfe) {
				// couldn't find the template
				throw new GenerateException("couldn't find the template", rnfe);
			} catch (ParseErrorException pee) {
				// syntax error : problem parsing the template
				throw new GenerateException(
						"syntax error : problem parsing the template", pee);
			} catch (MethodInvocationException mie) {
				// something invoked in the template
				// threw an exception
				throw new GenerateException(
						"something invoked in the template", mie);
			}

			Writer writer = null;
			// -- create a file for the path
			File file = new File(outputPath + "/" + servicePackage + "/"
					+ serviceName + ".java");

			// -- create all the directories
			if (!file.getParentFile().exists()) {
				boolean succes = (new File(file.getParent())).mkdirs();

				// -- check if the creation of the directories worked
				if (!succes)
					throw new GenerateException(
							"Unable to create the directory to write the file to");
			}

			// -- wrap the file into a writer so we can put data in it
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file)));

			template.merge(context, writer);

			// -- write our content to the file
			writer.flush();
			writer.close();

		}

		log.info("End createService ...");
	}

	private void setUp() {
		// TODO Auto-generated method stub
	}

}
