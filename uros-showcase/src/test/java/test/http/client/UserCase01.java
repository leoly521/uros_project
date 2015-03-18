package test.http.client;

import com.bamboocloud.uros.client.UrosHttpClient;
import java.io.IOException;

public class UserCase01 {
    public static void main(String[] args) throws IOException {
    	UrosHttpClient client = new UrosHttpClient();
        client.useService("http://localhost:8080/uros-showcase/Methods");
        System.out.println(client.invoke("a_getId"));
        System.out.println(client.invoke("b_getId"));
    }
}
