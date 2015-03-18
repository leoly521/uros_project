package test.helloworld;

import com.bamboocloud.uros.client.UrosClient;
import com.bamboocloud.uros.client.api.UrosCallback1;
import com.bamboocloud.uros.common.annotation.UrosSimpleMode;

import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloClient2 {
    public interface IStub {
        @UrosSimpleMode(true)
        String Hello(String name);
        @UrosSimpleMode(true)
        void Hello(String name, UrosCallback1<String> callback);
    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        UrosClient client = UrosClient.create("tcp://127.0.0.1:4321/");
        IStub stub = client.useService(IStub.class);
        stub.Hello("Async World", new UrosCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        System.out.println(stub.Hello("World"));
        client.close();
    }
}
