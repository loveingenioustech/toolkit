package generator.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
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

import conversion.Convertor;
import util.CollectionUtil;
import util.CommonUtil;
import util.DBUtil;
import util.XmlUtil;
import db.Column;
import db.Schema;
import db.Table;
import exception.GenerateException;
import generator.AbstractGenerator;
import hbm.ColumnElement;
import hbm.CompositeId;
import hbm.HibernateMapping;
import hbm.Id;
import hbm.KeyProperty;
import hbm.Property;

public class DemoGenerator extends AbstractGenerator {

	private Table table = null;

	public static void main(String[] args) {
		DemoGenerator demoGenerator = new DemoGenerator();

		demoGenerator.clientCode = "demo";

		// TODO
		// generator name

		demoGenerator.start();
	}

	@Override
	public void generate() throws Exception {

		setUp();

		createDTO();

		createHbm();
//
		updateCfg();

		createRequestWrapper();

		createResponseWrapper();

		createDAO();

		createDAOImpl();

//		updateDbConfig();

		createTest();

		// updateTestConfig();
	}

	private void updateDbConfig() throws Exception {
		String outputPath = config.getString("output.basepath");
		String dbconfigFolder = config.getString("dbconfig.folder");
		String dbconfigFileName = config.getString("dbconfig.fileName");

		String dbconfigFilePath = outputPath + "" + dbconfigFolder + "/"
				+ dbconfigFileName;
		File xmlFile = new File(dbconfigFilePath);
		// Get the JDOM document
		Document doc = XmlUtil.parse(dbconfigFilePath);

		Element rootElement = doc.getRootElement();

		String daoName = config.getString("dao.interfaceName");
		String daoImplName = config.getString("daoimpl.packageName") + "."
				+ config.getString("daoimpl.className");

		Element bean = new Element("bean");
		bean.setAttribute("id", daoName).setAttribute("class", daoImplName);

		Element property = new Element("property").setAttribute("name",
				"sessionFactory").setAttribute("ref", "SessionFactory");

		bean.addContent(property);

		rootElement.addContent(bean);
		// document is processed and edited successfully, save it
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		xmlOutputter.output(doc, new FileWriter(xmlFile));
	}

	private void setUp() throws Exception {
		String username = config.getString("jdbc.username");
		String password = config.getString("jdbc.password");
		String url = config.getString("jdbc.url");
		String driver = config.getString("jdbc.driver");

		Schema schema = new Schema(username, password, url, driver);

		Connection conn = DBUtil.getDBConnection(schema);

		String tableName = config.getString("jdbc.table");

		this.table = DBUtil.reverseTable(tableName.toUpperCase(), conn);

		String[] filterColumns = config.getStringArray("columns.exclude");

		// Filter Columns
		if (filterColumns != null && filterColumns.length > 0) {
			log.info("filterd column: " + filterColumns[0]);
			CollectionUtil.exclude(table.getColumns(), filterColumns);
		}
	}

	private void createDTO() throws Exception {
		log.info("Strat createDTO ...");

		VelocityContext context = new VelocityContext();

		String outputPath = config.getString("java.basepath");
		String dtoPackage = config.getString("dto.package");

		String dtoPackageName = config.getString("dto.packageName");
		String dtoClassName = config.getString("dto.className");

		context.put("package", dtoPackageName);
		context.put("className", dtoClassName);
		
		for(Column col: table.getColumns()){
			if(col.getName().contains("#")){
				col.setName(col.getName().replaceAll("#", "_pound"));
			}
		}
		
		context.put("table", table);

		Template template = null;

		try {
			template = Velocity.getTemplate(config.getString("velocity.dto"));
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
			throw new GenerateException("something invoked in the template",
					mie);
		}

		Writer writer = null;
		// -- create a file for the path
		File file = new File(outputPath + "/" + dtoPackage + "/" + dtoClassName
				+ ".java");

		// -- create all the directories
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

		log.info("End createDTO ...");
	}

	private void createHbm() throws Exception {
		JAXBContext jaxbContext = JAXBContext
				.newInstance(HibernateMapping.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));

		// String declaration =
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		String docType = "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \r\n \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\r\n";

