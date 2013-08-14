package generator.impl;

import generator.AbstractGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import util.DBUtil;
import db.Schema;
import db.Table;
import db.Column;

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

		loadKnowledge();

		createSnippet();
	}

	private void createSnippet() {
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
