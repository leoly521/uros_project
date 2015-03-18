package test.http.client;

import com.bamboocloud.uros.client.UrosHttpClient;
import java.io.IOException;
import java.util.ArrayList;

public class UserCase02 {
    public static void main(String[] args) throws IOException {
        UrosHttpClient client = new UrosHttpClient();
        client.useService("http://localhost:8080/uros-showcase/Methods");
        System.out.println(client.invoke("ex1_sum", new Object[] { new int[] {1,2,3,4,5} }));
        System.out.println(client.invoke("ex1_sum", new Object[] { new short[] {6,7,8,9,10} }));
        System.out.println(client.invoke("ex1_sum", new Object[] { new long[] {11,12,13,14,15} }));
        System.out.println(client.invoke("ex1_sum", new Object[] { new double[] {16,17,18,19,20} }));
        System.out.println(client.invoke("ex1_sum", new Object[] { new String[] {"21","22","23","24","25"} }));
        ArrayList intList = new ArrayList();
        intList.add(26);
        intList.add(27);
        intList.add(28);
        intList.add(29);
        intList.add(30);
        System.out.println(client.invoke("ex2_sum", new Object[] { intList }, double.class));
    }
}
