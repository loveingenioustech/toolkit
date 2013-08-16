package view;

public class DataColumn {
	private String type;

	private String header;

	private String content;	
	
	public DataColumn() {
	}
	
	public DataColumn(String type, String header, String content) {
		this.type = type;
		this.header = header;
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
