package test.helloworld;

import com.bamboocloud.uros.client.UrosTcpClient;
import com.bamboocloud.uros.client.api.UrosCallback1;

import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloClient {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("TCPHelloClient START");
        long start = System.currentTimeMillis();
        UrosTcpClient client = new UrosTcpClient("tcp://10.100.100.100:4321,tcp://localhost:4321");
        final int[] n = new int[1];
        for (int i = 0; i < 10000; i++) {
            client.invoke("hello", new Object[] {"World"});
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        start = System.currentTimeMillis();
        for (int i = 1; i <= 10000; i++) {
            client.invoke("hello", new Object[] {"World"}, new UrosCallback1() {
                @Override
                public void handler(Object result) {
                }
            });
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println("TCPHelloClient END");
        client.close();
    }
}
