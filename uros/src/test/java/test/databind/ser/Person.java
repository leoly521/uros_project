package test.databind.ser;

import java.util.ArrayList;
import java.util.List;

public class Person {
	public static enum TYPE {
		TypeA("a", "A"), TypeB("b", "B"), TypeC("c", "C"), TypeD("d", "D");
		
		private String key;
		private String value;
		
		TYPE(String key,String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String getCode() {
			return key;
		}

		public String getDesc() {
			return value;
		}
	}
	
	private TYPE type = TYPE.TypeB;
	private String name = "Leo";
	private String fullname = "李勇";
	private Gender gender = Gender.AA;
	private String description = "中华人民共和国\n\r\n哈哈\"'\"{dsf}";
	private java.sql.Date birthday = new java.sql.Date(new java.util.Date().getTime());
	private int age = 1132;
	private java.util.Date createAt = new java.util.Date();
	private boolean married = false;
	private Integer count = 3;
	private Boolean disabled = true;
	private List<Object> list = new ArrayList<Object>();
	private byte b1 = 'a';
	private Byte b2 = 'b';
	private byte[] bb1 = new byte[]{'a', 'b'};
	private Byte[] bb2 = new Byte[]{'a', 'b'};
	private Object obj = new String("object");

	public Person() {
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender sex) {
		this.gender = sex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.sql.Date getBirthday() {
		return birthday;
	}

	public void setBirthday(java.sql.Date birthday) {
		this.birthday = birthday;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public java.util.Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(java.util.Date createAt) {
		this.createAt = createAt;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	public byte getB1() {
		return b1;
	}

	public void setB1(byte b1) {
		this.b1 = b1;
	}

	public Byte getB2() {
		return b2;
	}

	public void setB2(Byte b2) {
		this.b2 = b2;
	}

	public byte[] getBb1() {
		return bb1;
	}

	public void setBb1(byte[] bb1) {
		this.bb1 = bb1;
	}

	public Byte[] getBb2() {
		return bb2;
	}

	public void setBb2(Byte[] bb2) {
		this.bb2 = bb2;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
}
