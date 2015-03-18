package test.http.client;

import com.bamboocloud.uros.client.UrosHttpClient;
import java.io.IOException;

public class UserCase05 {
    public static void main(String[] args) throws IOException {
        UrosHttpClient client = new UrosHttpClient();
        client.useService("http://localhost:8080/uros-showcase/Methods");
        PojoA exam1 = client.useService(PojoA.class, "ex1");
        PojoA exam2 = client.useService(PojoA.class, "ex2");
        System.out.println(exam1.getId());
        System.out.println(exam2.getId());
    }
}
