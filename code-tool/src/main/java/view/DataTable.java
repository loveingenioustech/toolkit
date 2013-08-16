package view;

import java.util.List;

public class DataTable {
	private String id;

	private String value;

	private String var;

	private List<DataColumn> columns;

	public DataTable() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public List<DataColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DataColumn> columns) {
		this.columns = columns;
	}

}
