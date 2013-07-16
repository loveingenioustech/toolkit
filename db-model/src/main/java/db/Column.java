package db;

import org.apache.commons.lang3.builder.ToStringBuilder;

import conversion.Convertor;

public class Column {
	private String name;

	private String type;

	private int size;

	private int decimalDigits;

	private boolean nullAble;

	private String defaultValue;

	private String remarks;

	private int numbersPrecedingRadix;

	private int ordinalPosition;

	private short keySequence;

	private boolean readable;

	private boolean writable;

	public Column() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSqlDataType() {
		return type;
	}

	public void setSqlDataType(String type) {
		this.type = type;
	}

	public String getJavaDataType() {
		return Convertor.translateOracleTypeToJava(this.type);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public boolean isNullAble() {
		return nullAble;
	}

	public void setNullAble(boolean nullAble) {
		this.nullAble = nullAble;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getNumbersPrecedingRadix() {
		return numbersPrecedingRadix;
	}

	public void setNumbersPrecedingRadix(int numbersPrecedingRadix) {
		this.numbersPrecedingRadix = numbersPrecedingRadix;
	}

	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(int ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public short getKeySequence() {
		return keySequence;
	}

	public void setKeySequence(short keySequence) {
		this.keySequence = keySequence;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public boolean isPrimaryKey() {
		return keySequence != 0;
	}

	public String getGetMethod() {
		String getMethod = this
				.getName()
				.toLowerCase()
				.replaceFirst(this.name.substring(0, 1),
						this.name.substring(0, 1).toUpperCase());

		return "get" + getMethod;
	}

	public String getSetMethod() {
		String output = this.name.substring(0, 1).toUpperCase() + this.name.substring(1).toLowerCase();
		
//		String setMethod = this
//				.getName()
//				.toLowerCase()
//				.replaceFirst(this.name.substring(0, 1),
//						this.name.substring(0, 1).toUpperCase());
//		return "set" + setMethod;
		return "set" + output;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this).appendSuper(super
				.toString());
		builder.append("name", name);
		builder.append("type", type);
		builder.append("size", size);
		builder.append("decimalDigits", decimalDigits);
		builder.append("nullAble", nullAble);
		builder.append("defaultValue", defaultValue);
		builder.append("remarks", remarks);
		builder.append("numbersPrecedingRadix", numbersPrecedingRadix);
		builder.append("ordinalPosition", ordinalPosition);
		builder.append("keySequence", keySequence);
		builder.append("readable", readable);
		builder.append("writable", writable);
		return builder.toString();
	}

}
