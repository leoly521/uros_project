package test.http.server;

import com.bamboocloud.uros.server.UrosMethods;
import com.bamboocloud.uros.server.UrosServlet;

public class MyUrosServlet1 extends UrosServlet {

	private static final long serialVersionUID = -4686721237668842372L;

	@Override
	protected void setGlobalMethods(UrosMethods methods) {
		PojoB pojoB = new PojoB();
		methods.addMethod("getID", pojoB);
	}
}