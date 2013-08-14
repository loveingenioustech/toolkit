package reflect;

public class ServiceMethod {
	private String name;

	private String returnType;

	private String parameter;

	private String rawPara;

	private String passPara;

	public ServiceMethod() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getRawPara() {
		return rawPara;
	}

	public void setRawPara(String rawPara) {
		this.rawPara = rawPara;
	}

	public String getPassPara() {
		return passPara;
	}

	public void setPassPara(String passPara) {
		this.passPara = passPara;
	}

}
