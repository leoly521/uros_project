package test.helloworld;

import com.bamboocloud.uros.server.UrosTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloServer {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        UrosTcpServer server = new UrosTcpServer("tcp://0.0.0.0:4321");
        server.add("hello", TCPHelloServer.class);
        server.start();
        System.out.println("TCPHelloServer START");
        System.in.read();
        server.stop();
        System.out.println("TCPHelloServer STOP");
    }
}
