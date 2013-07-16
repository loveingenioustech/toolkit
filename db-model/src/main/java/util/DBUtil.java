package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import db.Column;
import db.Schema;
import db.Table;

public class DBUtil {

	/**
	 * get database connection
	 * 
	 * @return Connection
	 */
	public static Connection getDBConnection(Schema schema) throws SQLException {
		try {
			Class.forName(schema.getDriver());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection connection = DriverManager.getConnection(schema.getUrl(),
				schema.getUser(), schema.getPassword());

		return connection;
	}

	public static Table reverseTable(String tableName) {
		Properties props = new Properties();
		String driver = null;
		String url = null;
		String user = null;
		String password = null;

		try {
			String proFilePath = System.getProperty("user.dir")
					+ "/src/database.properties";
			props.load(new BufferedInputStream(new FileInputStream(proFilePath)));

			driver = props.getProperty("driver");
			url = props.getProperty("url");
			user = props.getProperty("user");
			password = props.getProperty("password");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Schema defaultSchema = new Schema(user, password, url, driver);
		return reverseTable(tableName, defaultSchema);
	}

	public static Table reverseTable(String tableName, Schema schema) {
		Table table = null;
		List<Column> colList = null;
		ResultSet rs = null;

		try {
			Connection conn = DBUtil.getDBConnection(schema);

			DatabaseMetaData md = conn.getMetaData();
			rs = md.getColumns(null, md.getUserName(), tableName.toUpperCase(),
					"%");

			colList = new ArrayList<Column>();
			Column col = null;
			while (rs.next()) {
				col = new Column();
				col.setName(rs.getString("COLUMN_NAME"));
				col.setSqlDataType(rs.getString("TYPE_NAME"));
				col.setNullAble(rs.getInt("NULLABLE") == 1);
				col.setSize(rs.getInt("COLUMN_SIZE"));
				col.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
				col.setNullAble(rs.getInt("NULLABLE") == 1);
				colList.add(col);
			}

			rs = md.getPrimaryKeys(null, md.getUserName(), tableName);

			while (rs.next()) {
				for (Column c : colList) {
					if (c.getName().equals(rs.getString("COLUMN_NAME"))) {
						c.setKeySequence(rs.getShort("KEY_SEQ"));
						continue;
					}
				}
			}

			table = new Table();
			table.setName(tableName);

			if (colList != null && colList.size() > 0) {
				table.setColumns(colList);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return table;
	}

}