package db;

public class Test {

	public static void main(String[] args) {		
		Column c = new Column();
		c.setName("ABC_A");		
		
		System.out.println(c.getGetMethod());
		System.out.println(c.getSetMethod());
	}

}