		marshaller.setProperty("jaxb.encoding", "UTF-8");
		// marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty("com.sun.xml.bind.xmlHeaders", docType);

		HibernateMapping hbm = new HibernateMapping();
		List<Object> clazz = new ArrayList<Object>();

		hbm.Class mappingClass = new hbm.Class();

		String packageName = config.getString("dto.packageName");
		String className = config.getString("dto.className");

		mappingClass.setName(packageName + "." + className);
		mappingClass.setTable(table.getName().toLowerCase());

		List<String> ids = Arrays.asList(config.getStringArray("table.id"));

		List<Object> properties = new ArrayList<Object>();
		Property p = null;
		ColumnElement ce = null;

		Id id = null;
		CompositeId compositeId = null;

		List<Object> keyProperties = new ArrayList<Object>();
		KeyProperty keyProperty = null;
		ColumnElement keyColumnElement = null;

		if (ids != null && ids.size() > 1) {
			compositeId = new CompositeId();
		}

		for (Column c : table.getColumns()) {
			// if (ids.contains(c.getName()))
			// continue;
			if (ids != null && ids.size() > 0) {
				if (ids.contains(c.getName())) {
					if (ids.size() == 1) {
						id = new Id();

						id.setName(c.getName());
						id.setType("integer");

						ColumnElement idColumnElement = new ColumnElement();
						idColumnElement.setName(c.getName());

						id.getColumnElement().add(idColumnElement);

						mappingClass.setId(id);
					} else {
						// String pkName = StringUtils.uncapitalize(className)
						// + "Pk";

						String pkName = className + "Pk";
						compositeId.setName(pkName);
						compositeId.setClazz(packageName + "." + pkName);

						keyProperty = new KeyProperty();

						keyProperty.setName(c.getName());
						keyProperty.setType(Convertor
								.translateOracleTypeToHibernate(c
										.getSqlDataType()));

						keyColumnElement = new ColumnElement();
						keyColumnElement.setName(c.getName());
						if (!c.getSqlDataType().equalsIgnoreCase("date")) {
							keyColumnElement.setLength(String.valueOf(c
									.getSize()));
						}

						keyProperty.getColumnElement().add(keyColumnElement);

						keyProperties.add(keyProperty);
					}

					continue;

				}
			}

			p = new Property();
			ce = new ColumnElement();

			p.setName(c.getName());
			p.setType(Convertor.translateOracleTypeToHibernate(c
					.getSqlDataType()));

			ce.setName(c.getName());
			if (!c.getSqlDataType().equalsIgnoreCase("date")) {
				ce.setLength(String.valueOf(c.getSize()));
			}

			if (c.getDecimalDigits() != 0) {
				ce.setPrecision(String.valueOf(c.getDecimalDigits()));
			}

			p.getColumnElementOrFormulaElement().add(ce);
			properties.add(p);
		}

		if (compositeId != null) {
			compositeId.getKeyPropertyOrKeyManyToOne().addAll(keyProperties);
			mappingClass.setCompositeId(compositeId);
		}

		mappingClass.getPropertyOrManyToOneOrOneToOne().addAll(properties);

		clazz.add(mappingClass);
		hbm.setClazzOrSubclassOrJoinedSubclass(clazz);

		String outputPath = config.getString("output.basepath");
		String hbmFolder = config.getString("hbm.folder");
		String filePath = outputPath + "/../resources/" + hbmFolder + "/"
				+ className + ".hbm.xml";

