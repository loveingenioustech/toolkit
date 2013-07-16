package db;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Table {
	private String name;

	private List<Column> keies;

	private List<Column> columns;

	public Table() {
	}

	public Table(String name, List<Column> columns) {
		this.name = name;
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getKeies() {
		List<Column> result = new LinkedList<Column>();

		for (Column col : getColumns())
			if (col.isPrimaryKey())
				result.add(col);

		return result;
	}

	public String getConstructorArgs() {
		StringBuffer args = new StringBuffer();
		for (Column col : this.getColumns()) {
			args.append("final " + col.getJavaDataType() + " "
					+ col.getName().toLowerCase() + ", ");
		}

		return args.toString().substring(0, args.length() - 2);
	}

	public String getToStringChain() {
		StringBuffer chain = new StringBuffer(
				"new ToStringBuilder(this).appendSuper(super.toString())");
		for (Column col : this.getColumns()) {
			chain.append(".append(\"" + col.getName().toLowerCase()
					+ "\", this." + col.getName().toLowerCase() + ")");
		}

		chain.append(".toString()");
		return chain.toString();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this).appendSuper(super
				.toString());
		builder.append("name", name).append("keies", keies)
				.append("columns", columns);
		return builder.toString();
	}

}
