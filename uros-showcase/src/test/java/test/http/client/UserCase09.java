package test.http.client;

import com.bamboocloud.uros.client.UrosHttpClient;
import com.bamboocloud.uros.client.api.UrosCallback1;

import java.io.IOException;

public class UserCase09 {
    public static void main(String[] args) throws IOException {
        UrosHttpClient client = new UrosHttpClient();
        client.useService("http://localhost:8080/uros-showcase/Methods");
        client.invoke("ex1_getId", new UrosCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        }, String.class);
        client.invoke("ex2_getId", new UrosCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        }, String.class);
        client.close();
    }
}
