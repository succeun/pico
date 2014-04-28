package pico.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;
import pico.Utility;

public class Html extends AbstractData {
	private String html;

	public Html(String html) {
		this.html = html;
	}
	
	public Html(Exception ex) {
		this.html = Utility.toStrackTraceStringHTML(ex);
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		write("text/html", html);
	}
}
