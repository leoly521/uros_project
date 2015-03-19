package test.databind.ser;

import java.nio.charset.Charset;
import java.util.Map;

import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.UrosFormatter;

public class DatabindUserCase01 {

	public static void main(String[] args) throws Exception {
		int a = 1200;
		System.out.println(a/500);
//		ser1();
//		ser2();
		deser1();
//		deser2();
	}
	
	private static void ser1() throws Exception {
		Person person1 = new Person();
		byte[] bytes = UrosFormatter.serializeToByteArray(person1);
		
		String str1 = new String(bytes, Charset.forName("UTF-8"));
		System.out.println("Person:===============");
		System.out.println(str1);
		System.out.println("Person:===============");
	}
	
	private static void ser2() throws Exception {
		String string1 = new String("aaaa");
		byte[] bytes = UrosFormatter.serializeToByteArray(string1);
		
		String str1 = new String(bytes, Charset.forName("UTF-8"));
		System.out.println("String:===============");
		System.out.println(str1);
		System.out.println("String:===============");
	}
	
	private static void deser1() throws Exception {
		Person person1 = new Person();
		ByteBufferStream stream = UrosFormatter.serialize(person1);
		String str1 = new String(stream.asByteArray(), Charset.forName("UTF-8"));
		System.out.println(str1);
		Person person2 = UrosFormatter.deserialize(stream, Person.class);
		int a = 1;
	}
	
	private static void deser2() throws Exception {
		String string1 = new String("aaaa");
		ByteBufferStream stream = UrosFormatter.serialize(string1);
		String string2 = UrosFormatter.deserialize(stream, String.class);
		System.out.println(string2);
		int a = 1;
	}
}