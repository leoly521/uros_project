package test.http.server;

import com.bamboocloud.uros.server.UrosHttpMethods;
import com.bamboocloud.uros.server.UrosHttpService;
import com.bamboocloud.uros.server.HttpContext;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyUrosServlet2 extends HttpServlet {

	private static final long serialVersionUID = 6450816723275062452L;
	
	private final UrosHttpService service = new UrosHttpService();

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PojoB pojoB = new PojoB();
		UrosHttpMethods methods = new UrosHttpMethods();
		methods.addInstanceMethods(pojoB);
		methods.addInstanceMethods(pojoB, PojoA.class);
		this.service.setDebug(true);
		this.service.handle(new HttpContext(request, response, this.getServletConfig(), this.getServletContext()), methods);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}