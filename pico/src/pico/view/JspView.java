package pico.view;


import pico.ControllerContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @since 2010. 8. 13.
 */
public class JspView extends View {
	private String path;

	public JspView(String path) {
		this.path = path;
	}
	
	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		RequestDispatcher dispatcher = req.getRequestDispatcher(path);
		dispatcher.forward(req, res);
	}
}
