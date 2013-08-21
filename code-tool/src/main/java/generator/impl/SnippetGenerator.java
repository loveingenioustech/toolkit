package generator.impl;

import generator.AbstractGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import util.DBUtil;
import view.DataColumn;
import view.DataTable;
import db.Column;
import db.Schema;
import db.Table;

public class SnippetGenerator extends AbstractGenerator {

	private List<Table> tables = null;

	private Map knowledgeMap = null;

	private Schema schema = null;

	public static void main(String[] args) {
		SnippetGenerator snippetGenerator = new SnippetGenerator();
		snippetGenerator.configFileName = "snippet";
		snippetGenerator.start();
	}

	@Override
	public void generate() throws Exception {
		setUp();

		// createJavaSnippet();

		// createJspSnippet();

		createJspSnippet("UTF-8");
	}

	private void createJspSnippet(String encoding) throws Exception {
		String[] views = config.getStringArray("file.views");

		// Prepare DataTables
		List<DataTable> dataTables = new ArrayList<DataTable>();
		DataTable dataTable;

		String outputPath = config.getString("resource.basepath");
		String viewFolder = config.getString("view.config.folder");

		for (int i = 0; i < views.length; i++) {

			String viewText = readFile(outputPath + "/" + viewFolder + "/"
					+ views[i] + ".sql", encoding);

			if (StringUtils.isNotEmpty(viewText)) {
				dataTable = new DataTable();
				dataTable.setId("dt_" + views[i]);
				dataTable.setValue("#{result." + views[i] + "}");
				dataTable.setVar(views[i]);

				parseViewText(viewText, dataTable);
				dataTables.add(dataTable);
			}
		}

		// Construct JSP Snippet
		if (dataTables.size() > 0) {
			StringBuffer fragment = null;

			for (DataTable dt : dataTables) {
				fragment = new StringBuffer("<dataTable id=\"" + dt.getId()
						+ "\" value=\"" + dt.getValue() + "\" var=\""
						+ dt.getVar() + "\">");

				for (DataColumn dc : dt.getColumns()) {
					fragment.append("<column>\r\n<header>" + dc.getHeader()
							+ "</header>");
					fragment.append("<content>" + dt.getVar() + "."
							+ dc.getContent() + "</content>");
					fragment.append("</column>");
				}

				fragment.append("</dataTable>");

				System.out.println(dt.getId());
				System.out.println(fragment.toString() + "\r\n");
			}

		}

	}

	private String readFile(String path, String encoding) throws Exception {
		FileInputStream fis = new FileInputStream(new File(path));

		return IOUtils.toString(fis, encoding);
	}

	private void createJspSnippet() throws Exception {
		Connection conn = DBUtil.getDBConnection(schema);

		// Prepare DataTables
		Statement stat = conn.createStatement();
		ResultSet rs = null;

		String[] views = config.getStringArray("jdbc.views");
		List<DataTable> dataTables = new ArrayList<DataTable>();
		DataTable dataTable;

		for (int i = 0; i < views.length; i++) {
			String sql = "select a.text from user_views a where a.view_name = '"
					+ views[i].toUpperCase() + "'";

			rs = stat.executeQuery(sql);

			String viewText = "";
			if (rs.next()) {
				viewText = rs.getString("TEXT");
			}

			if (StringUtils.isNotEmpty(viewText)) {
				dataTable = new DataTable();
				dataTable.setId("dt_" + views[i]);
				dataTable.setValue("#{result." + views[i] + "}");
				dataTable.setVar(views[i]);

				parseViewText(viewText, dataTable);
				dataTables.add(dataTable);
			}
		}

		rs.close();
		stat.close();
		conn.close();

		// Construct JSP Snippet
		if (dataTables.size() > 0) {
			StringBuffer fragment = null;

			for (DataTable dt : dataTables) {
				fragment = new StringBuffer("<dataTable id=\"" + dt.getId()
						+ "\" value=\"" + dt.getValue() + "\" var=\""
						+ dt.getVar() + "\">");

				for (DataColumn dc : dt.getColumns()) {
					fragment.append("<column>\r\n<header>" + dc.getHeader()
							+ "</header>");
					fragment.append("<content>" + dt.getVar() + "."
							+ dc.getContent() + "</content>");
					fragment.append("</column>");
				}

				fragment.append("</dataTable>");

				System.out.println(dt.getId());
				System.out.println(fragment.toString() + "\r\n");
			}

		}

	}

