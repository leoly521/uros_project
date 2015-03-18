package test.http.client;

import com.bamboocloud.uros.common.annotation.UrosSimpleMode;

import java.util.List;
import java.util.Map;

public interface PojoA {
    @UrosSimpleMode(true)
    String getId();
    @UrosSimpleMode(true)
    int sum(int[] nums);
    @UrosSimpleMode(true)
    int sum(short[] nums);
    @UrosSimpleMode(true)
    int sum(long[] nums);
    @UrosSimpleMode(true)
    int sum(double[] nums);
    @UrosSimpleMode(true)
    int sum(String[] nums);
    @UrosSimpleMode(true)
    double sum(List nums);
    @UrosSimpleMode(true)
    Map<String, String> swapKeyAndValue(Map<String, String> strmap);
}
