package hi.pico.view;


import hi.pico.ControllerContext;

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
		return new JspForward(null, path);
	}

	/**
	 * Layout을 이용하여, View로 forward 나타낸다.<br/>
	 * 사용방법: <!-- Body Start --> <jsp:include
	 * page="<%=RendererUtil.getContentsPath(request)%>"></jsp:include> <!--
	 * Body End -->
	 * 
	 * @param request
	 * @param response
	 * @param layoutPath
	 * @param path
	 * @throws ControllerException
	 */
	@Override
	public View forwardInternal(String layoutPath, String path) throws ServletException {
		if (layoutPath == null)
			throw new ServletException("Layout Path is null.");
		if (!layoutPath.startsWith("/"))
			throw new ServletException("LayoutPath(" + layoutPath
					+ ") is not abstract path. ex) '/layout/layout1.jsp'");
		if (!path.startsWith("/"))
			throw new ServletException("Path(" + path
					+ ") is not abstract path. ex) '/page/path1.jsp'");

		return new JspForward(layoutPath, path);
	}

	/**
	 * Layout을 이용하여, View로 forward 나타낸다.<br/>
	 * 사용방법: <!-- Body Start --> <jsp:include
	 * page="<%=RendererUtil.getContentsPath(request, 0)%>"></jsp:include> <!--
	 * Body End -->
	 * 
	 * @param request
	 * @param response
	 * @param layoutPath
	 * @param paths
	 * @throws ControllerException
	 */
	@Override
	public View forwardInternal(String layoutPath, String[] paths) throws ServletException {
		if (layoutPath == null)
			throw new ServletException("Layout Path is null.");
		if (!layoutPath.startsWith("/"))
			throw new ServletException("LayoutPath(" + layoutPath + ") must be abstract path. ex) '/layout/layout1.jsp'");
		for (String path : paths) {
			if (!path.startsWith("/"))
				throw new ServletException("Path(" + path + ") must be abstract path. ex) '/page/path1.jsp'");
		}

		return new JspForward(layoutPath, paths);
	}
	
	public class JspForward extends View {
		private String layoutPath;
		private String path;
		private String[] paths;

		public JspForward(String layoutPath, String path) {
			this.layoutPath = layoutPath;
			this.path = path;
		}
		
		public JspForward(String layoutPath, String[] paths) {
			this.layoutPath = layoutPath;
			this.paths = paths;
		}
		
		@Override
		public void renderInternal(ServletContext context, HttpServletRequest req,
				HttpServletResponse res, ControllerContext controllerContext) throws Exception {
			if (layoutPath != null && layoutPath.length() > 0) {
				if (path != null) {
					req.setAttribute(BODY_PATH, path);
				} else {
					req.setAttribute(BODY_PATH, paths);
				}
				RequestDispatcher dispatcher = req.getRequestDispatcher(layoutPath);
				dispatcher.forward(req, res);
			} else {
				RequestDispatcher dispatcher = req.getRequestDispatcher(path);
				dispatcher.forward(req, res);
			}
			
		}
		
	}
}
