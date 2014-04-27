package pico.sample;

import hi.pico.After;
import hi.pico.Before;
import hi.pico.MultipartConfig;
import hi.pico.WebController;
import hi.pico.WebMethod;
import hi.pico.view.Renderer;
import hi.pico.view.View;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebController
@MultipartConfig(location=Sample.UPLOAD_PATH, maxFileSize=1000*1024*1024)
public class Sample {
	public static final String UPLOAD_PATH = "c:/tmp";
	
	@Before
	public void onBefore(ServletContext context, HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setContentType("text/html; charset=utf-8");
		req.setCharacterEncoding("utf-8");
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
