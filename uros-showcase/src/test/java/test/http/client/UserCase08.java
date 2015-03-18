package test.http.client;

import com.bamboocloud.uros.client.UrosHttpClient;
import com.bamboocloud.uros.io.UrosClassManager;
import java.io.IOException;

public class UserCase08 {
    public static void main(String[] args) throws IOException {
        UrosClassManager.register(User.class, "User");
        UrosHttpClient client = new UrosHttpClient();
        client.useService("http://localhost:8080/uros-showcase/Methods");
        PoJoB b = client.useService(PoJoB.class, "b");
        User[] users1 = b.getUserList();
        for (User user : users1) {
        	System.out.printf(">>>users1:");
            System.out.printf("name: %s, ", user.getName());
            System.out.printf("age: %d, ", user.getAge());
            System.out.printf("sex: %s, ", user.getSex());
            System.out.printf("birthday: %s, ", user.getBirthday());
            System.out.printf("married: %s.", user.isMarried());
            System.out.println();
        }
        
        User[] users2 = b.getUserArray();
        for (User user : users2) {
        	System.out.printf(">>>users2:");
            System.out.printf("name: %s, ", user.getName());
            System.out.printf("age: %d, ", user.getAge());
            System.out.printf("sex: %s, ", user.getSex());
            System.out.printf("birthday: %s, ", user.getBirthday());
            System.out.printf("married: %s.", user.isMarried());
            System.out.println();
        }
        
        User[] users3 = b.getUserList();
        for (User user : users3) {
        	System.out.printf(">>>users3:");
            System.out.printf("name: %s, ", user.getName());
            System.out.printf("age: %d, ", user.getAge());
            System.out.printf("sex: %s, ", user.getSex());
            System.out.printf("birthday: %s, ", user.getBirthday());
            System.out.printf("married: %s.", user.isMarried());
            System.out.println();
        }
        
        User[] users4 = b.getUserArray();
        for (User user : users4) {
        	System.out.printf(">>>users4:");
            System.out.printf("name: %s, ", user.getName());
            System.out.printf("age: %d, ", user.getAge());
            System.out.printf("sex: %s, ", user.getSex());
            System.out.printf("birthday: %s, ", user.getBirthday());
            System.out.printf("married: %s.", user.isMarried());
            System.out.println();
        }
    }
}