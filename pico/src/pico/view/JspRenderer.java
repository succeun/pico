package pico.view;


import pico.ControllerContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Eun Jeong-Ho, silver@intos.biz
 * @since 2010. 8. 13.
 */
public class JspRenderer extends Renderer {
	/**
	 * View로 forward 나타낸다.
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @throws ControllerException
	 */
	@Override
	public View forwardInternal(String path) throws ServletException {
		return new JspForward(path);
	}

	public class JspForward extends View {
		private String path;
		private String[] paths;

		public JspForward(String path) {
			this.path = path;
		}
		
		@Override
		public void renderInternal(ServletContext context, HttpServletRequest req,
				HttpServletResponse res, ControllerContext controllerContext) throws Exception {
			RequestDispatcher dispatcher = req.getRequestDispatcher(path);
			dispatcher.forward(req, res);
		}
		
	}
}