		this.write(marshaller, hbm, filePath);
	}

	private void updateCfg() throws Exception {
		String outputPath = config.getString("resource.basepath");
		String cfgFolder = config.getString("cfg.folder");
		String cfgFileName = config.getString("cfg.fileName");

		String cfgFilePath = outputPath + "" + cfgFolder + "/" + cfgFileName;
		File xmlFile = new File(cfgFilePath);
		// Get the JDOM document
		Document doc = XmlUtil.parse(cfgFilePath);

		Element rootElement = doc.getRootElement();

		Element sessionFactory = rootElement.getChild("session-factory");

		String hbmFolder = config.getString("hbm.folder");
		String className = config.getString("dto.className");
		Element mappingElement = new Element("mapping").setAttribute(
				"resource", hbmFolder + "/" + className + ".hbm.xml");
		// Add the mapping element

		sessionFactory.addContent(mappingElement);
		// document is processed and edited successfully, save it
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		xmlOutputter.output(doc, new FileWriter(xmlFile));

		// For Unit Test
		String testCfgFolder = config.getString("test.cfg.folder");
		String testCfgFileName = config.getString("test.cfg.fileName");

		String testCfgFilePath = outputPath + "" + testCfgFolder + "/"
				+ testCfgFileName;
		File testXmlFile = new File(testCfgFilePath);
		// Get the JDOM document
		Document testDoc = XmlUtil.parse(testCfgFilePath);

		Element testRootElement = testDoc.getRootElement();

		Element testSessionFactory = testRootElement
				.getChild("session-factory");
		Element testMappingElement = new Element("mapping").setAttribute(
				"resource", hbmFolder + "/" + className + ".hbm.xml");
		testSessionFactory.addContent(testMappingElement);
		// document is processed and edited successfully, save it
		xmlOutputter.output(testDoc, new FileWriter(testXmlFile));
	}

	private void createRequestWrapper() throws Exception {
		log.info("Strat createRequestWrapper ...");

		VelocityContext context = new VelocityContext();

		String requestPackage = config.getString("request.package");
		String outputPath = config.getString("output.basepath");

		String dtoClassName = config.getString("dto.className");

		// TODO combine package and package with replace with

		String requestPackageName = config.getString("request.packageName");
		String requestClassName = config.getString("request.className");

		context.put("package", requestPackageName);
		context.put("className", requestClassName);

		context.put("dtoObject", StringUtils.uncapitalize(dtoClassName));
		context.put("dtoClassName", dtoClassName);

		Template template = null;

		try {
			template = Velocity.getTemplate("demo/request.java.vm");
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
			throw new GenerateException("something invoked in the template",
					mie);
		}

		String filePath = outputPath + "/" + requestPackage + "/"
				+ requestClassName + ".java";

		this.write(template, context, filePath);

		log.info("End createRequestWrapper ...");
	}

	private void createResponseWrapper() throws Exception {
		log.info("Strat createResponseWrapper ...");

		VelocityContext context = new VelocityContext();

		String responsePackage = config.getString("response.package");
		String outputPath = config.getString("output.basepath");

		String dtoClassName = config.getString("dto.className");

		String responsePackageName = config.getString("response.packageName");
		String responseClassName = config.getString("response.className");

		context.put("package", responsePackageName);
		context.put("className", responseClassName);

		context.put("dtoObject", StringUtils.uncapitalize(dtoClassName));
		context.put("dtoClassName", dtoClassName);

		Template template = null;

		try {
			template = Velocity.getTemplate("demo/response.java.vm");
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
			throw new GenerateException("something invoked in the template",
					mie);
		}

		String filePath = outputPath + "/" + responsePackage + "/"
				+ responseClassName + ".java";

		this.write(template, context, filePath);

		log.info("End createResponseWrapper ...");
	}

	private void createDAO() throws Exception {
		log.info("Strat createDAO ...");

		VelocityContext context = new VelocityContext();

		String requestPackageName = config.getString("request.packageName");
		String responsePackageName = config.getString("response.packageName");

		String requestClassName = config.getString("request.className");
		String responseClassName = config.getString("response.className");

		String dtoClassName = config.getString("dto.className");

		String daoPackageName = config.getString("dao.packageName");
		String interfaceName = config.getString("dao.interfaceName");

		context.put("package", daoPackageName);
		context.put("interfaceName", interfaceName);

		context.put("requestPackage", requestPackageName);
		context.put("responsePackage", responsePackageName);
		context.put("requestClassName", requestClassName);
		context.put("responseClassName", responseClassName);
		context.put("dtoClassName", dtoClassName);

		Template template = null;

		try {
			template = Velocity.getTemplate("demo/dao.java.vm");
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
			throw new GenerateException("something invoked in the template",
					mie);
		}

		String outputPath = config.getString("output.basepath");
		String daoPackage = config.getString("dao.package");

		String filePath = outputPath + "/" + daoPackage + "/" + interfaceName
				+ ".java";

		this.write(template, context, filePath);

		log.info("End createDAO ...");
	}

	private void createDAOImpl() throws Exception {
		log.info("Strat createDAOImpl ...");

		VelocityContext context = new VelocityContext();

		String requestPackageName = config.getString("request.packageName");
		String responsePackageName = config.getString("response.packageName");

		String requestClassName = config.getString("request.className");
		String responseClassName = config.getString("response.className");

		String dtoPackageName = config.getString("dto.packageName");
		String dtoClassName = config.getString("dto.className");

		String daoPackageName = config.getString("dao.packageName");
		String daoInterfaceName = config.getString("dao.interfaceName");

		String daoimplPackageName = config.getString("daoimpl.packageName");
		String daoimplClassName = config.getString("daoimpl.className");

		context.put("package", daoimplPackageName);
		context.put("className", daoimplClassName);

		context.put("requestPackage", requestPackageName);
		context.put("responsePackage", responsePackageName);
		context.put("requestClassName", requestClassName);
		context.put("responseClassName", responseClassName);
		context.put("dtoPackage", dtoPackageName);
		context.put("dtoClassName", dtoClassName);
		context.put("daoPackage", daoPackageName);
		context.put("daoInterfaceName", daoInterfaceName);
		context.put("dtoList", StringUtils.uncapitalize(dtoClassName) + "List");

		Object dtoClassDesc = CommonUtil.toSpaceSplit(dtoClassName);
		context.put("dtoClassDesc", dtoClassDesc);
		context.put("tableName", table.getName().toLowerCase());

		Template template = null;

		try {
			template = Velocity.getTemplate("demo/daoimpl.java.vm");
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
			throw new GenerateException("something invoked in the template",
					mie);
		}

		String outputPath = config.getString("output.basepath");
		String daoimplPackage = config.getString("daoimpl.packageName")
				.replace(".", "/");

		String filePath = outputPath + "/" + daoimplPackage + "/"
				+ daoimplClassName + ".java";

		this.write(template, context, filePath);

		log.info("End createDAOImpl ...");
	}

	private void createTest() throws Exception {
		log.info("Strat createTest ...");

		VelocityContext context = new VelocityContext();

		String requestPackageName = config.getString("request.packageName");
		String responsePackageName = config.getString("response.packageName");

		String requestClassName = config.getString("request.className");
		String responseClassName = config.getString("response.className");

		String dtoPackageName = config.getString("dto.packageName");
		String dtoClassName = config.getString("dto.className");

		String daoPackageName = config.getString("dao.packageName");
		String daoInterfaceName = config.getString("dao.interfaceName");

		String daoimplPackageName = config.getString("daoimpl.packageName");
		String daoimplClassName = config.getString("daoimpl.className");

		String daotestPackageName = config.getString("daotest.packageName");
		String daotestClassName = config.getString("daotest.className");

		context.put("package", daotestPackageName);
		context.put("className", daotestClassName);

		context.put("requestPackage", requestPackageName);
		context.put("responsePackage", responsePackageName);
		context.put("requestClassName", requestClassName);
		context.put("responseClassName", responseClassName);
		context.put("dtoPackage", dtoPackageName);
		context.put("dtoClassName", dtoClassName);
		context.put("daoPackage", daoPackageName);
		context.put("daoInterfaceName", daoInterfaceName);
		context.put("dtoList", StringUtils.uncapitalize(dtoClassName) + "List");
		context.put("daoimplPackage", daoimplPackageName);
		context.put("daoimplClassName", daoimplClassName);

		context.put("testCfgFileName", config.getString("test.cfg.fileName"));

		Object dtoClassDesc = CommonUtil.toSpaceSplit(dtoClassName);
		context.put("dtoClassDesc", dtoClassDesc);
		context.put("tableName", table.getName());

		Template template = null;

		try {
			template = Velocity.getTemplate("demo/daotest.java.vm");
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
			throw new GenerateException("something invoked in the template",
					mie);
		}

		String outputPath = config.getString("output.basepath");
		String daotestPackage = config.getString("daotest.packageName")
				.replace(".", "/");

		String filePath = outputPath + "/" + daotestPackage + "/"
				+ daotestClassName + ".java";

		this.write(template, context, filePath);

		log.info("End createTest ...");
	}

}
