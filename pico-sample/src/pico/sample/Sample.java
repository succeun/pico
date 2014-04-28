package pico.sample;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.After;
import pico.Before;
import pico.MultipartConfig;
import pico.WebController;
import pico.WebMethod;
import pico.view.Renderer;
import pico.view.View;

@WebController
@MultipartConfig(location=Sample.UPLOAD_PATH, maxFileSize=1000*1024*1024)
public class Sample {
	public static final String UPLOAD_PATH = "c:/tmp";
	
	@Before
	public void onBefore(ServletContext context, HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setContentType("text/html; charset=utf-8");
		req.setCharacterEncoding("utf-8");
		System.out.println("Before call.");
	}
	
	@After
	public void onAfter() {
		System.out.println("After call.");
	}
	
	@WebMethod
	public View sum(int x, int y) throws ServletException {
		return Renderer.text("x + y = " + (x + y));
	}
}
