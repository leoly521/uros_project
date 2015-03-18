package test.http.client;

import com.bamboocloud.uros.common.annotation.UrosMethodName;
import com.bamboocloud.uros.common.annotation.UrosSimpleMode;

public interface PoJoB {
    @UrosSimpleMode(true)
    User[] getUserList();
    @UrosMethodName("getUserList")
    @UrosSimpleMode(true)
    User[] getUserArray();
}