	private void createJavaSnippet() throws Exception {
		loadKnowledge();

		// Do createJavaSnippet
		StringBuffer columnNames = null;
		StringBuffer selectStatment = null;
		StringBuffer fragment = null;

		for (Table table : tables) {
			columnNames = new StringBuffer();
			selectStatment = new StringBuffer("select ");

			fragment = new StringBuffer("if (this.fileName.equals(\""
					+ table.getName().toLowerCase()
					+ ".csv\")) { this.table.put(\"Column Names\", \"");

			for (Column col : table.getColumns()) {
				if (knowledgeMap.containsKey(col.getName())) {
					columnNames.append(knowledgeMap.get(col.getName()) + ",");
				} else {
					columnNames.append("TODO" + ",");
				}

				selectStatment.append(col.getName().toUpperCase() + "||','||");
			}

			fragment.append(StringUtils.removeEnd(columnNames.toString(), ","));
			fragment.append("\");\r\nthis.tables.put(\"Select Statement\", \"");
			fragment.append(StringUtils.removeEnd(selectStatment.toString(),
					"||','||"));
			fragment.append(" from " + table.getName().toLowerCase()
					+ "\";\r\n");

			System.out.println(table.getName());
			System.out.println(fragment.toString());

		}

	}

	private void loadKnowledge() throws Exception {
		Connection conn = DBUtil.getDBConnection(schema);

		Statement stat = conn.createStatement();
		ResultSet rs = null;

		String[] views = config.getStringArray("jdbc.views");
		knowledgeMap = new HashMap();

		for (int i = 0; i < views.length; i++) {
			String sql = "select a.text from user_views a where a.view_name = '"
					+ views[i].toUpperCase() + "'";

			rs = stat.executeQuery(sql);

			String viewText = "";
			if (rs.next()) {
				viewText = rs.getString("TEXT");
			}

			if (StringUtils.isNotEmpty(viewText)) {
				parseViewText(viewText);
			}

		}

		rs.close();
		stat.close();
		conn.close();
	}

	private void parseViewText(String viewText) {
		Pattern p = Pattern.compile("\\s+\t|\r|\n|select|^from");
		Matcher m = p.matcher(viewText);

		viewText = m.replaceAll("");

		String[] columns = viewText.split("\",");
		String[] tmpArr;
		String tmpKey;

		for (int i = 0; i < columns.length; i++) {
			tmpArr = columns[i].split("\"");
			tmpKey = StringUtils.trim(tmpArr[0]).toLowerCase();
			if (!knowledgeMap.containsKey(tmpKey)) {
				knowledgeMap.put(tmpKey, StringUtils.trim(tmpArr[1]));
			}
		}

	}

	private void parseViewText(String viewText, DataTable dataTable) {
		// ^([^\r]+)\r replace the first line
		Pattern p = Pattern
				.compile("^([^\r]+)\r|\\s+\t|\r|\n|as([\n\r\\s]+)select|from([\n\r\\s]+).*");

		Matcher m = p.matcher(viewText);

		viewText = m.replaceAll("");

		String[] columns = viewText.split("\",");
		String[] tmpArr;
		List<DataColumn> tmpDataColumns = new ArrayList<DataColumn>();

		for (int i = 0; i < columns.length; i++) {
			tmpArr = columns[i].split("\"");

			tmpDataColumns.add(new DataColumn(null,
					StringUtils.trim(tmpArr[1]), StringUtils.trim(tmpArr[0])
							.toLowerCase()));
		}

		dataTable.setColumns(tmpDataColumns);

	}

	private void setUp() throws Exception {
		String username = config.getString("jdbc.username");
		String password = config.getString("jdbc.password");
		String url = config.getString("jdbc.url");
		String driver = config.getString("jdbc.driver");

		schema = new Schema(username, password, url, driver);

		Connection conn = DBUtil.getDBConnection(schema);

		String[] sourceTables = config.getStringArray("jdbc.tables");

		tables = new ArrayList<Table>();

		if (sourceTables != null && sourceTables.length > 0) {
			Table table;

			for (int i = 0; i < sourceTables.length; i++) {
				table = DBUtil.reverseTable(sourceTables[i], conn);
				tables.add(table);
			}

		}

	}

}
